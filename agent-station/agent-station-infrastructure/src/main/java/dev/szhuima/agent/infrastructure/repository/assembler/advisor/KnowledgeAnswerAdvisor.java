package dev.szhuima.agent.infrastructure.repository.assembler.advisor;

import dev.szhuima.agent.domain.support.utils.StringTemplateRender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class KnowledgeAnswerAdvisor implements BaseAdvisor, StringTemplateRender {

    private final Long knowledgeId;
    private final VectorStore vectorStore;
    private final SearchRequest searchRequest;
    private final String userTextAdvise;

    public KnowledgeAnswerAdvisor(Long knowledgeId, VectorStore vectorStore, SearchRequest searchRequest) {
        this.knowledgeId = knowledgeId;
        this.vectorStore = vectorStore;
        this.searchRequest = searchRequest;
        this.userTextAdvise = """
                下面是对回复用户问题可能有用的上下文信息:
                ---------------------
                {{question_answer_context}}
                ---------------------
                根据上下文信息回复用户的问题。
                """;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String userText = chatClientRequest.prompt().getUserMessage().getText();
        String advisedUserText = "用户问题: " + userText;

        SearchRequest searchRequestToUse = SearchRequest.from(this.searchRequest)
                .query(userText)
                .filterExpression("knowledge_id === " + this.knowledgeId)
                .build();
        List<Document> documents = this.vectorStore.similaritySearch(searchRequestToUse);

        if (documents.isEmpty()) {
            log.debug("没有文档，不加任何上下文，直接返回原始请求");
            return chatClientRequest;
        }
        advisedUserText += System.lineSeparator() + userTextAdvise;

        String documentContext = documents.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));

        log.debug("用户问题:{},召回文档数: {}, 召回内容:{}", userText, documents.size(), documentContext);

        Map<String, String> questionAnswerContext = Map.of("question_answer_context", documentContext);

        advisedUserText = this.render(advisedUserText, questionAnswerContext);

        return ChatClientRequest.builder()
                .prompt(Prompt.builder().messages(new UserMessage(advisedUserText)).build())
                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        ChatResponse.Builder chatResponseBuilder = ChatResponse.builder().from(chatClientResponse.chatResponse());
        chatResponseBuilder.metadata("qa_retrieved_documents", chatClientResponse.context().get("qa_retrieved_documents"));
        ChatResponse chatResponse = chatResponseBuilder.build();

        return ChatClientResponse.builder()
                .chatResponse(chatResponse)
                .context(chatClientResponse.context())
                .build();
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(this.before(chatClientRequest, callAdvisorChain));
        return this.after(chatClientResponse, callAdvisorChain);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        return BaseAdvisor.super.adviseStream(chatClientRequest, streamAdvisorChain);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    protected Filter.Expression doGetFilterExpression(Map<String, Object> context) {
        Object filterExprObj = context.get("qa_filter_expression");
        String filterExpr = filterExprObj != null ? filterExprObj.toString() : null;

        if (StringUtils.hasText(filterExpr)) {
            return new FilterExpressionTextParser().parse(filterExpr);
        }
        return this.searchRequest.getFilterExpression();
    }

}

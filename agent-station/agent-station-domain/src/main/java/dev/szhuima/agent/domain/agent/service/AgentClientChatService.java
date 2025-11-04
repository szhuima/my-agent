package dev.szhuima.agent.domain.agent.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import dev.szhuima.agent.domain.agent.AgentClient;
import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
import dev.szhuima.agent.domain.agent.model.entity.ChatRequest;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientModelVO;
import dev.szhuima.agent.domain.agent.model.valobj.Knowledge;
import dev.szhuima.agent.domain.agent.repository.IAgentRepository;
import dev.szhuima.agent.domain.agent.repository.IClientModelRepository;
import dev.szhuima.agent.domain.agent.service.config.factory.AgentBeanFactory;
import dev.szhuima.agent.domain.support.chain.HandlerChain;
import dev.szhuima.agent.domain.support.utils.StringTemplateRender;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;


/**
 * * @Author: szhuima
 * * @Date    2025/10/16 13:03
 * * @Description
 **/
@Slf4j
@Service
public class AgentClientChatService implements StringTemplateRender {

    @Resource
    private AgentBeanFactory agentBeanFactory;

    @Resource
    private PgVectorStore vectorStore;

    @Resource
    private IAgentRepository repository;

    @Resource
    private IClientModelRepository modelRepository;


    @Value("${agent-station.rag.query-rewrite-model-id}")
    private Long queryRewriteModelId;

    @Resource
    @Qualifier("agentAssemblyChain")
    private HandlerChain<AgentAssemblyInput, Void> agentAssemblyChain;


    public synchronized ChatClient getChatClient(Long clientId) {
        ChatClient chatClient = agentBeanFactory.getChatClient(clientId);
        if (chatClient == null) {
            agentAssemblyChain.handleWithEmptyCtx(AgentAssemblyInput.builder().clientIdList(List.of(clientId)).build());
        }
        return agentBeanFactory.getChatClient(clientId);
    }

    public AgentClient getAgentClient(Long clientId) {
        List<AgentClient> agentClients = repository.queryAgentClient(List.of(clientId));
        if (agentClients.isEmpty()) {
            log.error("未找到客户端配置，clientId：{}", clientId);
            throw new IllegalArgumentException("未找到客户端配置");
        }
        return agentClients.get(0);
    }


    public String noneStreamChat(ChatRequest chatRequest) {
        AgentClient agentClient = getAgentClient(chatRequest.getClientId());
        ChatClient chatClient = getChatClient(chatRequest.getClientId());
        ChatClient.ChatClientRequestSpec spec = chatClient.prompt().user(chatRequest.getUserMessage());
        // 系统提示词渲染
        if (StrUtil.isNotEmpty(agentClient.getSystemPrompt())) {
            String systemPrompt = agentClient.getSystemPrompt();
            if (MapUtil.isNotEmpty(chatRequest.getContext())) {
                systemPrompt = render(agentClient.getSystemPrompt(), chatRequest.getContext());
            }
            spec.system(systemPrompt);
        }
        // 设置对话记忆
        if (StrUtil.isNotEmpty(chatRequest.getSessionId())) {
            spec.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatRequest.getSessionId()));
        }
        ChatClient.CallResponseSpec responseSpec = spec.call();
        String response = responseSpec.content();
        return response;
    }


    public Flux<String> streamChat(ChatRequest chatRequest) {
        AgentClient agentClient = getAgentClient(chatRequest.getClientId());
        ChatClient chatClient = getChatClient(chatRequest.getClientId());
        ChatClient.ChatClientRequestSpec spec = chatClient.prompt();
        // 用户提示词渲染
        if (StrUtil.isNotEmpty(chatRequest.getUserMessage())) {
            String userPrompt = chatRequest.getUserMessage();
            if (MapUtil.isNotEmpty(chatRequest.getContext())) {
                userPrompt = render(chatRequest.getUserMessage(), chatRequest.getContext());
            }
            spec.user(userPrompt);
        }
        // 系统提示词渲染
        if (StrUtil.isNotEmpty(agentClient.getSystemPrompt())) {
            String systemPrompt = agentClient.getSystemPrompt();
            if (MapUtil.isNotEmpty(chatRequest.getContext())) {
                systemPrompt = render(agentClient.getSystemPrompt(), chatRequest.getContext());
            }
            spec.system(systemPrompt);
        }
        // 设置对话记忆
        if (StrUtil.isNotEmpty(chatRequest.getSessionId())) {
            spec.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatRequest.getSessionId()));
        }
        Flux<String> content = spec.stream().content();
        return content;
    }

    public String simpleChat(Long clientId, String userPrompt) {
        String sessionId = IdUtil.simpleUUID(); // 这里必须指定一个新的sessionId，如果不指定，会导致对话记忆被不同的请求共享。
        ChatClient chatClient = getChatClient(clientId);
        String response = chatClient.prompt(userPrompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call().content();
        return response;
    }

    public String memoryChat(Long clientId, String userPrompt, String sessionId) {
        ChatClient chatClient = getChatClient(clientId);
        String response = chatClient.prompt(userPrompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call().content();
        return response;
    }

    /**
     * 知识库文档检索文档，不支持对话记忆，从指定文档中进行回复
     * 都会先查知识库，然后在调用大模型
     *
     * @param clientId
     * @param userPrompt
     * @param ragId
     * @return
     */
    public String ragChat(Long clientId, String userPrompt, Long ragId) {
        String sessionId = IdUtil.simpleUUID();

        Knowledge knowledge = repository.queryKnowledge(ragId);
        ChatClient chatClient = getChatClient(clientId);

        SearchRequest request = SearchRequest.builder()
                .query(userPrompt)
                .topK(5)
                .filterExpression("knowledge =='" + knowledge.getKnowledgeTag() + "'")
                .build();

        List<Document> documents = vectorStore.similaritySearch(request);
        String documentCollectors = documents.stream().map(Document::getFormattedContent).collect(Collectors.joining("\n\n"));

        log.info("召回知识库文档chunkSize：{}", documents.size());

        String response = chatClient.prompt()
                .system("你是一名专业的知识问答助手，请根据提供的文档回答问题。若无信息，请说不知道。回答必须用中文。")
                .user("以下是相关文档：\n" + documentCollectors + "\n\n用户问题：" + userPrompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call().content();

        return response;

    }

    /**
     * 知识库文档检索文档优化改进版v1，不支持对话记忆，从指定文档中进行回复
     * 改进方案：
     * 1、对用户问题进行改写为更适合检索的表达。
     *
     * @param clientId
     * @param userPrompt
     * @param ragId
     * @return
     */
    public String ragChatWithRewrite(Long clientId, String userPrompt, Long ragId) {
        String sessionId = IdUtil.simpleUUID();

        AiClientModelVO modelVO = modelRepository.getClientModelById(queryRewriteModelId);
        if (modelVO == null) throw new IllegalArgumentException("未找到查询重写模型,id=" + queryRewriteModelId);

        ChatModel chatModel = agentBeanFactory.createChatModel(modelVO);

        String rewritePrompt = """
                你是一个知识库查询助手，你的任务是将用户提出的问题改写为最适合知识库检索的查询。 
                要求： 
                1. 保留用户原意。 
                2. 直接输出简短的检索查询，不要回答问题，不要解释。 
                3. 如果文档包含多种语言，请统一输出中文。 
                4. 尽量包含关键术语和关键词。 用户问题：%s
                 改写后的查询：
                """;

        String finalRewritePrompt = String.format(rewritePrompt, userPrompt);

        String rewriteResult = chatModel.call(finalRewritePrompt);

        log.info("RAG问答,用户问题:{} 被改写为:{}", userPrompt, rewriteResult);

        Knowledge knowledge = repository.queryKnowledge(ragId);
        ChatClient chatClient = getChatClient(clientId);

        SearchRequest request = SearchRequest.builder()
                .query(userPrompt)
                .topK(3)
                .filterExpression("knowledge =='" + knowledge.getKnowledgeTag() + "'")
                .build();

        List<Document> documents = vectorStore.similaritySearch(request);
        String documentCollectors = documents.stream().map(Document::getFormattedContent).collect(Collectors.joining("\n\n"));

        log.info("召回知识库文档chunkSize：{}", documents.size());

        String response = chatClient.prompt()
                .system("你是一名专业的知识问答助手，请根据提供的文档回答问题。若无信息，请说不知道。回答必须用中文。")
                .user("以下是相关文档：\n" + documentCollectors + "\n\n用户问题：" + rewriteResult)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call().content();

        return response;

    }


    /**
     * 知识库文档检索文档优化改进版v1，不支持对话记忆，从指定文档中进行回复
     * 改进方案：
     * 1、使用Reranker模型 计算每个文档和问题的匹配度；只保留 Top-K ；高分文档再用 LLM 对文档进行摘要或段落提纯，去掉无关信息。
     *
     * @return
     */
    public String ragChatWithReranker(Long clientId, String userPrompt, Long ragId) {
        return null;
    }


    public void clearMemory(Long clientId, String sessionId) {
        ChatMemory chatMemory = agentBeanFactory.getChatMemory(clientId);
        if (chatMemory != null) {
            chatMemory.clear(sessionId);
        }
    }
}

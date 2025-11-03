package dev.szhuima.agent.domain.agent.service.config.handler;

import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientAdvisorVO;
import dev.szhuima.agent.domain.agent.model.valobj.enums.AdvisorType;
import dev.szhuima.agent.domain.agent.service.config.AbstractAgentAssembler;
import dev.szhuima.agent.domain.agent.service.config.factory.advisor.KnowledgeAnswerAdvisor;
import dev.szhuima.agent.domain.support.chain.ChainContext;
import dev.szhuima.agent.domain.support.chain.HandleResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/9/11 23:27
 * * @Description
 **/
@Slf4j
@Component
public class AdvisorAssembler extends AbstractAgentAssembler {

    @Resource
    private VectorStore vectorStore;

    @Override
    public HandleResult<Void> doHandle(ChainContext ctx, AgentAssemblyInput param) {
        List<AiClientAdvisorVO> aiClientAdvisorList = ctx.getValue(AGENT_CLIENT_ADVISORS_KEY);
        if (aiClientAdvisorList == null || aiClientAdvisorList.isEmpty()) {
            log.warn("没有可用的AI客户端顾问（advisor）配置");
            return proceed(ctx, param, HandleResult.keepGoing());
        }

        for (AiClientAdvisorVO aiClientAdvisorVO : aiClientAdvisorList) {
            // 构建顾问访问对象
            Advisor advisor = createAdvisor(aiClientAdvisorVO);
            // 注册Bean对象
            agentBeanFactory.registerAdvisor(aiClientAdvisorVO.getId(), advisor);
        }
        return proceed(ctx, param, HandleResult.keepGoing());
    }

    private Advisor createAdvisor(AiClientAdvisorVO aiClientAdvisorVO) {
        String advisorType = aiClientAdvisorVO.getAdvisorType();
        AdvisorType advisorT = AdvisorType.valueOf(advisorType);
        switch (advisorT) {
            case CHAT_MEMORY -> {
                AiClientAdvisorVO.ChatMemory chatMemory = aiClientAdvisorVO.getChatMemory();
                return PromptChatMemoryAdvisor.builder(MessageWindowChatMemory.builder()
                        .maxMessages(chatMemory.getMaxMessages())
                        .build()).build();
            }
            case RAG_ANSWER -> {
                AiClientAdvisorVO.RagAnswer ragAnswer = aiClientAdvisorVO.getRagAnswer();
                return new KnowledgeAnswerAdvisor(1L, vectorStore, SearchRequest.builder()
                        .topK(ragAnswer.getTopK())
                        .filterExpression(ragAnswer.getFilterExpression())
                        .build());
            }
        }
        throw new RuntimeException("err! advisorType " + advisorType + " not exist!");
    }
}

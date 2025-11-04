package dev.szhuima.agent.domain.agent.service;

import dev.szhuima.agent.domain.agent.Agent;
import dev.szhuima.agent.domain.agent.factory.AgentBeanFactory;
import dev.szhuima.agent.domain.agent.model.AgentExecuteParams;
import dev.szhuima.agent.domain.agent.model.ChatRequest;
import dev.szhuima.agent.domain.agent.repository.IAgentRepository;
import dev.szhuima.agent.domain.agent.repository.IClientModelRepository;
import dev.szhuima.agent.domain.knowledge.repository.IKnowledgeRepository;
import dev.szhuima.agent.domain.support.utils.StringTemplateRender;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;


/**
 * * @Author: szhuima
 * * @Date    2025/10/16 13:03
 * * @Description
 **/
@Slf4j
@Service
public class AgentChatService implements StringTemplateRender {

    @Resource
    private AgentBeanFactory agentBeanFactory;

    @Resource
    private PgVectorStore vectorStore;

    @Resource
    private IAgentRepository repository;

    @Resource
    private IKnowledgeRepository knowledgeRepository;

    @Resource
    private IClientModelRepository modelRepository;

    @Resource
    private AgentExecutor agentExecutor;


    public Agent getAgentClient(Long clientId) {
        List<Agent> agents = repository.queryAgentList(List.of(clientId));
        if (agents.isEmpty()) {
            log.error("未找到客户端配置，clientId：{}", clientId);
            throw new IllegalArgumentException("未找到客户端配置");
        }
        return agents.get(0);
    }


    public String noneStreamChat(ChatRequest chatRequest) {
        Agent agent = getAgentClient(chatRequest.getClientId());
        AgentExecuteParams params = AgentExecuteParams.builder()
                .agent(agent)
                .userMessage(chatRequest.getUserMessage())
                .context(chatRequest.getContext())
                .conversationId(chatRequest.getSessionId())
                .streaming(chatRequest.isStreaming())
                .build();

        ChatResponse chatResponse = agentExecutor.executeTask(params, ChatResponse.class);
        return chatResponse.getResult().getOutput().getText();
    }

    public Flux<String> streamChat(ChatRequest chatRequest) {
        Agent agent = getAgentClient(chatRequest.getClientId());
        AgentExecuteParams params = AgentExecuteParams.builder()
                .agent(agent)
                .userMessage(chatRequest.getUserMessage())
                .context(chatRequest.getContext())
                .conversationId(chatRequest.getSessionId())
                .streaming(chatRequest.isStreaming())
                .build();

        Flux<String> flux = agentExecutor.executeTask(params, Flux.class);
        return flux;
    }


    public void clearMemory(Long clientId, String sessionId) {
        ChatMemory chatMemory = agentBeanFactory.getChatMemory(clientId);
        if (chatMemory != null) {
            chatMemory.clear(sessionId);
        }
    }
}

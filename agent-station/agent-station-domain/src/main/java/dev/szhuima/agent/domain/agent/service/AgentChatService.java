package dev.szhuima.agent.domain.agent.service;

import dev.szhuima.agent.domain.agent.Agent;
import dev.szhuima.agent.domain.agent.factory.AgentBeanFactory;
import dev.szhuima.agent.domain.agent.model.AgentExecuteParams;
import dev.szhuima.agent.domain.agent.model.ChatRequest;
import dev.szhuima.agent.domain.agent.repository.IAgentRepository;
import dev.szhuima.agent.domain.support.exception.BizException;
import dev.szhuima.agent.domain.support.utils.StringTemplateRender;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


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
    private IAgentRepository agentRepository;

    @Resource
    private AgentExecutor agentExecutor;


    public Agent getAgent(Long agentId) {
        Agent agent = agentRepository.getAgent(agentId);
        if (agent == null) {
            throw BizException.of("智能体不存在");
        }
        return agent;
    }


    public String noneStreamChat(ChatRequest chatRequest) {
        Agent agent = getAgent(chatRequest.getClientId());
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
        Agent agent = getAgent(chatRequest.getClientId());
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

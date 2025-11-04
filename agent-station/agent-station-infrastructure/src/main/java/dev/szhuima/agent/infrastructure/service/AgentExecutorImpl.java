package dev.szhuima.agent.infrastructure.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import dev.szhuima.agent.domain.agent.Agent;
import dev.szhuima.agent.domain.agent.model.AgentExecuteParams;
import dev.szhuima.agent.domain.agent.service.AgentExecutor;
import dev.szhuima.agent.infrastructure.factory.ChatClientFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AgentExecutorImpl implements AgentExecutor {

    private final ChatClientFactory chatClientFactory;

    public AgentExecutorImpl(ChatClientFactory chatClientFactory) {
        this.chatClientFactory = chatClientFactory;
    }

    @Override
    public <T> T executeTask(AgentExecuteParams params, Class<T> returnType) {
        Agent agent = params.getAgent();
        ChatClient chatClient = chatClientFactory.getOrCreate(agent);
        ChatClient.ChatClientRequestSpec spec = chatClient.prompt();

        // 用户提示词渲染
        if (StrUtil.isNotEmpty(params.getUserMessage())) {
            spec.user(render(params.getUserMessage(), params.getContext()));
        }
        // 系统提示词渲染
        if (StrUtil.isNotEmpty(agent.getSystemPrompt())) {
            String systemPrompt = agent.getSystemPrompt();
            if (MapUtil.isNotEmpty(params.getContext())) {
                systemPrompt = render(agent.getSystemPrompt(), params.getContext());
            }
            spec.system(systemPrompt);
        }
        // 设置对话记忆
        if (StrUtil.isNotEmpty(params.getConversationId())) {
            spec.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, params.getConversationId()));
        }

        if (params.isStreaming() && returnType.equals(Flux.class)) {
            return (T) spec.stream().content();
        }

        return (T) spec.call().chatResponse();
    }
}
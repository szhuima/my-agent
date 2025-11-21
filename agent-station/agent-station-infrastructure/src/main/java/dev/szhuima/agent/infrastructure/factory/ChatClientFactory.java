package dev.szhuima.agent.infrastructure.factory;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.cloud.ai.memory.redis.RedissonRedisChatMemoryRepository;
import dev.szhuima.agent.domain.agent.Agent;
import dev.szhuima.agent.domain.agent.model.Knowledge;
import dev.szhuima.agent.domain.support.utils.StringTemplateRender;
import dev.szhuima.agent.infrastructure.repository.assembler.advisor.KnowledgeAnswerAdvisor;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatClientFactory implements StringTemplateRender {

    @Resource
    private PgVectorStore vectorStore;

    @Resource
    private RedissonRedisChatMemoryRepository redisChatMemoryRepository;

    @Resource
    private ChatModelFactory chatModelFactory;

    /**
     * 缓存对话客户端
     * 缓存对话客户端，key为agentId value为对话客户端
     */
    private final Map<Long, ChatClient> clientMap = new ConcurrentHashMap<>();

    private final Map<Long, ChatMemory> chatMemoryMap = new ConcurrentHashMap<>();

    public ChatClient getOrCreate(Agent agent) {
        return clientMap.computeIfAbsent(agent.getId(), (agentId) -> createChatClient(agent));
    }

    private ChatClient createChatClient(Agent agent) {
        ChatModel chatModel = chatModelFactory.createChatModel(agent.getModelApi());
        List<Advisor> advisors = new ArrayList<>();

        //配置记忆顾问
        if (agent.getMemorySize() > 0) {
            ChatMemory chatMemory = MessageWindowChatMemory.builder()
                    .chatMemoryRepository(redisChatMemoryRepository)
                    .maxMessages(agent.getMemorySize()).build();
            chatMemoryMap.put(agent.getId(), chatMemory);
            PromptChatMemoryAdvisor memoryAdvisor = PromptChatMemoryAdvisor.builder(chatMemory).build();
            advisors.add(memoryAdvisor);
        }

        // 配置RAG顾问
        List<Knowledge> knowledgeList = agent.getKnowledgeList();
        if (CollectionUtil.isNotEmpty(knowledgeList)) {
            for (Knowledge knowledge : knowledgeList) {
                KnowledgeAnswerAdvisor answerAdvisor = new KnowledgeAnswerAdvisor(knowledge.getId(), vectorStore, SearchRequest.builder()
                        .topK(knowledge.getTopK())
                        .build());
                advisors.add(answerAdvisor);
            }
        }

        advisors.add(new SimpleLoggerAdvisor());
        Advisor[] advisorArray = advisors.toArray(new Advisor[]{});

        // 5. 构建对话客户端
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        ChatClient chatClient = builder
//                    .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients.toArray(new McpSyncClient[]{})))
                .defaultAdvisors(advisorArray)
                .build();

        return chatClient;

    }


    public ChatMemory getChatMemory(Long agentId) {
        return chatMemoryMap.get(agentId);
    }

}

package dev.szhuima.agent.infrastructure.repository;

import dev.szhuima.agent.domain.agent.Agent;
import dev.szhuima.agent.domain.agent.model.Knowledge;
import dev.szhuima.agent.domain.agent.model.Mcp;
import dev.szhuima.agent.domain.agent.model.ModelApi;
import dev.szhuima.agent.domain.agent.repository.IAgentRepository;
import dev.szhuima.agent.domain.knowledge.repository.IKnowledgeRepository;
import dev.szhuima.agent.infrastructure.entity.TbAgent;
import dev.szhuima.agent.infrastructure.factory.ChatClientFactory;
import dev.szhuima.agent.infrastructure.mapper.AgentMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/9/11 22:33
 * * @Description
 **/
@Slf4j
@Repository
public class AgentRepository implements IAgentRepository {

    @Resource
    private AgentMapper agentMapper;

    @Resource
    private ModelApiRepository modelApiRepository;

    @Resource
    private McpRepository mcpRepository;

    @Resource
    private IKnowledgeRepository knowledgeRepository;

    @Resource
    private ChatClientFactory chatClientFactory;



    @Override
    public Agent getAgent(Long agentId) {
        TbAgent tbAgent = agentMapper.selectById(agentId);
        if (tbAgent == null) {
            return null;
        }
        ModelApi modelApi = modelApiRepository.getModelApi(tbAgent.getModelId());
        Agent agent = Agent.builder()
                .id(agentId)
                .systemPrompt(tbAgent.getSystemPrompt())
                .modelApi(modelApi)
                .memorySize(tbAgent.getMemorySize())
                .build();

        // 设置知识库ID列表
        List<Knowledge> knowledgeList = knowledgeRepository.queryByAgentId(agentId);
        agent.setKnowledgeList(knowledgeList);

        // 设置MCP
        List<Mcp> mcpList = mcpRepository.getMcpList(agentId);
        agent.setMcpList(mcpList);

        return agent;
    }

    @Override
    public void clearMemory(Long agentId, String sessionId) {
        ChatMemory chatMemory = chatClientFactory.getChatMemory(agentId);
        if (chatMemory == null) {
            return;
        }
        chatMemory.clear(sessionId);
    }
}



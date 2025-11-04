package dev.szhuima.agent.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import dev.szhuima.agent.domain.agent.Agent;
import dev.szhuima.agent.domain.agent.model.Knowledge;
import dev.szhuima.agent.domain.agent.model.ModelApi;
import dev.szhuima.agent.domain.agent.repository.IAgentRepository;
import dev.szhuima.agent.domain.knowledge.repository.IKnowledgeRepository;
import dev.szhuima.agent.infrastructure.entity.TbAgent;
import dev.szhuima.agent.infrastructure.entity.TbAgentKnowledgeConfig;
import dev.szhuima.agent.infrastructure.entity.TbModelApi;
import dev.szhuima.agent.infrastructure.mapper.AgentKnowledgeConfigMapper;
import dev.szhuima.agent.infrastructure.mapper.AgentMapper;
import dev.szhuima.agent.infrastructure.mapper.ModelApiMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private ModelApiMapper modelApiMapper;

    @Resource
    private IKnowledgeRepository knowledgeRepository;

    @Resource
    private AgentKnowledgeConfigMapper agentKnowledgeConfigMapper;


    @Override
    public List<Agent> queryAgentList(List<Long> agentIdList) {
        if (null == agentIdList || agentIdList.isEmpty()) {
            return Collections.emptyList();
        }
        List<TbAgent> tbAgents = agentMapper.selectBatchIds(agentIdList);

        // 查询知识库配置
        List<TbAgentKnowledgeConfig> knowledgeConfigs = agentKnowledgeConfigMapper.selectList(Wrappers.lambdaQuery(TbAgentKnowledgeConfig.class)
                .in(TbAgentKnowledgeConfig::getClientId, agentIdList));
        Map<Long, List<TbAgentKnowledgeConfig>> knowledgeConfigMap = knowledgeConfigs.stream()
                .collect(Collectors.groupingBy(TbAgentKnowledgeConfig::getClientId));

        List<Agent> result = new ArrayList<>();
        for (TbAgent client : tbAgents) {
            TbModelApi tbModelApi = modelApiMapper.selectById(client.getModelId());
            ModelApi modelApi = BeanUtil.copyProperties(tbModelApi, ModelApi.class);
            Long clientId = client.getId();
            Agent agentClient = Agent.builder()
                    .id(clientId)
                    .systemPrompt(client.getSystemPrompt())
                    .modelApi(modelApi)
                    .memorySize(client.getMemorySize())
                    .build();

            // 设置知识库ID列表
            if (knowledgeConfigMap.containsKey(clientId)) {
                List<Long> knowledgeIdList = knowledgeConfigMap.get(clientId).stream()
                        .map(TbAgentKnowledgeConfig::getKnowledgeId)
                        .collect(Collectors.toList());

                List<Knowledge> knowledgeList = knowledgeRepository.queryKnowledgeList(knowledgeIdList);
                agentClient.setKnowledgeList(knowledgeList);
            }
            result.add(agentClient);
        }
        return result;
    }
}

package dev.szhuima.agent.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.szhuima.agent.domain.agent.AgentClient;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientAdvisorVO;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientModelVO;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientToolMcpVO;
import dev.szhuima.agent.domain.agent.model.valobj.Knowledge;
import dev.szhuima.agent.domain.agent.model.valobj.enums.AdvisorType;
import dev.szhuima.agent.domain.agent.repository.IAgentRepository;
import dev.szhuima.agent.infrastructure.mapper.*;
import dev.szhuima.agent.infrastructure.po.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
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
    private AiClientMapper aiClientMapper;

    @Resource
    private AiClientModelMapper aiClientModelMapper;

    @Resource
    private AiClientToolMcpMapper aiClientToolMcpDao;

    @Resource
    private AiClientToolConfigMapper aiClientToolConfigDao;

    @Resource
    private AiClientAdvisorMapper aiClientAdvisorDao;

    @Resource
    private AiClientAdvisorConfigMapper aiClientAdvisorConfigDao;

    @Resource
    private AiKnowledgeMapper knowledgeMapper;

    @Resource
    private AiClientKnowledgeConfigMapper aiClientKnowledgeConfigMapper;

    @Override
    public List<AiClientModelVO> queryAiClientModelVOListByClientIds(List<Long> clientIdList) {
        // 根据客户端ID列表查询模型配置
        LambdaQueryWrapper<AiClient> clientWrapper = Wrappers.lambdaQuery(AiClient.class)
                .in(CollectionUtil.isNotEmpty(clientIdList), AiClient::getId, clientIdList)
                .select(AiClient::getModelId);
        List<Long> modelIds = aiClientMapper.selectList(clientWrapper).stream().map(AiClient::getModelId).distinct().toList();

        if (CollectionUtils.isEmpty(modelIds)) return new ArrayList<>();

        LambdaQueryWrapper<AiClientModel> modelWrapper = Wrappers.lambdaQuery(AiClientModel.class)
                .in(CollectionUtil.isNotEmpty(modelIds), AiClientModel::getId, modelIds);
        List<AiClientModel> aiClientModels = aiClientModelMapper.selectList(modelWrapper);
        if (null == aiClientModels || aiClientModels.isEmpty()) return new ArrayList<>();

        // 将PO对象转换为VO对象
        List<AiClientModelVO> aiClientModelVOList = new ArrayList<>();
        for (AiClientModel aiClientModel : aiClientModels) {
            AiClientModelVO vo = new AiClientModelVO();
            vo.setId(aiClientModel.getId());
            vo.setModelName(aiClientModel.getModelApiName());
            vo.setBaseUrl(aiClientModel.getBaseUrl());
            vo.setApiKey(aiClientModel.getApiKey());
            vo.setCompletionsPath(aiClientModel.getCompletionsPath());
            vo.setEmbeddingsPath(aiClientModel.getEmbeddingsPath());
            vo.setModelType(aiClientModel.getModelType());
            vo.setModelSource(aiClientModel.getModelSource());
            vo.setModelVersion(aiClientModel.getModelName());
            vo.setTimeout(aiClientModel.getTimeout());
            aiClientModelVOList.add(vo);
        }

        return aiClientModelVOList;
    }

    @Override
    public List<AiClientToolMcpVO> queryAiClientToolMcpVOListByClientIds(List<Long> clientIdList) {
        List<AiClientToolMcp> aiClientToolMcps = aiClientToolMcpDao.queryMcpConfigByClientIds(clientIdList);

        // 将PO对象转换为VO对象
        List<AiClientToolMcpVO> aiClientToolMcpVOList = new ArrayList<>();
        if (null == aiClientToolMcps || aiClientToolMcps.isEmpty()) return aiClientToolMcpVOList;
        for (AiClientToolMcp aiClientToolMcp : aiClientToolMcps) {
            AiClientToolMcpVO vo = new AiClientToolMcpVO();
            vo.setId(aiClientToolMcp.getId());
            vo.setMcpName(aiClientToolMcp.getMcpName());
            vo.setTransportType(aiClientToolMcp.getTransportType());
            vo.setRequestTimeout(aiClientToolMcp.getRequestTimeout());

            // 根据传输类型解析JSON配置
            String transportType = aiClientToolMcp.getTransportType();
            String transportConfig = aiClientToolMcp.getTransportConfig();

            try {
                if ("sse".equals(transportType)) {
                    // 解析SSE配置
                    ObjectMapper objectMapper = new ObjectMapper();
                    AiClientToolMcpVO.TransportConfigSse sseConfig = objectMapper.readValue(transportConfig, AiClientToolMcpVO.TransportConfigSse.class);
                    vo.setTransportConfigSse(sseConfig);
                } else if ("stdio".equals(transportType)) {
                    // 解析STDIO配置
                    Map<String, AiClientToolMcpVO.TransportConfigStdio.Stdio> stdio = JSON.parseObject(transportConfig,
                            new com.alibaba.fastjson.TypeReference<>() {
                            });
                    AiClientToolMcpVO.TransportConfigStdio stdioConfig = new AiClientToolMcpVO.TransportConfigStdio();
                    stdioConfig.setStdio(stdio);

                    vo.setTransportConfigStdio(stdioConfig);
                }
            } catch (Exception e) {
                log.error("解析传输配置失败: {}", e.getMessage(), e);
            }
            aiClientToolMcpVOList.add(vo);
        }

        return aiClientToolMcpVOList;
    }

    @Override
    public List<AiClientAdvisorVO> queryAdvisorConfigByClientIds(List<Long> clientIdList) {
        List<AiClientAdvisor> aiClientAdvisors = aiClientAdvisorDao.queryAdvisorConfigByClientIds(clientIdList);

        if (null == aiClientAdvisors || aiClientAdvisors.isEmpty()) return Collections.emptyList();

        return aiClientAdvisors.stream().map(advisor -> {
            AiClientAdvisorVO vo = AiClientAdvisorVO.builder()
                    .id(advisor.getId())
                    .advisorName(advisor.getAdvisorName())
                    .advisorType(advisor.getAdvisorType())
                    .orderNum(advisor.getOrderNum())
                    .build();

            // 根据 advisorType 类型转换 extParam
            if (StringUtils.isNotEmpty(advisor.getExtParam())) {
                try {
                    if (AdvisorType.CHAT_MEMORY.name().equals(advisor.getAdvisorType())) {
                        AiClientAdvisorVO.ChatMemory chatMemory = JSON.parseObject(advisor.getExtParam(), AiClientAdvisorVO.ChatMemory.class);
                        vo.setChatMemory(chatMemory);
                    } else if (AdvisorType.RAG_ANSWER.name().equals(advisor.getAdvisorType())) {
                        AiClientAdvisorVO.RagAnswer ragAnswer = JSON.parseObject(advisor.getExtParam(), AiClientAdvisorVO.RagAnswer.class);
                        vo.setRagAnswer(ragAnswer);
                    }
                } catch (Exception e) {
                    log.error("解析 extParam 失败，advisorId={}，extParam={}", advisor.getId(), advisor.getExtParam(), e);
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }


    @Override
    public List<AgentClient> queryAiClientByClientIds(List<Long> clientIdList) {
        if (null == clientIdList || clientIdList.isEmpty()) {
            return Collections.emptyList();
        }
        List<AiClient> aiClients = aiClientMapper.selectBatchIds(clientIdList);


        // 查询MCP工具配置
        List<AiClientToolConfig> clientToolConfigs = aiClientToolConfigDao.queryToolConfigByClientIds(clientIdList);
        Map<Long, List<AiClientToolConfig>> mcpMap = clientToolConfigs.stream()
                .collect(Collectors.groupingBy(AiClientToolConfig::getClientId));

        // 查询顾问配置
        List<AiClientAdvisorConfig> advisorConfigs = aiClientAdvisorConfigDao.queryClientAdvisorConfigByClientIds(clientIdList);
        Map<Long, List<AiClientAdvisorConfig>> advisorConfigMap = advisorConfigs.stream()
                .collect(Collectors.groupingBy(AiClientAdvisorConfig::getClientId));

        // 查询知识库配置
        List<AiClientKnowledgeConfig> knowledgeConfigs = aiClientKnowledgeConfigMapper.selectList(Wrappers.lambdaQuery(AiClientKnowledgeConfig.class)
                .in(AiClientKnowledgeConfig::getClientId, clientIdList));
        Map<Long, List<AiClientKnowledgeConfig>> knowledgeConfigMap = knowledgeConfigs.stream()
                .collect(Collectors.groupingBy(AiClientKnowledgeConfig::getClientId));

//        aiClientKnowledgeConfigMapper

        // 构建AiClientVO列表
        List<AgentClient> result = new ArrayList<>();
        for (AiClient client : aiClients) {
            Long clientId = client.getId();
            AgentClient agentCLient = AgentClient.builder()
                    .clientId(clientId)
                    .systemPrompt(client.getSystemPrompt())
                    .modelId(String.valueOf(client.getModelId()))
                    .memorySize(client.getMemorySize())
                    .build();

            // 设置MCP工具ID列表
            if (mcpMap.containsKey(clientId)) {
                List<String> mcpBeanIdList = mcpMap.get(clientId).stream()
                        .map(mcp -> String.valueOf(mcp.getToolId()))
                        .collect(Collectors.toList());
                agentCLient.setMcpIdList(mcpBeanIdList);
            } else {
                agentCLient.setMcpIdList(new ArrayList<>());
            }

            // 设置顾问ID列表
            if (advisorConfigMap.containsKey(clientId)) {
                List<String> advisorBeanIdList = advisorConfigMap.get(clientId).stream()
                        .map(advisor -> String.valueOf(advisor.getAdvisorId()))
                        .collect(Collectors.toList());
                agentCLient.setAdvisorIdList(advisorBeanIdList);
            } else {
                agentCLient.setAdvisorIdList(new ArrayList<>());
            }

            // 设置知识库ID列表
            if (knowledgeConfigMap.containsKey(clientId)) {
                List<Long> knowledgeIdList = knowledgeConfigMap.get(clientId).stream()
                        .map(AiClientKnowledgeConfig::getKnowledgeId)
                        .collect(Collectors.toList());

                List<AiKnowledge> aiKnowledgeList = knowledgeMapper.selectBatchIds(knowledgeIdList);
                List<Knowledge> knowledgeList = BeanUtil.copyToList(aiKnowledgeList, Knowledge.class);
                agentCLient.setKnowledgeList(knowledgeList);
            }
            result.add(agentCLient);
        }

        return result;
    }

    @Override
    public List<Long> queryAiClientIds() {

        LambdaQueryWrapper<AiClient> wrapper = Wrappers.lambdaQuery(AiClient.class)
                .select(AiClient::getId)
                .eq(AiClient::getStatus, 1);

        return aiClientMapper.selectList(wrapper).stream().map(AiClient::getId).toList();
    }

    @Override
    public Long saveKnowledge(Knowledge knowledge) {
        AiKnowledge aiKnowledge = new AiKnowledge();
        aiKnowledge.setRagName(knowledge.getRagName());
        aiKnowledge.setKnowledgeTag(knowledge.getKnowledgeTag());
        aiKnowledge.setContent(knowledge.getContent());
        aiKnowledge.setStatus(1);
        aiKnowledge.setCreateTime(LocalDateTime.now());
        aiKnowledge.setUpdateTime(LocalDateTime.now());
        knowledgeMapper.insert(aiKnowledge);
        return aiKnowledge.getId();
    }

    @Override
    public Knowledge queryRagOrderById(Long ragId) {
        AiKnowledge aiKnowledge = knowledgeMapper.selectById(ragId);
        return BeanUtil.copyProperties(aiKnowledge, Knowledge.class);
    }


}

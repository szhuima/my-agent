package dev.szhuima.agent.domain.agent.repository;


import dev.szhuima.agent.domain.agent.AgentClient;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientAdvisorVO;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientModelVO;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientToolMcpVO;
import dev.szhuima.agent.domain.agent.model.valobj.Knowledge;

import java.util.List;

/**
 * 仓储服务
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-02 14:15
 */
public interface IAgentRepository {

    List<AiClientModelVO> queryAiClientModelVOListByClientIds(List<Long> clientIdList);

    List<AiClientToolMcpVO> queryAiClientToolMcpVOListByClientIds(List<Long> clientIdList);

    List<AiClientAdvisorVO> queryAdvisorConfigByClientIds(List<Long> clientIdList);

    List<AgentClient> queryAiClientByClientIds(List<Long> clientIdList);

    List<Long> queryAiClientIds();

    Long saveKnowledge(Knowledge knowledge);

    Knowledge queryRagOrderById(Long ragId);
}

package dev.szhuima.agent.domain.agent.repository;


import dev.szhuima.agent.domain.agent.Agent;

import java.util.List;

/**
 * 仓储服务
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-02 14:15
 */
public interface IAgentRepository {

    List<Agent> queryAgentList(List<Long> agentIdList);

}

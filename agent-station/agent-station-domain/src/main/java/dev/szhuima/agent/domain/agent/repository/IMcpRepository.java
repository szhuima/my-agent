package dev.szhuima.agent.domain.agent.repository;

import dev.szhuima.agent.domain.agent.model.Mcp;

import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/11/7 00:05
 * * @Description
 **/
public interface IMcpRepository {

    Mcp getMcp(Long id);

    List<Mcp> getMcpList(Long agentId);

}

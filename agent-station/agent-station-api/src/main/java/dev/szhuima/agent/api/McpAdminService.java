package dev.szhuima.agent.api;


import dev.szhuima.agent.api.dto.AiClientToolMcpRequestDTO;
import dev.szhuima.agent.api.dto.McpQueryRequestDTO;
import dev.szhuima.agent.api.dto.McpResponseDTO;

import java.util.List;

/**
 * MCP客户端配置管理服务接口
 *
 * @author szhuima
 * @description MCP客户端配置管理服务接口
 */
public interface McpAdminService {

    /**
     * 创建MCP客户端配置
     *
     * @param request MCP客户端配置请求对象
     * @return 操作结果
     */
    Response<Boolean> createAiClientToolMcp(AiClientToolMcpRequestDTO request);

    /**
     * 根据ID更新MCP客户端配置
     *
     * @param request MCP客户端配置请求对象
     * @return 操作结果
     */
    Response<Boolean> updateAiClientToolMcpById(AiClientToolMcpRequestDTO request);


    /**
     * 根据ID删除MCP客户端配置
     *
     * @param id 主键ID
     * @return 操作结果
     */
    Response<Boolean> deleteAiClientToolMcpById(Long id);

    /**
     * 根据ID查询MCP客户端配置
     *
     * @param id 主键ID
     * @return MCP客户端配置对象
     */
    Response<McpResponseDTO> queryAiClientToolMcpById(Long id);

    /**
     * 查询启用的MCP客户端配置
     *
     * @return MCP客户端配置列表
     */
    Response<List<McpResponseDTO>> queryEnabledAiClientToolMcps();

    /**
     * 根据查询条件查询MCP客户端配置列表
     *
     * @param request 查询请求对象
     * @return MCP客户端配置列表
     */
    Response<List<McpResponseDTO>> queryAiClientToolMcpList(McpQueryRequestDTO request);

}
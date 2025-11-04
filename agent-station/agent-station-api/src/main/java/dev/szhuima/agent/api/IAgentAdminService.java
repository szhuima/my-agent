package dev.szhuima.agent.api;


import dev.szhuima.agent.api.dto.AiClientQueryRequestDTO;
import dev.szhuima.agent.api.dto.AiClientRequestDTO;
import dev.szhuima.agent.api.dto.AiClientResponseDTO;

import java.util.List;

/**
 * 智能体管理服务接口
 *
 * @author szhuima
 * @description AI客户端配置管理服务接口
 */
public interface IAgentAdminService {

    /**
     * 创建AI客户端配置
     *
     * @param request AI客户端配置请求对象
     * @return 操作结果
     */
    Response<Boolean> createAiClient(AiClientRequestDTO request);

    /**
     * 根据ID更新AI客户端配置
     *
     * @param request AI客户端配置请求对象
     * @return 操作结果
     */
    Response<Boolean> updateAiClientById(AiClientRequestDTO request);

    /**
     * 根据客户端ID更新AI客户端配置
     *
     * @param request AI客户端配置请求对象
     * @return 操作结果
     */
    Response<Boolean> updateAiClientByClientId(AiClientRequestDTO request);

    /**
     * 根据ID删除AI客户端配置
     *
     * @param id 主键ID
     * @return 操作结果
     */
    Response<Boolean> deleteAiClientById(Long id);

    /**
     * 根据ID查询AI客户端配置
     *
     * @param id 主键ID
     * @return AI客户端配置对象
     */
    Response<AiClientResponseDTO> queryAiClientById(Long id);

    /**
     * 查询所有启用的AI客户端配置
     *
     * @return AI客户端配置列表
     */
    Response<List<AiClientResponseDTO>> queryEnabledAiClients();

    /**
     * 根据条件查询AI客户端配置列表
     *
     * @param request 查询条件
     * @return AI客户端配置列表
     */
    Response<List<AiClientResponseDTO>> queryAiClientList(AiClientQueryRequestDTO request);

}
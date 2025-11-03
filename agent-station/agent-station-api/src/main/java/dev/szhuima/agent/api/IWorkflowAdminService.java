package dev.szhuima.agent.api;


import dev.szhuima.agent.api.dto.PageDTO;
import dev.szhuima.agent.api.dto.WorkflowQueryRequestDTO;
import dev.szhuima.agent.api.dto.WorkflowResponseDTO;

/**
 * 智能体工作流拖拉拽配置管理服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/9/28 07:35
 */
public interface IWorkflowAdminService {

    /**
     * 查询工作流
     *
     * @param requestDTO
     * @return
     */
    Response<PageDTO<WorkflowResponseDTO>> queryWorkflow(WorkflowQueryRequestDTO requestDTO);


    /**
     * 查询工作流定义
     *
     * @param workflowId
     * @return
     */
    Response<String> queryDSL(Long workflowId);


    /**
     * 从DSL文件中导入工作流
     *
     * @param dslText dsl 配置内容
     * @return
     */
    Response<String> importWorkflowFromDsl(String dslText);


    /**
     * 删除指定ID的工作流
     * @param workflowId 工作流ID
     * @return
     */
    Response<Boolean> deleteWorkflow(Long workflowId);

}

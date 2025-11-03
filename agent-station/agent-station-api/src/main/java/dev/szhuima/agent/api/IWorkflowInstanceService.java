package dev.szhuima.agent.api;

import dev.szhuima.agent.api.dto.PageDTO;
import dev.szhuima.agent.api.dto.WorkflowInstanceDTO;
import dev.szhuima.agent.api.dto.WorkflowInstanceQuery;

/**
 * * @Author: szhuima
 * * @Date    2025/10/15 17:16
 * * @Description
 **/
public interface IWorkflowInstanceService {


    /**
     * 查询工作流实例
     *
     * @param query
     * @return
     */
    Response<PageDTO<WorkflowInstanceDTO>> queryInstance(WorkflowInstanceQuery query);

    /**
     * 卸载工作流实例
     *
     * @param instanceId
     * @return
     */
    Response<Boolean> unDeploy(Long instanceId);

    /**
     * 部署工作流实例
     *
     * @param instanceId
     * @return
     */
    Response<Boolean> deploy(Long instanceId);


    /**
     * 删除工作流实例
     *
     * @param instanceId
     * @return
     */
    Response<Boolean> deleteInstance(Long instanceId);


}

package dev.szhuima.agent.domain.workflow.reository;

import dev.szhuima.agent.domain.workflow.model.WorkflowInstanceDO;

/**
 * * @Author: szhuima
 * * @Date    2025/9/24 21:41
 * * @Description
 **/
public interface IWorkflowInstanceRepository {


    /**
     * 根据工作流名称获取最新部署的实例
     */
    WorkflowInstanceDO getLastInstance(String workflowName);

    /**
     * 保存工作流实例
     *
     * @param workflowInstance
     */
    void saveInstance(WorkflowInstanceDO workflowInstance);


    /**
     * 获取工作流实例
     *
     * @param instanceId
     * @return
     */
    WorkflowInstanceDO getInstance(Long instanceId);


    void updateByInstanceId(WorkflowInstanceDO workflowInstance);

}

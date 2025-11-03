package dev.szhuima.agent.domain.workflow.reository;

import dev.szhuima.agent.domain.workflow.model.NodeExecutionDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowExecutionDO;

/**
 * * @Author: szhuima
 * * @Date    2025/10/9 12:32
 * * @Description
 **/
public interface IWorkflowExecutionRepository {


    void saveWorkflowExecution(WorkflowExecutionDO workflowExecution);

    void saveNodeExecution(Long instanceId, NodeExecutionDO nodeExec);

    void updateWorkflowExecution(WorkflowExecutionDO executionDO);
}

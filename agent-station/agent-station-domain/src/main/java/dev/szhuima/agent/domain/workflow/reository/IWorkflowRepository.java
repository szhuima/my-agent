package dev.szhuima.agent.domain.workflow.reository;

import dev.szhuima.agent.domain.workflow.model.WorkflowDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowEdgeDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowNodeDO;

/**
 * * @Author: szhuima
 * * @Date    2025/9/24 22:04
 * * @Description
 **/
public interface IWorkflowRepository {

    Long saveWorkflowDsl(Long workflowId, String dsl);

    Long saveWorkflow(WorkflowDO workflow);

    Long saveWorkflowNode(WorkflowNodeDO workflowNode);

    Long saveWorkflowEdge(WorkflowEdgeDO workflowEdge);

    WorkflowDO getById(Long workflowId);

    WorkflowDO getWorkflowByName(String workflowName);

    void deleteWorkflowByName(String workflowName);
}

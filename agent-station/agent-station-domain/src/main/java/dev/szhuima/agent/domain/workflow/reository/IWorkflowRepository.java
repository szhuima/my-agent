package dev.szhuima.agent.domain.workflow.reository;

import dev.szhuima.agent.domain.workflow.model.Workflow;
import dev.szhuima.agent.domain.workflow.model.WorkflowEdgeDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowNodeDO;

/**
 * * @Author: szhuima
 * * @Date    2025/9/24 22:04
 * * @Description
 **/
public interface IWorkflowRepository {

    Long saveWorkflow(Workflow workflow);

    Long saveWorkflowNode(WorkflowNodeDO workflowNode);

    Long saveWorkflowEdge(WorkflowEdgeDO workflowEdge);

    Workflow getById(Long workflowId);

    Workflow getWorkflowByName(String workflowName);

    void deleteWorkflowByName(String workflowName);
}

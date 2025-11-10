package dev.szhuima.agent.domain.workflow.reository;

import dev.szhuima.agent.domain.workflow.model.Workflow;

/**
 * * @Author: szhuima
 * * @Date    2025/9/24 22:04
 * * @Description
 **/
public interface IWorkflowRepository {

    Long saveWorkflow(Workflow workflow);

    Workflow getById(Long workflowId);

    Workflow getActiveWorkflow(String workflowName);

    void updateWorkflow(Workflow workflow);
}

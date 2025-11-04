package dev.szhuima.agent.domain.workflow.reository;

import dev.szhuima.agent.domain.workflow.model.*;

import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/9/24 22:04
 * * @Description
 **/
public interface IWorkflowRepository {

    Long saveWorkflowDsl(Long workflowId, String dsl);

    Long saveWorkflow(WorkflowDO workflow);

    Long saveWorkflowTrigger(WorkflowTriggerDO workflowTrigger);

    Long saveWorkflowNode(WorkflowNodeDO workflowNode);

    Long saveWorkflowEdge(WorkflowEdgeDO workflowEdge);

    Long saveFormNodeConfig(WorkflowNodeConfigFormDO nodeConfigFormDO);

    WorkflowNodeDO getNodeById(Long nodeId);

    WorkflowNodeConfigHttp getHttpConfigNode(Long configId);

    WorkflowNodeConfigBatchDO getBatchConfigNode(Long configId);

    WorkflowDO getById(Long workflowId);

    WorkflowNodeConfigLoopDO getLoopConfigNode(Long workflowId);

    Long saveLoopConfigNode(WorkflowNodeConfigLoopDO loopConfigDO);

    Long saveHttpNodeConfig(WorkflowNodeConfigHttp nodeConfigHttpDO);

    List<WorkflowTriggerDO> getTrigger(Long workflowId, TriggerType triggerType);

    WorkflowTriggerDO getTriggerById(Long triggerId);

    WorkflowDO getWorkflowByName(String workflowName);

    void deleteWorkflowByName(String workflowName);
}

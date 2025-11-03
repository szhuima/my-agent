package dev.szhuima.agent.domain.workflow.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 工作流运行实例
@Data
@NoArgsConstructor
public class WorkflowInstanceDO {

    private Long instanceId;
    private Long workflowId;
    private String workflowName;
    private WorkflowDO workflowDO;
    private WorkflowInstanceStatus status;
    private List<NodeExecutionDO> nodeExecutionDOS;

    public void addNodeExecution(NodeExecutionDO execution) {
        this.nodeExecutionDOS.add(execution);
    }
}

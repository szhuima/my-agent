package dev.szhuima.agent.domain.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 节点执行记录
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeExecutionDO {

    private Long nodeId;
    private Long workflowExecutionId;
    private NodeExecutionStatus status;
    private Object result; // 保存输出
    private String errorMsg; // 失败时保存错误信息

    public NodeExecutionDO(Long nodeId) {
        this.nodeId = nodeId;
        this.status = NodeExecutionStatus.RUNNING;
    }

    public void markCompleted(Object  result) {
        this.status = NodeExecutionStatus.COMPLETED;
        this.result = result;
    }

    public void markFailed(String errorMsg) {
        this.status = NodeExecutionStatus.FAILED;
        this.errorMsg = errorMsg;
    }


    public enum NodeExecutionStatus {
        RUNNING,
        COMPLETED,
        FAILED
    }

}

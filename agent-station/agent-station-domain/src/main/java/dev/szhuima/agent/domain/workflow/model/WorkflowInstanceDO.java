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


    /**
     * 查找工作流的启动节点
     *
     * @return 启动节点
     */
    public WorkflowNodeDO findStartNode() {
        return this.workflowDO.findStartNode();
    }

    /**
     * 查找工作流的第一个可执行的节点，即启动节点的下一个节点
     *
     * @return 第一个可执行的节点
     */
    public WorkflowNodeDO findBeginNode() {
        WorkflowNodeDO startNode = findStartNode();
        return this.workflowDO.nextNode(startNode.getName(), "");
    }


}

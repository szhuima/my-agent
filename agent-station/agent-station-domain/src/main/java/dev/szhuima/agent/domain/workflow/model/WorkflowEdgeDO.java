package dev.szhuima.agent.domain.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDateTime;

// 节点之间的连接
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEdgeDO {
    private Long edgeId;
    private Long workflowId;
    private Long fromNodeId;
    private String fromNodeName;
    private Long toNodeId;
    private String toNodeName;
    private String label;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

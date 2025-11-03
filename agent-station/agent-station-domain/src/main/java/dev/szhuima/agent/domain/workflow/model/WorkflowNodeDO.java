package dev.szhuima.agent.domain.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 工作流节点
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowNodeDO {
    private Long nodeId;
    private Long workflowId;
    private String name;
    private NodeType type;
    private String configJson;
    private String conditionExpr;
    private Long configId;
    private Integer positionX;
    private Integer positionY;
    private boolean startNode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public String responsePath() {
        return name + ".response";
    }

}

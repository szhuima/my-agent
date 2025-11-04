package dev.szhuima.agent.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @TableName workflow_edge
 */
@TableName(value = "tb_workflow_edge")
@Data
public class TbWorkflowEdge {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long edgeId;

    /**
     *
     */
    private Long workflowId;

    /**
     *
     */
    private Long fromNodeId;

    private String fromNodeName;

    /**
     *
     */
    private Long toNodeId;

    private String toNodeName;

    /**
     * 分支标签，例如 true/false 或 branchA，用于表示 CONDITION 节点的哪个输出
     */
    private String label;

    /**
     *
     */
    private LocalDateTime createdAt;

    /**
     *
     */
    private LocalDateTime updatedAt;
}
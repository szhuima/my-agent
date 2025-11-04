package dev.szhuima.agent.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @TableName workflow_node
 */
@TableName(value = "tb_workflow_node")
@Data
public class TbWorkflowNode {
    /**
     *
     */
    @TableId(value = "node_id", type = IdType.AUTO)
    private Long nodeId;

    /**
     *
     */
    @TableField(value = "workflow_id")
    private Long workflowId;

    /**
     *
     */
    @TableField(value = "name")
    private String name;

    /**
     * 节点类型，例如 HTTP_CALL, AGENT,BATCH
     */
    @TableField(value = "type")
    private String type;

    /**
     * 触发器类型
     */
    @TableField(value = "trigger_type")
    private String triggerType;

    /**
     * 开始节点,1表示是开始节点
     */
    @TableField(value = "start_node")
    private Integer startNode;

    /**
     * 断言条件表达式
     */
    @TableField(value = "condition_expr")
    private String conditionExpr;

    /**
     * 节点配置ID
     */
    @TableField(value = "config_id")
    private Long configId;

    /**
     * 节点配置
     */
    @TableField(value = "config_json")
    private String configJson;

    /**
     *
     */
    @TableField(value = "position_x")
    private Integer positionX;

    /**
     *
     */
    @TableField(value = "position_y")
    private Integer positionY;

    /**
     *
     */
    @TableField(value = "created_at")
    private LocalDateTime createdAt;

    /**
     *
     */
    @TableField(value = "updated_at")
    private LocalDateTime updatedAt;
}
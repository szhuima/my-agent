package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName workflow_trigger
 */
@TableName(value ="workflow_trigger")
@Data
public class WorkflowTrigger {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

     /**
     * 工作流ID
     */
    @TableField(value = "workflow_id")
    private Long workflowId;

    /**
     * 工作流名称
     */
    @TableField(value = "workflow_name")
    private String workflowName;

    /**
     * 
     */
    @TableField(value = "trigger_type")
    private String triggerType;

    /**
     * 
     */
    @TableField(value = "config")
    private String config;

    /**
     * 
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 
     */
    @TableField(value = "created_at")
    private LocalDateTime createdAt;
}
package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 运行时触发器
 * @TableName workflow_instance_trigger
 */
@TableName(value ="workflow_instance_trigger")
@Data
public class WorkflowInstanceTrigger {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "workflow_instance_id")
    private Long workflowInstanceId;

    /**
     * 
     */
    @TableField(value = "workflow_trigger_id")
    private Long workflowTriggerId;

    /**
     * 触发器类型
     */
    @TableField(value = "trigger_type")
    private String triggerType;

    /**
     * 状态
     */
    @TableField(value = "status")
    private String status;

    /**
     * 是否启用 0未启用 1启用
     */
    @TableField(value = "enabled")
    private Integer enabled;

    /**
     * 最近启动时间
     */
    @TableField(value = "last_started_time")
    private LocalDateTime lastStartedTime;

    /**
     * 报错信息
     */
    @TableField(value = "error_message")
    private String errorMessage;
}
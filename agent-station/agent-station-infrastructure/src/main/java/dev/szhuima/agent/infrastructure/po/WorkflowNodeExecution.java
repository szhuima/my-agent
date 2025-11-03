package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 
 * @TableName workflow_node_execution
 */
@TableName(value ="workflow_node_execution")
@Data
public class WorkflowNodeExecution {
    /**
     * 
     */
    @TableId(value = "node_execution_id", type = IdType.AUTO)
    private Long nodeExecutionId;

    /**
     * 
     */
    @TableField(value = "workflow_execution_id")
    private Long workflowExecutionId;

    /**
     * 
     */
    @TableField(value = "instance_id")
    private Long instanceId;

    /**
     *
     */
    @TableField(value = "node_id")
    private Long nodeId;

    /**
     * 
     */
    @TableField(value = "status")
    private String status;

    /**
     * 
     */
    @TableField(value = "output")
    private String output;

    /**
     *
     */
    @TableField(value = "error_msg")
    private String errorMsg;

    /**
     *
     */
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
     * 
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;
}
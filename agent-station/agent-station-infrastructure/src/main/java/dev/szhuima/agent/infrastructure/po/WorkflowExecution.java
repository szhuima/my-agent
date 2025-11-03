package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 
 * @TableName workflow_execution
 */
@TableName(value ="workflow_execution")
@Data
public class WorkflowExecution {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long executionId;

    /**
     * 工作流名称
     */
    private String workflowName;

    /**
     * 
     */
    private Long workflowInstanceId;

    /**
     * 执行状态：PENDING / RUNNING / SUCCESS / FAILED
     */
    private String status;

    /**
     * 
     */
    private LocalDateTime startTime;

    /**
     * 
     */
    private LocalDateTime endTime;

    /**
     * 执行时上下文数据快照
     */
    private String context;

    /**
     * 执行异常信息
     */
    private String errorMessage;

    /**
     * 
     */
    private LocalDateTime createdAt;

    /**
     * 
     */
    private LocalDateTime updatedAt;
}
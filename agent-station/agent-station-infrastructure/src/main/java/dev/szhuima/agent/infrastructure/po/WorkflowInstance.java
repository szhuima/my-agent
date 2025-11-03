package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 
 * @TableName workflow_instance
 */
@TableName(value ="workflow_instance")
@Data
public class WorkflowInstance {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long instanceId;

    /**
     * 
     */
    private Long workflowId;


    private String workflowName;

    /**
     * 
     */
    private String status;


    /**
     * 
     */
    private LocalDateTime createdAt;

    /**
     * 
     */
    private LocalDateTime updatedAt;
}
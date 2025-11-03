package dev.szhuima.agent.domain.workflow.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 
 * @TableName workflow_node_execution
 */
@Data
public class WorkflowNodeExecutionDO {
    /**
     * 
     */
    private Long nodeExecutionId;

    /**
     * 
     */
    private Long executionId;

    /**
     * 
     */
    private Long nodeId;

    /**
     * 
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
     * 
     */
    private String inputData;

    /**
     * 
     */
    private String outputData;

    /**
     * 
     */
    private String errorMsg;

    /**
     * 
     */
    private LocalDateTime createdAt;

    /**
     * 
     */
    private LocalDateTime updatedAt;
}
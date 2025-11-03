package dev.szhuima.agent.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * * @Author: szhuima
 * * @Date    2025/10/15 22:42
 * * @Description
 **/
@Data
public class WorkflowExecutionDTO {
    /**
     *
     */
    private Long executionId;

    private String workflowName;

    /**
     *
     */
    private Long workflowInstanceId;

    /**
     * 执行状态： RUNNING / SUCCESS / FAILED
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

}

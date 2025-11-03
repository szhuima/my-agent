package dev.szhuima.agent.domain.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * * @Author: szhuima
 * * @Date    2025/10/9 11:45
 * * @Description
 **/
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowExecutionDO {
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
    private Status status;

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
    private WorkflowContext context;

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


    public enum Status {
        RUNNING,
        SUCCESS,
        FAILED
    }

}

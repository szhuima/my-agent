package dev.szhuima.agent.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * * @Author: szhuima
 * * @Date    2025/10/15 17:23
 * * @Description
 **/
@Data
public class WorkflowInstanceDTO {

    private Long workflowId;

    private Long instanceId;

    private String workflowName;

    private String status;

    /**
     * 执行记录数
     */
    private Long executionCount;

    private LocalDateTime createdAt;

}

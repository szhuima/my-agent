package dev.szhuima.agent.domain.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * * @Author: szhuima
 * * @Date    2025/10/7 12:58
 * * @Description
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowInstanceTriggerDO {

    /**
     *
     */
    private Integer id;

    /**
     *
     */
    private Long workflowInstanceId;

    private WorkflowInstanceDO workflowInstanceDO;

    private Long workflowTriggerId;

    private String triggerType;

    /**
     * 状态
     */
    private String status;

    /**
     * 是否启用 0未启用 1启用
     */
    private Integer enabled;

    /**
     * 最近启动时间
     */
    private LocalDateTime lastStartedTime;

    /**
     * 报错信息
     */
    private String errorMessage;


    public enum Status {
        RUNNING,
        STOPPED,
        FAILED
    }


}

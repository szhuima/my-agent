package dev.szhuima.agent.domain.workflow.model;

import lombok.Getter;

/**
 * * @Author: szhuima
 * * @Date    2025/10/2 11:30
 * * @Description
 **/
@Getter
public enum WorkflowStatus {
    ACTIVE(1),
    ARCHIVED(0);

    private Integer code;

    WorkflowStatus(Integer code) {
        this.code = code;
    }
}

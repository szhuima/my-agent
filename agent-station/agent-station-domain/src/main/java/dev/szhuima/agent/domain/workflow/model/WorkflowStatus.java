package dev.szhuima.agent.domain.workflow.model;

import lombok.Getter;

/**
 * * @Author: szhuima
 * * @Date    2025/10/2 11:30
 * * @Description
 **/
@Getter
public enum WorkflowStatus {
    ACTIVE("1"),
    INACTIVE("0");

    private String code;

    WorkflowStatus(String code) {
        this.code = code;
    }
}

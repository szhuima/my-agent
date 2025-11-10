package dev.szhuima.agent.domain.workflow.model;

import lombok.Builder;
import lombok.Data;

/**
 * * @Author: szhuima
 * * @Date    2025/11/10 12:48
 * * @Description
 **/
@Data
@Builder
public class WorkflowSaveRequest {

    private Long workflowId;

    private String jsonConfig;

}

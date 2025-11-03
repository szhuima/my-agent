package dev.szhuima.agent.api.dto;

import lombok.Data;

/**
 * * @Author: szhuima
 * * @Date    2025/10/15 22:49
 * * @Description
 **/
@Data
public class WorkflowExecutionQuery extends PageQuery {

    private Long instanceId;

    private String workflowName;

}

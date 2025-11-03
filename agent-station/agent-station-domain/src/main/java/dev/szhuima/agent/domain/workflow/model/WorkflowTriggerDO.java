package dev.szhuima.agent.domain.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * * @Author: szhuima
 * * @Date    2025/10/1 12:31
 * * @Description
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowTriggerDO {

    private Long id;

    private Long workflowId;

    private String workflowName;

    private TriggerType triggerType;

    private String status;

    private String config;

}

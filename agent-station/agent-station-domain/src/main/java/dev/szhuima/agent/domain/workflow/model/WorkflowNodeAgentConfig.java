package dev.szhuima.agent.domain.workflow.model;

import lombok.Data;

/**
 * * @Author: szhuima
 * * @Date    2025/9/30 15:57
 * * @Description
 **/
@Data
public class WorkflowNodeAgentConfig {

    private Long clientId;

    private String userMessage;

    private String sessionId;

}

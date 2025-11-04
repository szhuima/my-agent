package dev.szhuima.agent.domain.agent.model;

import dev.szhuima.agent.domain.agent.Agent;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/11/4 16:21
 * * @Description
 **/
@Data
@Builder
public class AgentExecuteParams {

    private Agent agent;
    private String userMessage;
    private Map<String, Object> context;
    private String conversationId;
    private boolean streaming;
}

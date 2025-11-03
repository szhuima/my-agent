package dev.szhuima.agent.domain.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * * @Author: szhuima
 * * @Date    2025/9/13 21:42
 * * @Description
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentExecuteParam {

    private Long agentId;

    private Long clientId;

    private String userMessage;

    private String sessionId;

    private Integer maxStep;

}

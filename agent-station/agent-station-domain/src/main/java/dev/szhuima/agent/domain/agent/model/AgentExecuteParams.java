package dev.szhuima.agent.domain.agent.model;

import dev.szhuima.agent.domain.agent.Agent;
import lombok.Builder;
import lombok.Data;
import org.springframework.ai.content.Media;

import java.util.List;
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
    private List<Media> mediaList;
    private Map<String, Object> context;
    private String conversationId;
    private boolean streaming;
}

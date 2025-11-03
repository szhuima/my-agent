package dev.szhuima.agent.domain.agent.model.entity;

import lombok.Data;

import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/10/31 09:59
 * * @Description
 **/
@Data
public class ChatRequest {

    private Long clientId;

    private String sessionId;

    private String userMessage;

    /**
     * 知识库ID
     */
    private Long ragId;

    private Map<String, Object> context;


}

package dev.szhuima.agent.api.dto;

import lombok.Data;

import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/10/16 12:54
 * * @Description
 **/
@Data
public class ChatMessageRequest {

    private Long clientId;

    private String sessionId;

    private String userMessage;

    /**
     * 知识库ID
     */
    private Long ragId;

    private Map<String, Object> context;

}

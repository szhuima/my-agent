package dev.szhuima.agent.api.dto;

import lombok.Data;

/**
 * * @Author: szhuima
 * * @Date    2025/11/7 20:23
 * * @Description
 **/
@Data
public class ModelApiChatRequest {

    private Long modelApiId;

    private String userMessage;

}

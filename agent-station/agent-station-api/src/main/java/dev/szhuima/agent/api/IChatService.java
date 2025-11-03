package dev.szhuima.agent.api;

import dev.szhuima.agent.api.dto.ChatMessageRequest;
import dev.szhuima.agent.api.dto.ChatMessageResponse;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * * @Author: szhuima
 * * @Date    2025/10/16 12:53
 * * @Description
 **/
public interface IChatService {


    Response<ChatMessageResponse> nonStreamChat(@RequestBody ChatMessageRequest request);

    /**
     * 简单的交互方式，非流式输出，无对话记忆
     *
     * @param request
     * @return
     */
    Response<ChatMessageResponse> simpleChat(ChatMessageRequest request);

    /**
     * 具备对话记忆的聊天
     *
     * @param request
     * @return
     */
    Response<ChatMessageResponse> memoryChat(ChatMessageRequest request);

    Response<ChatMessageResponse> ragChat(ChatMessageRequest request);


}

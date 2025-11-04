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

}

package dev.szhuima.agent.trigger.http;

import cn.hutool.core.bean.BeanUtil;
import dev.szhuima.agent.api.IChatService;
import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.api.dto.ChatMessageRequest;
import dev.szhuima.agent.api.dto.ChatMessageResponse;
import dev.szhuima.agent.domain.agent.model.ChatRequest;
import dev.szhuima.agent.domain.agent.service.AgentChatService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * * @Author: szhuima
 * * @Date    2025/10/16 12:51
 * * @Description
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1/client-chat")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ClientChatController implements IChatService {

    @Resource
    private AgentChatService agentChatService;

    @PostMapping("/clear-memory/{clientId}/{sessionId}")
    public Response<Boolean> clearMemory(@PathVariable("clientId") Long clientId,
                                         @PathVariable("sessionId") String sessionId) {
        log.info("clear memory for clientId: {}, sessionId: {}", clientId, sessionId);
        agentChatService.clearMemory(clientId, sessionId);
        return Response.success(true);
    }

    /**
     * 非流式输出的聊天，具备对话记忆功能
     *
     * @param request
     * @return
     */
    @Override
    @PostMapping("/chat-non-stream")
    public Response<ChatMessageResponse> nonStreamChat(@RequestBody ChatMessageRequest request) {
        if (request.getClientId() == null) {
            throw new IllegalArgumentException("非法参数");
        }
        ChatRequest chatRequest = BeanUtil.copyProperties(request, ChatRequest.class);
        String res = agentChatService.noneStreamChat(chatRequest);
        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .sessionId(request.getSessionId())
                .content(res)
                .build();
        return Response.success(chatMessageResponse);
    }


    /**
     * 非流式输出的聊天，具备对话记忆功能
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody ChatMessageRequest request) {
        if (request.getClientId() == null) {
            throw new IllegalArgumentException("非法参数");
        }
        ChatRequest chatRequest = BeanUtil.copyProperties(request, ChatRequest.class);
        chatRequest.setStreaming(true);
        Flux<String> stringFlux = agentChatService.streamChat(chatRequest);
        return stringFlux;
    }


}

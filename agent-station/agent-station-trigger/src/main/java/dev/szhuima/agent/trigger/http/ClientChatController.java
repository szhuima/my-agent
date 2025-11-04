package dev.szhuima.agent.trigger.http;

import cn.hutool.core.bean.BeanUtil;
import dev.szhuima.agent.api.IChatService;
import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.api.dto.ChatMessageRequest;
import dev.szhuima.agent.api.dto.ChatMessageResponse;
import dev.szhuima.agent.domain.agent.model.entity.ChatRequest;
import dev.szhuima.agent.domain.agent.service.AgentClientChatService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private AgentClientChatService agentClientChatService;

    @PostMapping("/clear-memory/{clientId}/{sessionId}")
    public Response<Boolean> clearMemory(@PathVariable("clientId") Long clientId,
                                         @PathVariable("sessionId") String sessionId) {
        log.info("clear memory for clientId: {}, sessionId: {}", clientId, sessionId);
        agentClientChatService.clearMemory(clientId, sessionId);
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
        String res = agentClientChatService.noneStreamChat(chatRequest);
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
        Flux<String> stringFlux = agentClientChatService.streamChat(chatRequest);
        return stringFlux;
    }



    /**
     * 简单的交互方式，非流式输出，无对话记忆
     *
     * @param request
     * @return
     */
    @Override
    @PostMapping("/chat-simple")
    public Response<ChatMessageResponse> simpleChat(@RequestBody ChatMessageRequest request) {
        if (request.getClientId() == null
                || StringUtils.isEmpty(request.getUserMessage())) {
            throw new IllegalArgumentException("非法参数");
        }
        String res = agentClientChatService.simpleChat(request.getClientId(), request.getUserMessage());

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .sessionId(request.getSessionId())
                .content(res)
                .build();
        return Response.success(chatMessageResponse);
    }

    /**
     * 具备对话记忆的聊天
     *
     * @param request
     * @return
     */
    @Override
    @PostMapping("/chat-memory")
    public Response<ChatMessageResponse> memoryChat(@RequestBody ChatMessageRequest request) {
        if (request.getClientId() == null
                || StringUtils.isEmpty(request.getUserMessage())
                || StringUtils.isEmpty(request.getSessionId())) {
            throw new IllegalArgumentException("非法参数");
        }
        String res = agentClientChatService.memoryChat(request.getClientId(), request.getUserMessage(), request.getSessionId());

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .sessionId(request.getSessionId())
                .content(res)
                .build();
        return Response.success(chatMessageResponse);
    }

    @Override
    @PostMapping("/chat-rag")
    public Response<ChatMessageResponse> ragChat(@RequestBody ChatMessageRequest request) {
        if (request.getClientId() == null
                || StringUtils.isEmpty(request.getUserMessage())
                || request.getRagId() == null) {
            throw new IllegalArgumentException("非法参数");
        }
        String res = agentClientChatService.ragChat(request.getClientId(), request.getUserMessage(), request.getRagId());

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .sessionId(request.getSessionId())
                .content(res)
                .build();
        return Response.success(chatMessageResponse);
    }


}

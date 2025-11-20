package dev.szhuima.agent.infrastructure.factory;

import dev.szhuima.agent.domain.agent.model.ModelApi;
import dev.szhuima.agent.domain.agent.model.ModelSource;
import dev.szhuima.agent.domain.support.exception.BizException;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * * @Author: szhuima
 * * @Date    2025/11/4 15:37
 * * @Description
 **/
@Component
public class ChatModelFactory {

    private Map<Long, ChatModel> cache = new ConcurrentHashMap<>();


    public ChatModel getOrCreate(ModelApi modelApi) {
        return cache.computeIfAbsent(modelApi.getId(), (apiId) -> createChatModel(modelApi));
    }

    public ChatModel clear(Long apiId) {
        return cache.remove(apiId);
    }

    public ChatModel createChatModel(ModelApi modelApi) {
        ModelSource modelSource = modelApi.getModelSource();
        return switch (modelSource) {
            case OLLAMA -> createOllamaChatModel(modelApi);
            case OPENAI -> createOpenAiChatModel(modelApi);
            default -> throw BizException.of("不支持模型来源: " + modelSource);
        };
    }

    private OpenAiChatModel createOpenAiChatModel(ModelApi modelApi) {
        // 构建OpenAiApi
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(modelApi.getBaseUrl())
                .apiKey(modelApi.getApiKey())
                .completionsPath(modelApi.getCompletionsPath())
                .embeddingsPath(modelApi.getEmbeddingsPath())
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(modelApi.getModelName())
                        .build())
                .build();
    }

    private OllamaChatModel createOllamaChatModel(ModelApi modelVO) {
        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl(modelVO.getBaseUrl())
                .build();

        OllamaChatModel chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaChatOptions.builder()
                        .model(modelVO.getModelName())
                        .build())
                .build();
        return chatModel;
    }

}

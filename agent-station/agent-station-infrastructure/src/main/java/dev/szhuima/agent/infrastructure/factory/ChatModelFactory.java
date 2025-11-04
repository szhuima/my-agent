package dev.szhuima.agent.infrastructure.factory;

import dev.szhuima.agent.domain.agent.model.ModelApi;
import dev.szhuima.agent.domain.agent.model.ModelSource;
import dev.szhuima.agent.domain.support.exception.BizException;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

/**
 * * @Author: szhuima
 * * @Date    2025/11/4 15:37
 * * @Description
 **/
@Component
public class ChatModelFactory {

    public ChatModel createChatModel(ModelApi modelApi) {
        ModelSource modelSource = modelApi.getModelSource();
        switch (modelSource) {
            case OLLAMA:
                return createOllamaChatModel(modelApi);
            case OPENAI:
                return createOpenAiChatModel(modelApi);
            default:
                throw BizException.of("不支持模型来源: " + modelSource);
        }
    }

    private OpenAiChatModel createOpenAiChatModel(ModelApi modelVO) {
        // 构建OpenAiApi
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(modelVO.getBaseUrl())
                .apiKey(modelVO.getApiKey())
                .completionsPath(modelVO.getCompletionsPath())
                .embeddingsPath(modelVO.getEmbeddingsPath())
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(modelVO.getModelVersion())
                        .build())
                .build();
    }

    private OllamaChatModel createOllamaChatModel(ModelApi modelVO) {
        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl(modelVO.getBaseUrl())
                .build();

        OllamaChatModel chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder()
                        .model(modelVO.getModelVersion())
                        .build())
                .build();
        return chatModel;
    }

}

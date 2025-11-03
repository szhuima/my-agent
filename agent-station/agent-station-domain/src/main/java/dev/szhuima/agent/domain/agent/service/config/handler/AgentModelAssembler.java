package dev.szhuima.agent.domain.agent.service.config.handler;

import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientModelVO;
import dev.szhuima.agent.domain.agent.model.valobj.enums.ModelSource;
import dev.szhuima.agent.domain.agent.service.config.AbstractAgentAssembler;
import dev.szhuima.agent.domain.support.chain.ChainContext;
import dev.szhuima.agent.domain.support.chain.HandleResult;
import dev.szhuima.agent.domain.workflow.service.executor.AgentNodeExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/9/12 17:00
 * * @Description
 **/
@Slf4j
@Component
public class AgentModelAssembler extends AbstractAgentAssembler {


    private final AgentNodeExecutor modelClientNodeExecutor;

    public AgentModelAssembler(AgentNodeExecutor modelClientNodeExecutor) {
        super();
        this.modelClientNodeExecutor = modelClientNodeExecutor;
    }

    @Override
    public HandleResult<Void> doHandle(ChainContext ctx, AgentAssemblyInput param) {
        List<AiClientModelVO> aiClientModelList = ctx.getValue(AGENT_CLIENT_MODES_KEY);
        if (CollectionUtils.isEmpty(aiClientModelList)) {
            log.warn("agent client model list is empty");
            return proceed(ctx, param, HandleResult.stopWith(null));
        }

        for (AiClientModelVO modelVO : aiClientModelList) {
            ChatModel chatModel = null;
            if (ModelSource.OPENAI.name().equalsIgnoreCase(modelVO.getModelSource())) {
                chatModel = createOpenAiChatModel(modelVO);
            } else if (ModelSource.OLLAMA.name().equalsIgnoreCase(modelVO.getModelSource())) {
                chatModel = createOllamaChatModel(modelVO);
            } else {
                throw new IllegalArgumentException("unknown model type: " + modelVO.getModelType());
            }
            agentBeanFactory.registerModel(modelVO.getId(), chatModel);
        }
        return proceed(ctx, param, HandleResult.continueWith(null));
    }

    private OpenAiChatModel createOpenAiChatModel(AiClientModelVO modelVO) {
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

    private OllamaChatModel createOllamaChatModel(AiClientModelVO modelVO) {
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

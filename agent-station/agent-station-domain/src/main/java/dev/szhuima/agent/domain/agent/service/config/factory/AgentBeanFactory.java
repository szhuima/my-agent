package dev.szhuima.agent.domain.agent.service.config.factory;

import dev.szhuima.agent.domain.agent.model.valobj.AiClientModelVO;
import io.modelcontextprotocol.client.McpSyncClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/10/22 22:24
 * * @Description
 **/
@Slf4j
@Component
public class AgentBeanFactory {

    @Resource
    private ApplicationContext applicationContext;

    public void registerChatMemory(Long clientId, ChatMemory chatMemory) {
        registerBean(String.valueOf(clientId), ChatMemory.class, chatMemory);
    }

    public ChatMemory getChatMemory(Long clientId) {
        return getBean(ChatMemory.class, String.valueOf(clientId));
    }


    public synchronized void registerChatClient(Long clientId, ChatClient bean) {
        registerBean(String.valueOf(clientId), ChatClient.class, bean);
    }

    public ChatClient getChatClient(Long clientId) {
        return getBean(ChatClient.class, String.valueOf(clientId));
    }

    public synchronized void registerModel(Long modelId, ChatModel model) {
        registerBean(String.valueOf(modelId), ChatModel.class, model);
    }

    public ChatModel getModel(String modelId) {
        return getBean(ChatModel.class, String.valueOf(modelId));
    }

    public synchronized void registerAdvisor(Long advisorId, Advisor advisor) {
        registerBean(String.valueOf(advisorId), Advisor.class, advisor);
    }

    public Advisor getAdvisor(String advisorId) {
        return getBean(Advisor.class, String.valueOf(advisorId));
    }

    public synchronized void registerMCP(Long mcpId, McpSyncClient mcpSyncClient) {
        registerBean(String.valueOf(mcpId), McpSyncClient.class, mcpSyncClient);
    }

    public McpSyncClient getMcpSyncClient(String toolId) {
        return getBean(McpSyncClient.class, String.valueOf(toolId));
    }


    private synchronized <T> void registerBean(String beanId, Class<T> beanClass, T beanInstance) {
        String beanName = beanClass.getSimpleName() + "_" + beanId;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        // 注册Bean
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass, () -> beanInstance);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);

        // 如果Bean已存在，先移除
        if (beanFactory.containsBeanDefinition(beanName)) {
            beanFactory.removeBeanDefinition(beanName);
        }

        // 注册新的Bean
        beanFactory.registerBeanDefinition(beanName, beanDefinition);

        log.info("成功注册Bean: {}", beanName);
    }


    protected <T> T getBean(Class<T> beanClass, String id) {
        try {
            return (T) applicationContext.getBean(beanClass.getSimpleName() + "_" + id);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }


    public ChatModel createChatModel(AiClientModelVO modelVO) {
        ChatModel chatModel = null;
        if ("openai".equalsIgnoreCase(modelVO.getModelType())) {
            chatModel = createOpenAiChatModel(modelVO);
        } else if ("ollama".equalsIgnoreCase(modelVO.getModelType())) {
            chatModel = createOllamaChatModel(modelVO);
        } else {
            throw new IllegalArgumentException("unknown model type: " + modelVO.getModelType());
        }
        return chatModel;
    }


    public OpenAiChatModel createOpenAiChatModel(AiClientModelVO modelVO) {
        // 构建OpenAiApi
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(modelVO.getBaseUrl())
                .apiKey(modelVO.getApiKey())
                .completionsPath(modelVO.getCompletionsPath())
                .embeddingsPath(modelVO.getEmbeddingsPath())
                .build();

        List<McpSyncClient> mcpSyncClients = new ArrayList<>();
        List<AiClientModelVO.AIClientModelToolConfigVO> toolConfigs = modelVO.getAiClientModelToolConfigs();
        if (null != toolConfigs && !toolConfigs.isEmpty()) {
            for (AiClientModelVO.AIClientModelToolConfigVO toolConfig : toolConfigs) {
                McpSyncClient mcpSyncClient = getMcpSyncClient(String.valueOf(toolConfig.getToolId()));
                mcpSyncClients.add(mcpSyncClient);
            }
        }
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(modelVO.getModelVersion())
                        .toolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients).getToolCallbacks())
                        .build())
                .build();
    }

    public OllamaChatModel createOllamaChatModel(AiClientModelVO modelVO) {
        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl(modelVO.getBaseUrl())
                .build();

        List<McpSyncClient> mcpSyncClients = new ArrayList<>();
        List<AiClientModelVO.AIClientModelToolConfigVO> toolConfigs = modelVO.getAiClientModelToolConfigs();
        if (null != toolConfigs && !toolConfigs.isEmpty()) {
            for (AiClientModelVO.AIClientModelToolConfigVO toolConfig : toolConfigs) {
                McpSyncClient mcpSyncClient = getMcpSyncClient(String.valueOf(toolConfig.getToolId()));
                mcpSyncClients.add(mcpSyncClient);
            }
        }

        OllamaChatModel chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder()
                        .model(modelVO.getModelVersion())
                        .toolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients).getToolCallbacks())
                        .build())
                .build();
        return chatModel;
    }

}

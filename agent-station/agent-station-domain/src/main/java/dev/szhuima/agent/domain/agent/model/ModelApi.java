package dev.szhuima.agent.domain.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelApi {

    /**
     * 主键IDl
     */
    private Long id;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 基础URL
     */
    private String baseUrl;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * 完成路径
     */
    private String completionsPath;

    /**
     * 嵌入路径
     */
    private String embeddingsPath;

    /**
     * 模型类型(chat/embedding等)
     */
    private ModelType modelType;

    /**
     * 模型厂家(openai/ollama等)
     */
    private ModelSource modelSource;

    /**
     * 超时时间(秒)
     */
    private Integer timeout;


}

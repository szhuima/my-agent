package dev.szhuima.agent.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI客户端模型配置请求 DTO
 *
 * @author szhuima
 * @description AI客户端模型配置请求数据传输对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiClientModelRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键ID（更新时使用）
     */
    private Long id;

    /**
     * API名称
     */
    private String modelApiName;

    /**
     * 模型名称
     */
    private String modelName;


    /**
     * 模型类型：openai、deepseek、claude
     */
    private String modelType;

    /**
     * 模型来源：openai、deepseek、claude
     */
    private String modelSource;

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
     * 状态：0-禁用，1-启用
     */
    private Integer status;

}
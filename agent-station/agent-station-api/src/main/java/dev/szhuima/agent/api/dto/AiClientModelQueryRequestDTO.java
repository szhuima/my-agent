package dev.szhuima.agent.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI客户端模型配置查询请求 DTO
 *
 * @author szhuima
 * @description AI客户端模型配置查询请求数据传输对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiClientModelQueryRequestDTO extends PageQuery {


    /**
     * 模型ID
     */
    private String modelId;

    /**
     * API名称
     */
    private String modelApiName;

    /**
     * 模型类型：openai、deepseek、claude
     */
    private String modelType;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

}
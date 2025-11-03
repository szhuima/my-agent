package dev.szhuima.agent.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * AI客户端配置请求 DTO
 *
 * @author szhuima
 * @description AI客户端配置请求数据传输对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiClientRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（更新时使用）
     */
    private Long id;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 描述
     */
    private String description;

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 记忆大小
     */
    private Integer memorySize;

    /**
     * 知识库ID列表
     */
    private List<Long> knowledgeIds;


    private List<Long> advisorIds;

    private List<Long> mcpToolIds;

    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status;


}
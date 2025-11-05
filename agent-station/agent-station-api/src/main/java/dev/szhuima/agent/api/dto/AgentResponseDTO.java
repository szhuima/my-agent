package dev.szhuima.agent.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI客户端配置响应 DTO
 *
 * @author szhuima
 * @description AI客户端配置响应数据传输对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;


    /**
     * 客户端名称
     */
    private String agentName;

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 模型名称
     */
    private String modelName;

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


    /**
     * 描述
     */
    private String description;

    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * MCP工具ID列表
     */
    private List<Long> mcpToolIds;

    private List<String> mcpToolNames;


}
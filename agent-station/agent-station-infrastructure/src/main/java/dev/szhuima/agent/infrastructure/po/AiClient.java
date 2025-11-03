package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI客户端配置表
 * @TableName ai_client
 */
@TableName(value ="ai_client")
@Data
public class AiClient {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 客户端类型
     */
    private String clientType;

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 描述
     */
    private String description;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 记忆大小
     */
    @TableField(value = "memory_size")
    private Integer memorySize;

    /**
     * 返回格式
     */
    private String returnFormat;

    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
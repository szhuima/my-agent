package dev.szhuima.agent.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;


@TableName(value = "tb_agent")
@Data
public class TbAgent {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String agentName;


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
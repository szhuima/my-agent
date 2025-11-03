package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识库配置表
 * @TableName ai_rag_order
 */
@TableName(value = "ai_knowledge")
@Data
public class AiKnowledge {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 知识库名称
     */
    @TableField(value = "rag_name")
    private String ragName;

    /**
     * 知识标签
     */
    @TableField(value = "knowledge_tag")
    private String knowledgeTag;

    /**
     * 知识库内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 状态(0:禁用,1:启用)
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
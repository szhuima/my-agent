package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 智能体客户端和知识库关联表
 *
 * @TableName ai_client_knowledge_config
 */
@TableName(value = "ai_client_knowledge_config")
@Data
public class AiClientKnowledgeConfig {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客户端ID
     */
    @TableField(value = "client_id")
    private Long clientId;

    /**
     * 知识库ID
     */
    @TableField(value = "knowledge_id")
    private Long knowledgeId;
}
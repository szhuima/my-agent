package dev.szhuima.agent.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@TableName(value = "tb_agent_knowledge_config")
@Data
public class TbAgentKnowledgeConfig {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客户端ID
     */
    @TableField(value = "agent_id")
    private Long agentId;

    /**
     * 知识库ID
     */
    @TableField(value = "knowledge_id")
    private Long knowledgeId;
}
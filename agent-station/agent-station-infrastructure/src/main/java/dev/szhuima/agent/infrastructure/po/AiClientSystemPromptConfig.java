package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * AI客户端，零部件；模型配置
 * @TableName ai_client_system_prompt_config
 */
@TableName(value ="ai_client_system_prompt_config")
@Data
public class AiClientSystemPromptConfig {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户端ID
     */
    private Long clientId;

    /**
     * 系统提示词ID
     */
    private Long systemPromptId;

    /**
     * 创建时间
     */
    private Date createTime;
}
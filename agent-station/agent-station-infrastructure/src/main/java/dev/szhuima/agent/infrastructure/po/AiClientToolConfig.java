package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 客户端-MCP关联表
 * @TableName ai_client_tool_config
 */
@TableName(value ="ai_client_tool_config")
@Data
public class AiClientToolConfig {
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
     * MCP ID/ function call ID
     */
    private Long toolId;

    /**
     * 创建时间
     */
    private Date createTime;
}
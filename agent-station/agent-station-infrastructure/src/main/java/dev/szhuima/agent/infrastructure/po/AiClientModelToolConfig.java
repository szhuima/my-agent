package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * AI客户端，零部件；模型工具配置
 * @TableName ai_client_model_tool_config
 */
@TableName(value ="ai_client_model_tool_config")
@Data
public class AiClientModelToolConfig {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 工具类型(mcp/function call)
     */
    private String toolType;

    /**
     * MCP ID/ function call ID
     */
    private Long toolId;

    /**
     * 创建时间
     */
    private Date createTime;
}
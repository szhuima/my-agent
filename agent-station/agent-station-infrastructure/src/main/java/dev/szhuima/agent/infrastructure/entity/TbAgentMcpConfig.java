package dev.szhuima.agent.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@TableName(value ="agent_mcp_config")
@Data
public class TbAgentMcpConfig {
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
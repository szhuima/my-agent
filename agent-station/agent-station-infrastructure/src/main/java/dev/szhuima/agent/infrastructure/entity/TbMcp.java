package dev.szhuima.agent.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * MCP客户端配置表
 * @TableName tb_mcp
 */
@TableName(value ="tb_mcp")
@Data
public class TbMcp {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * JSON配置,格式如下：
     * {
     *   "mcpServers": {
     *     "mysql": {
     *       "command": "npx",
     *       "args": [
     *         "-y",
     *         "@fhuang/mcp-mysql-server"
     *       ],
     *       "env": {
     *         "MYSQL_HOST": "your_host",
     *         "MYSQL_USER": "your_user",
     *         "MYSQL_PASSWORD": "your_password",
     *         "MYSQL_DATABASE": "your_database"
     *       }
     *     }
     *   }
     * }
     */
    private String config;

    /**
     * 请求超时时间(分钟)
     */
    private Integer requestTimeout;

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
     * 
     */
    private String createdBy;

    /**
     * 
     */
    private String tenantId;
}
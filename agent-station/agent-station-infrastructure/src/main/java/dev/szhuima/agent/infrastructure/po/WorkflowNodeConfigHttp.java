package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName workflow_node_config_http
 */
@TableName(value ="workflow_node_config_http")
@Data
public class WorkflowNodeConfigHttp {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long configHttpId;

    /**
     * 配置名称，便于管理
     */
    private String name;

    /**
     * HTTP 方法，例如 GET, POST, PUT, DELETE, PATCH
     */
    private String method;

    /**
     * 请求 URL
     */
    private String url;

    /**
     * 请求头 JSON 字符串，例如 {"Authorization":"Bearer xxx"}
     */
    private String headers;

    /**
     * URL 参数 JSON 字符串，例如 {"userId":"123"}
     */
    private String queryParams;

    /**
     * 请求体 JSON 字符串
     */
    private String body;

    /**
     * 认证类型 NONE, BASIC, BEARER, API_KEY
     */
    private String authType;

    /**
     * 认证配置 JSON 字符串，例如 token、用户名密码
     */
    private String authConfig;

    /**
     * 超时时间，单位秒
     */
    private Integer timeout;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 
     */
    private LocalDateTime createdAt;

    /**
     * 
     */
    private LocalDateTime updatedAt;
}
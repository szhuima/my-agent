package dev.szhuima.agent.domain.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowNodeConfigHttp {
    /**
     *
     */
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
    private Map<String, String> headers;

    /**
     * URL 参数 JSON 字符串，例如 {"userId":"123"}
     */
    private Map<String, Object> params;

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
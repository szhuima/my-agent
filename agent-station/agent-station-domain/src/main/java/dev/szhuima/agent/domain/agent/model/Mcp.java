package dev.szhuima.agent.domain.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mcp {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * MCP名称
     */
    private String mcpName;

    private String config;

    /**
     * 传输类型(sse/stdio)
     */
    private McpTransportType transportType;

    /**
     * 传输配置 - sse
     */
    private SseConfig sseConfig;

    /**
     * 传输配置 - stdio
     */
    private StdioConfig stdioConfig;

    /**
     * 请求超时时间
     */
    private Integer requestTimeout;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SseConfig {
        private String baseUri;
        private String sseEndpoint;
    }

    /**
     * "mcp-server-weixin": {
     * "command": "java",
     * "args": [
     * "-Dspring.ai.mcp.server.stdio=true",
     * "-jar",
     * "xxxx-1.0.0.jar"
     * ]
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StdioConfig {

        private Map<String, Stdio> stdio;

        @Data
        public static class Stdio {
            private String command;
            private List<String> args;
            private Map<String,Object> env;

        }
    }

}

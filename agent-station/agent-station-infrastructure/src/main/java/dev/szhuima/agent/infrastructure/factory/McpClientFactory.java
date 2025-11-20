package dev.szhuima.agent.infrastructure.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.szhuima.agent.domain.agent.model.Mcp;
import dev.szhuima.agent.domain.agent.model.McpTransportType;
import dev.szhuima.agent.domain.support.exception.BizException;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/11/6 23:02
 * * @Description
 **/
@Slf4j
@Component
public class McpClientFactory {

    // 全局ObjectMapper：复用避免重复创建，适配MCP协议序列化
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final McpJsonMapper MCP_JSON_MAPPER = new JacksonMcpJsonMapper(OBJECT_MAPPER);

    protected McpSyncClient createClient(Mcp mcp) {
        McpTransportType transportType = mcp.getTransportType();
        switch (transportType) {
            case SSE -> {
                Mcp.SseConfig sseConfig = mcp.getSseConfig();
                String originalBaseUri = sseConfig.getBaseUri();
                String baseUri;
                String sseEndpoint;

                int queryParamStartIndex = originalBaseUri.indexOf("sse");
                if (queryParamStartIndex != -1) {
                    baseUri = originalBaseUri.substring(0, queryParamStartIndex - 1);
                    sseEndpoint = originalBaseUri.substring(queryParamStartIndex - 1);
                } else {
                    baseUri = originalBaseUri;
                    sseEndpoint = sseConfig.getSseEndpoint();
                }
                sseEndpoint = StringUtils.isBlank(sseEndpoint) ? "/sse" : sseEndpoint;
                HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport
                        .builder(baseUri) // 使用截取后的 baseUri
                        .sseEndpoint(sseEndpoint) // 使用截取或默认的 sseEndpoint
                        .build();

                McpSchema.Implementation implementation = new McpSchema.Implementation(mcp.getMcpName(), "1.0");
                McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport)
                        .clientInfo(implementation)
                        .requestTimeout(Duration.ofMinutes(mcp.getRequestTimeout()))
                        .build();
                var init_sse = mcpSyncClient.initialize();
                log.info("Tool SSE MCP Initialized {}", init_sse);
                return mcpSyncClient;
            }
            case STUDIO -> {
                Mcp.StdioConfig stdioConfig = mcp.getStdioConfig();
                Map<String, Mcp.StdioConfig.Stdio> stdioMap = stdioConfig.getStdio();
                Mcp.StdioConfig.Stdio stdio = stdioMap.get(mcp.getMcpName());

                var stdioParams = ServerParameters.builder(stdio.getCommand())
                        .args(stdio.getArgs())
                        .build();
                McpSchema.Implementation implementation = new McpSchema.Implementation(mcp.getMcpName(), "1.0");
                var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams,MCP_JSON_MAPPER))
                        .clientInfo(implementation)
                        .requestTimeout(Duration.ofSeconds(mcp.getRequestTimeout())).build();
                var init_stdio = mcpClient.initialize();
                log.info("Tool Stdio MCP Initialized {}", init_stdio);
                return mcpClient;
            }
        }
        throw new BizException("Unsupported mcp transportType: " + transportType);
    }

}

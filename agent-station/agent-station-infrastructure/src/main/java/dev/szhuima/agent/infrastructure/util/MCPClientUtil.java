package dev.szhuima.agent.infrastructure.util;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * * @Author: szhuima
 * * @Date    2025/9/13 08:57
 * * @Description
 **/
@Slf4j
public class MCPClientUtil {

    public static McpSyncClient stdioMcpClient(String command, Duration duration, String... args) {
        var stdioParams = ServerParameters.builder(command)
                .args(args)
                .build();

        var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                .requestTimeout(duration).build();

        var init = mcpClient.initialize();
        log.info("Stdio MCP Initialized: {}", init);
        return mcpClient;
    }


    public static McpSyncClient sseMcpClient(String baseUri, Duration requestTimeout, String sseEndpoint) {
        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport.builder(baseUri)
                .sseEndpoint(sseEndpoint)
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(requestTimeout).build();
        var init_sse = mcpSyncClient.initialize();
        log.info("Tool SSE MCP Initialized {}", init_sse);

        return mcpSyncClient;
    }
}

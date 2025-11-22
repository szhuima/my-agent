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
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    
    // 缓存Map，key是mcpId，value是McpSyncClient
    private final Map<Long, McpSyncClient> clientCache = new ConcurrentHashMap<>();

    /**
     * 获取或创建McpSyncClient，先从缓存中获取，如果缓存中不存在，然后再创建并放入到缓存中
     * @param mcp MCP配置信息
     * @return McpSyncClient实例
     */
    public McpSyncClient getOrCreate(Mcp mcp) {
        Long mcpId = mcp.getId();
        if (mcpId == null) {
            throw new BizException("MCP ID cannot be null");
        }
        
        // 先从缓存中获取
        McpSyncClient client = clientCache.get(mcpId);
        if (client != null) {
            log.debug("Retrieved MCP client from cache for mcpId: {}", mcpId);
            return client;
        }
        
        // 缓存中不存在，创建新的客户端
        client = initMcpClient(mcp);
        
        // 放入缓存
        McpSyncClient existingClient = clientCache.putIfAbsent(mcpId, client);
        if (existingClient != null) {
            // 如果已经有其他线程创建了客户端，使用已存在的客户端
            client = existingClient;
            log.debug("Using existing MCP client for mcpId: {}", mcpId);
        } else {
            log.debug("Created and cached new MCP client for mcpId: {}", mcpId);
        }
        
        return client;
    }

    /**
     * 刷新McpSyncClient，将缓存中的mcp client关闭连接，然后创建一个新的mcp client放入缓存
     * @param mcp MCP配置信息
     * @return 新的McpSyncClient实例
     */
    public McpSyncClient refresh(Mcp mcp) {
        Long mcpId = mcp.getId();
        if (mcpId == null) {
            throw new BizException("MCP ID cannot be null");
        }
        
        // 从缓存中获取现有的客户端
        McpSyncClient oldClient = clientCache.get(mcpId);
        
        // 关闭现有的客户端连接
        if (oldClient != null) {
            try {
                oldClient.close();
                log.info("Closed existing MCP client connection for mcpId: {}", mcpId);
            } catch (Exception e) {
                log.warn("Failed to close MCP client connection for mcpId: {}, error: {}", mcpId, e.getMessage());
                // 继续执行，即使关闭失败也要创建新的客户端
            }
        }
        
        // 创建新的客户端
        McpSyncClient newClient = initMcpClient(mcp);
        
        // 更新缓存
        clientCache.put(mcpId, newClient);
        log.info("Refreshed MCP client for mcpId: {}", mcpId);
        
        return newClient;
    }

    public McpSyncClient initMcpClient(Mcp mcp) {
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
                var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
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
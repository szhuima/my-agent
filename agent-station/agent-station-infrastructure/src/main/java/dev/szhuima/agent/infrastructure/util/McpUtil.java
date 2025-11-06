package dev.szhuima.agent.infrastructure.util;

import com.alibaba.fastjson2.JSONObject;
import dev.szhuima.agent.domain.agent.model.McpTransportType;

/**
 * MCP 工具类
 * 提供 MCP 配置解析和推断功能
 *
 * @author szhuima
 * @date 2025/11/7
 */
public class McpUtil {

    /**
     * 根据 mcpServers 配置推断传输类型
     *
     * @param mcpServers MCP 服务器配置
     * @return 传输类型
     */
    public static McpTransportType inferTransportType(JSONObject mcpServers) {
        if (mcpServers == null) {
            return McpTransportType.STUDIO; // 默认使用 STUDIO
        }
        
        // 检查是否有 SSE 相关的配置（如 baseUri）
        for (String serverName : mcpServers.keySet()) {
            JSONObject serverConfig = mcpServers.getJSONObject(serverName);
            if (serverConfig != null) {
                if (serverConfig.containsKey("baseUri")) {
                    return McpTransportType.SSE;
                }
                if (serverConfig.containsKey("command") || serverConfig.containsKey("args")) {
                    return McpTransportType.STUDIO;
                }
            }
        }
        
        // 默认使用 STUDIO
        return McpTransportType.STUDIO;
    }
    
    /**
     * 推断 MCP 名称
     *
     * @param mcpServers MCP 服务器配置
     * @return MCP 名称
     */
    public static String inferMcpName(JSONObject mcpServers) {
        if (mcpServers == null || mcpServers.isEmpty()) {
            return "Unknown MCP";
        }
        
        // 使用第一个服务器名称作为 MCP 名称
        String firstServerName = mcpServers.keySet().iterator().next();
        return firstServerName;
    }
}
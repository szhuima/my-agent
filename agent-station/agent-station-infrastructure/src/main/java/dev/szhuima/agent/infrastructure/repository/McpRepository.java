package dev.szhuima.agent.infrastructure.repository;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.szhuima.agent.domain.agent.model.Mcp;
import dev.szhuima.agent.domain.agent.model.McpTransportType;
import dev.szhuima.agent.domain.agent.repository.IMcpRepository;
import dev.szhuima.agent.infrastructure.entity.TbAgentMcpConfig;
import dev.szhuima.agent.infrastructure.entity.TbMcp;
import dev.szhuima.agent.infrastructure.mapper.AgentMcpConfigMapper;
import dev.szhuima.agent.infrastructure.mapper.McpMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/11/7 00:08
 * * @Description
 **/
@Slf4j
@Repository
public class McpRepository implements IMcpRepository {

    @Resource
    private McpMapper mcpMapper;

    @Resource
    private AgentMcpConfigMapper agentMcpConfigMapper;

    @Override
    public Mcp getMcp(Long id) {
        TbMcp tbMcp = mcpMapper.selectById(id);
        if (tbMcp == null) {
            return null;
        }
    
        String config = tbMcp.getConfig();
        JSONObject mcpConfig = JSON.parseObject(config);
        JSONObject mcpServers = mcpConfig.getJSONObject("mcpServers");
    
        // 推断传输类型
        McpTransportType transportType = inferTransportType(mcpServers);
        
        // 构建 Mcp 对象
        Mcp.McpBuilder mcpBuilder = Mcp.builder()
                .id(tbMcp.getId())
                .mcpName(inferMcpName(mcpServers)) // 从配置推断 MCP 名称
                .transportType(transportType)
                .config(config)
                .requestTimeout(tbMcp.getRequestTimeout());
    
        // 根据传输类型构建相应的配置对象
        if (transportType != null && mcpServers != null) {
            switch (transportType) {
                case SSE:
                    // 构建 SSE 配置
                    Mcp.SseConfig sseConfig = buildSseConfig(mcpServers);
                    mcpBuilder.sseConfig(sseConfig);
                    break;
                case STUDIO:
                    // 构建 Stdio 配置
                    Mcp.StdioConfig stdioConfig = buildStdioConfig(mcpServers);
                    mcpBuilder.stdioConfig(stdioConfig);
                    break;
                default:
                    // 未知传输类型，不设置配置
                    break;
            }
        }
        Mcp mcp = mcpBuilder.build();
        return mcp;
    }

    
    /**
     * 根据 mcpServers 配置推断传输类型
     */
    private McpTransportType inferTransportType(JSONObject mcpServers) {
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
     */
    private String inferMcpName(JSONObject mcpServers) {
        if (mcpServers == null || mcpServers.isEmpty()) {
            return "Unknown MCP";
        }
        
        // 使用第一个服务器名称作为 MCP 名称
        String firstServerName = mcpServers.keySet().iterator().next();
        return firstServerName;
    }
    
    /**
     * 构建 SSE 配置
     */
    private Mcp.SseConfig buildSseConfig(JSONObject mcpServers) {
        if (mcpServers == null || mcpServers.isEmpty()) {
            return null;
        }
        
        // 获取第一个服务器的配置
        String firstServerName = mcpServers.keySet().iterator().next();
        JSONObject serverConfig = mcpServers.getJSONObject(firstServerName);
        
        if (serverConfig == null) {
            return null;
        }
        
        return Mcp.SseConfig.builder()
                .baseUri(serverConfig.getString("baseUri"))
                .sseEndpoint(serverConfig.getString("sseEndpoint"))
                .build();
    }
    
    /**
     * 构建 Stdio 配置
     */
    private Mcp.StdioConfig buildStdioConfig(JSONObject mcpServers) {
        if (mcpServers == null || mcpServers.isEmpty()) {
            return null;
        }
        
        Map<String, Mcp.StdioConfig.Stdio> stdioMap = new HashMap<>();
        
        for (String serverName : mcpServers.keySet()) {
            JSONObject serverConfig = mcpServers.getJSONObject(serverName);
            if (serverConfig != null) {
                Mcp.StdioConfig.Stdio stdio = new Mcp.StdioConfig.Stdio();
                stdio.setCommand(serverConfig.getString("command"));
                
                // 解析 args 数组
                JSONArray argsArray = serverConfig.getJSONArray("args");
                if (argsArray != null) {
                    List<String> args = new ArrayList<>();
                    for (int i = 0; i < argsArray.size(); i++) {
                        args.add(argsArray.getString(i));
                    }
                    stdio.setArgs(args);
                }
                
                // 解析 env 对象
                JSONObject envObject = serverConfig.getJSONObject("env");
                if (envObject != null) {
                    Map<String, Object> env = new HashMap<>();
                    for (String envKey : envObject.keySet()) {
                        env.put(envKey, envObject.get(envKey));
                    }
                    stdio.setEnv(env);
                }
                
                stdioMap.put(serverName, stdio);
            }
        }
        
        return Mcp.StdioConfig.builder()
                .stdio(stdioMap)
                .build();
    }

    @Override
    public List<Mcp> getMcpList(Long agentId) {
        List<TbAgentMcpConfig> tbAgentMcpConfigList = agentMcpConfigMapper.selectList(
                new LambdaQueryWrapper<TbAgentMcpConfig>()
                        .eq(TbAgentMcpConfig::getAgentId, agentId)
        );
        if (CollectionUtils.isEmpty(tbAgentMcpConfigList)) {
            return List.of();
        }
        List<Long> mcpIds = tbAgentMcpConfigList.stream().map(TbAgentMcpConfig::getMcpId).toList();
        List<Mcp> mcpList = mcpIds.stream().map(this::getMcp).toList();
        return mcpList;
    }
}
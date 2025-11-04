//package dev.szhuima.agent.infrastructure.repository.assembler;
//
//import com.alibaba.fastjson.JSON;
//import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
//import dev.szhuima.agent.domain.agent.model.AgentMcpTool;
//import dev.szhuima.agent.infrastructure.design.chain.ChainContext;
//import dev.szhuima.agent.infrastructure.design.chain.HandleResult;
//import io.modelcontextprotocol.client.McpClient;
//import io.modelcontextprotocol.client.McpSyncClient;
//import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
//import io.modelcontextprotocol.client.transport.ServerParameters;
//import io.modelcontextprotocol.client.transport.StdioClientTransport;
//import io.modelcontextprotocol.spec.McpSchema;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//import java.util.List;
//import java.util.Map;
//
///**
// * * @Author: szhuima
// * * @Date    2025/9/11 23:26
// * * @Description
// **/
//@Slf4j
//@Component
//public class AgentMCPAssembler extends AbstractAgentAssembler {
//
//
//    @Override
//    public HandleResult<Void> doHandle(ChainContext ctx, AgentAssemblyInput param) {
//        List<AgentMcpTool> aiClientToolMcpList = ctx.getAs(AGENT_CLIENT_MCPS_KEY, List.class);
//        if (aiClientToolMcpList == null || aiClientToolMcpList.isEmpty()) {
//            log.warn("没有可用的AI客户端工具配置 MCP");
//            return proceed(ctx, param, HandleResult.keepGoing());
//        }
//        for (AgentMcpTool mcpVO : aiClientToolMcpList) {
//            // 创建McpSyncClient对象
//            try {
//                McpSyncClient mcpSyncClient = createMcpSyncClient(mcpVO);
//                agentBeanFactory.registerMCP(mcpVO.getId(), mcpSyncClient);
//            } catch (Exception e) {
//                log.error("创建MCP客户端报错|{}", JSON.toJSONString(mcpVO), e);
//                throw e;
//            }
//        }
//        return proceed(ctx, param, HandleResult.keepGoing());
//    }
//
//    protected McpSyncClient createMcpSyncClient(AgentMcpTool agentMcpTool) {
//        String transportType = agentMcpTool.getTransportType();
//
//        switch (transportType) {
//            case "sse" -> {
//                AgentMcpTool.TransportConfigSse transportConfigSse = agentMcpTool.getTransportConfigSse();
//                // http://127.0.0.1:9999/sse?apikey=DElk89iu8Ehhnbu
//                String originalBaseUri = transportConfigSse.getBaseUri();
//                String baseUri;
//                String sseEndpoint;
//
//                int queryParamStartIndex = originalBaseUri.indexOf("sse");
//                if (queryParamStartIndex != -1) {
//                    baseUri = originalBaseUri.substring(0, queryParamStartIndex - 1);
//                    sseEndpoint = originalBaseUri.substring(queryParamStartIndex - 1);
//                } else {
//                    baseUri = originalBaseUri;
//                    sseEndpoint = transportConfigSse.getSseEndpoint();
//                }
//                sseEndpoint = StringUtils.isBlank(sseEndpoint) ? "/sse" : sseEndpoint;
//                HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport
//                        .builder(baseUri) // 使用截取后的 baseUri
//                        .sseEndpoint(sseEndpoint) // 使用截取或默认的 sseEndpoint
//                        .build();
//
//                McpSchema.Implementation implementation = new McpSchema.Implementation(agentMcpTool.getMcpName(), "1.0");
//                McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport)
//                        .clientInfo(implementation)
//                        .requestTimeout(Duration.ofMinutes(agentMcpTool.getRequestTimeout()))
//                        .build();
//                var init_sse = mcpSyncClient.initialize();
//                log.info("Tool SSE MCP Initialized {}", init_sse);
//                return mcpSyncClient;
//            }
//            case "stdio" -> {
//                AgentMcpTool.TransportConfigStdio transportConfigStdio = agentMcpTool.getTransportConfigStdio();
//                Map<String, AgentMcpTool.TransportConfigStdio.Stdio> stdioMap = transportConfigStdio.getStdio();
//                AgentMcpTool.TransportConfigStdio.Stdio stdio = stdioMap.get(agentMcpTool.getMcpName());
//
//                // https://github.com/modelcontextprotocol/servers/tree/main/src/filesystem
//                var stdioParams = ServerParameters.builder(stdio.getCommand())
//                        .args(stdio.getArgs())
//                        .build();
//                McpSchema.Implementation implementation = new McpSchema.Implementation(agentMcpTool.getMcpName(), "1.0");
//                var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
//                        .clientInfo(implementation)
//                        .requestTimeout(Duration.ofSeconds(agentMcpTool.getRequestTimeout())).build();
//                var init_stdio = mcpClient.initialize();
//                log.info("Tool Stdio MCP Initialized {}", init_stdio);
//                return mcpClient;
//            }
//        }
//
//        throw new RuntimeException("err! transportType " + transportType + " not exist!");
//    }
//}

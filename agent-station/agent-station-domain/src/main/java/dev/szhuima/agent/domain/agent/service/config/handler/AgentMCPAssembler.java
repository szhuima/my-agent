package dev.szhuima.agent.domain.agent.service.config.handler;

import com.alibaba.fastjson.JSON;
import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientToolMcpVO;
import dev.szhuima.agent.domain.agent.service.config.AbstractAgentAssembler;
import dev.szhuima.agent.domain.support.chain.ChainContext;
import dev.szhuima.agent.domain.support.chain.HandleResult;
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
import java.util.List;
import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/9/11 23:26
 * * @Description
 **/
@Slf4j
@Component
public class AgentMCPAssembler extends AbstractAgentAssembler {


    @Override
    public HandleResult<Void> doHandle(ChainContext ctx, AgentAssemblyInput param) {
        List<AiClientToolMcpVO> aiClientToolMcpList = ctx.getAs(AGENT_CLIENT_MCPS_KEY, List.class);
        if (aiClientToolMcpList == null || aiClientToolMcpList.isEmpty()) {
            log.warn("没有可用的AI客户端工具配置 MCP");
            return proceed(ctx, param, HandleResult.keepGoing());
        }
        for (AiClientToolMcpVO mcpVO : aiClientToolMcpList) {
            // 创建McpSyncClient对象
            try {
                McpSyncClient mcpSyncClient = createMcpSyncClient(mcpVO);
                agentBeanFactory.registerMCP(mcpVO.getId(), mcpSyncClient);
            } catch (Exception e) {
                log.error("创建MCP客户端报错|{}", JSON.toJSONString(mcpVO), e);
                throw e;
            }
        }
        return proceed(ctx, param, HandleResult.keepGoing());
    }

    protected McpSyncClient createMcpSyncClient(AiClientToolMcpVO aiClientToolMcpVO) {
        String transportType = aiClientToolMcpVO.getTransportType();

        switch (transportType) {
            case "sse" -> {
                AiClientToolMcpVO.TransportConfigSse transportConfigSse = aiClientToolMcpVO.getTransportConfigSse();
                // http://127.0.0.1:9999/sse?apikey=DElk89iu8Ehhnbu
                String originalBaseUri = transportConfigSse.getBaseUri();
                String baseUri;
                String sseEndpoint;

                int queryParamStartIndex = originalBaseUri.indexOf("sse");
                if (queryParamStartIndex != -1) {
                    baseUri = originalBaseUri.substring(0, queryParamStartIndex - 1);
                    sseEndpoint = originalBaseUri.substring(queryParamStartIndex - 1);
                } else {
                    baseUri = originalBaseUri;
                    sseEndpoint = transportConfigSse.getSseEndpoint();
                }
                sseEndpoint = StringUtils.isBlank(sseEndpoint) ? "/sse" : sseEndpoint;
                HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport
                        .builder(baseUri) // 使用截取后的 baseUri
                        .sseEndpoint(sseEndpoint) // 使用截取或默认的 sseEndpoint
                        .build();

                McpSchema.Implementation implementation = new McpSchema.Implementation(aiClientToolMcpVO.getMcpName(), "1.0");
                McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport)
                        .clientInfo(implementation)
                        .requestTimeout(Duration.ofMinutes(aiClientToolMcpVO.getRequestTimeout()))
                        .build();
                var init_sse = mcpSyncClient.initialize();
                log.info("Tool SSE MCP Initialized {}", init_sse);
                return mcpSyncClient;
            }
            case "stdio" -> {
                AiClientToolMcpVO.TransportConfigStdio transportConfigStdio = aiClientToolMcpVO.getTransportConfigStdio();
                Map<String, AiClientToolMcpVO.TransportConfigStdio.Stdio> stdioMap = transportConfigStdio.getStdio();
                AiClientToolMcpVO.TransportConfigStdio.Stdio stdio = stdioMap.get(aiClientToolMcpVO.getMcpName());

                // https://github.com/modelcontextprotocol/servers/tree/main/src/filesystem
                var stdioParams = ServerParameters.builder(stdio.getCommand())
                        .args(stdio.getArgs())
                        .build();
                McpSchema.Implementation implementation = new McpSchema.Implementation(aiClientToolMcpVO.getMcpName(), "1.0");
                var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                        .clientInfo(implementation)
                        .requestTimeout(Duration.ofSeconds(aiClientToolMcpVO.getRequestTimeout())).build();
                var init_stdio = mcpClient.initialize();
                log.info("Tool Stdio MCP Initialized {}", init_stdio);
                return mcpClient;
            }
        }

        throw new RuntimeException("err! transportType " + transportType + " not exist!");
    }
}

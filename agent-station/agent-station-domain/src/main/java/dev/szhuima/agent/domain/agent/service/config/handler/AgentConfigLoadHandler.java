package dev.szhuima.agent.domain.agent.service.config.handler;

import dev.szhuima.agent.domain.agent.AgentClient;
import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientAdvisorVO;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientModelVO;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientToolMcpVO;
import dev.szhuima.agent.domain.agent.service.config.AbstractAgentAssembler;
import dev.szhuima.agent.domain.support.chain.ChainContext;
import dev.szhuima.agent.domain.support.chain.HandleResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * * @Author: szhuima
 * * @Date    2025/9/11 17:36
 * * @Description
 **/
@Slf4j
@Component
public class AgentConfigLoadHandler extends AbstractAgentAssembler {


    @Override
    public HandleResult<Void> doHandle(ChainContext ctx, AgentAssemblyInput param) {
        List<Long> configClientIdList = new ArrayList<>();
        List<Long> clientIdList = param.getClientIdList();

        if (clientIdList == null || clientIdList.isEmpty()) {
            List<Long> allClientIds = repository.queryAiClientIds();
            configClientIdList.addAll(allClientIds);
        } else {
            configClientIdList.addAll(clientIdList);
        }
        CompletableFuture<List<AiClientModelVO>> aiClientModelListFuture = CompletableFuture.supplyAsync(() -> {
            return repository.queryAiClientModelVOListByClientIds(configClientIdList);
        }, threadPoolExecutor);

        CompletableFuture<List<AiClientToolMcpVO>> aiClientToolMcpListFuture = CompletableFuture.supplyAsync(() -> {
            return repository.queryAiClientToolMcpVOListByClientIds(configClientIdList);
        }, threadPoolExecutor);

        CompletableFuture<List<AiClientAdvisorVO>> aiClientAdvisorListFuture = CompletableFuture.supplyAsync(() -> {
            return repository.queryAdvisorConfigByClientIds(configClientIdList);
        }, threadPoolExecutor);

        CompletableFuture<List<AgentClient>> aiClientListFuture = CompletableFuture.supplyAsync(() -> {
            return repository.queryAiClientByClientIds(configClientIdList);
        }, threadPoolExecutor);

        CompletableFuture.allOf(aiClientModelListFuture)
                .thenRun(() -> {
                    List<AiClientModelVO> modelList = aiClientModelListFuture.join();
                    List<AiClientToolMcpVO> clientTools = aiClientToolMcpListFuture.join();
                    List<AiClientAdvisorVO> advisorList = aiClientAdvisorListFuture.join();
                    List<AgentClient> clientList = aiClientListFuture.join();

                    ctx.put(AGENT_CLIENT_MODES_KEY, modelList);
                    ctx.put(AGENT_CLIENT_MCPS_KEY, clientTools);
                    ctx.put(AGENT_CLIENT_ADVISORS_KEY, advisorList);
                    ctx.put(AGENT_CLIENTS_KEY, clientList);
                }).join();
        return proceed(ctx, param, HandleResult.keepGoing());
    }

}

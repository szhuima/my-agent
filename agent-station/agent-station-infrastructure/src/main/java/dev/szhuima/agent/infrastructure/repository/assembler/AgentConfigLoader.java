//package dev.szhuima.agent.infrastructure.repository.assembler;
//
//import dev.szhuima.agent.domain.agent.Agent;
//import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
//import dev.szhuima.agent.domain.agent.model.ModelApi;
//import dev.szhuima.agent.domain.agent.model.AgentMcpTool;
//import dev.szhuima.agent.domain.support.exception.BizException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
///**
// * * @Author: szhuima
// * * @Date    2025/9/11 17:36
// * * @Description
// **/
//@Slf4j
//@Component
//public class AgentConfigLoader extends AbstractAgentAssembler {
//
//
//    @Override
//    public HandleResult<Void> doHandle(ChainContext ctx, AgentAssemblyInput param) {
//        List<Long> configClientIdList = new ArrayList<>();
//        List<Long> clientIdList = param.getClientIdList();
//
//        if (clientIdList == null || clientIdList.isEmpty()) {
//            throw BizException.of("Client id list is empty");
//        } else {
//            configClientIdList.addAll(clientIdList);
//        }
////        CompletableFuture<List<ModelApi>> aiClientModelListFuture = CompletableFuture.supplyAsync(() -> {
////            return repository.queryClientModelList(configClientIdList);
////        }, threadPoolExecutor);
//
//
////        CompletableFuture<List<Agent>> aiClientListFuture = CompletableFuture.supplyAsync(() -> {
////            return repository.queryAgentClient(configClientIdList);
////        }, threadPoolExecutor);
////
////        CompletableFuture.allOf(aiClientModelListFuture)
////                .thenRun(() -> {
////                    List<ModelApi> modelList = aiClientModelListFuture.join();
////                    List<Agent> clientList = aiClientListFuture.join();
////
////                    ctx.put(AGENT_CLIENT_MODES_KEY, modelList);
////                    ctx.put(AGENT_CLIENTS_KEY, clientList);
////                }).join();
//        return proceed(ctx, param, HandleResult.keepGoing());
//    }
//
//}

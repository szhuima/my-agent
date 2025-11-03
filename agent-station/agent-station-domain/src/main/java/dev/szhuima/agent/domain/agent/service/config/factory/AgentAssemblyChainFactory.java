package dev.szhuima.agent.domain.agent.service.config.factory;

import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
import dev.szhuima.agent.domain.agent.service.config.handler.*;
import dev.szhuima.agent.domain.support.chain.HandlerChain;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * * @Author: szhuima
 * * @Date    2025/9/11 17:27
 * * @Description
 **/
@Slf4j
@Service
public class AgentAssemblyChainFactory {

    @Resource
    private AgentConfigLoadHandler agentConfigLoadHandler;

    @Resource
    private AgentMCPAssembler agentMCPAssemblyHandler;

    @Resource
    private AgentModelAssembler agentModelAssemblyHandler;

    @Resource
    private AdvisorAssembler advisorAssembler;

    @Resource
    private AgentClientAssembler agentClientAssemblyHandler;


    public HandlerChain<AgentAssemblyInput, Void> createAgentAssemblyChain() {
        HandlerChain<AgentAssemblyInput, Void> assemblyChain = new HandlerChain<>();
        assemblyChain.addHandler(agentConfigLoadHandler)
                .addHandler(agentMCPAssemblyHandler)
                .addHandler(advisorAssembler)
                .addHandler(agentModelAssemblyHandler)
                .addHandler(agentClientAssemblyHandler);
        return assemblyChain;
    }

}

package dev.szhuima.agent.config;

import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
import dev.szhuima.agent.domain.agent.service.config.factory.AgentAssemblyChainFactory;
import dev.szhuima.agent.domain.support.chain.HandlerChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * * @Author: szhuima
 * * @Date    2025/10/22 22:06
 * * @Description
 **/
@Configuration
public class AgentBeanConfig {

    @Autowired
    private AgentAssemblyChainFactory agentAssemblyChainFactory;


    @Bean("agentAssemblyChain")
    public HandlerChain<AgentAssemblyInput, Void> agentHandler() {
        HandlerChain<AgentAssemblyInput, Void> agentAssemblyChain = agentAssemblyChainFactory.createAgentAssemblyChain();
        return agentAssemblyChain;
    }


}

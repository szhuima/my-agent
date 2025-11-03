package factory;

import dev.szhuima.agent.Application;
import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
import dev.szhuima.agent.domain.agent.service.config.factory.AgentAssemblyChainFactory;
import dev.szhuima.agent.domain.support.chain.DefaultChainContext;
import dev.szhuima.agent.domain.support.chain.HandlerChain;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AgentAssemblyFactoryTest {

    @Resource
    private AgentAssemblyChainFactory agentAssemblyFactory;

    @org.junit.Test
    public void createAgentAssemblyChain() {
        HandlerChain<AgentAssemblyInput, Void> agentAssemblyChain = agentAssemblyFactory.createAgentAssemblyChain();
        agentAssemblyChain.handle(new DefaultChainContext(), AgentAssemblyInput.builder().clientIdList(List.of(2L)).build());
    }
}
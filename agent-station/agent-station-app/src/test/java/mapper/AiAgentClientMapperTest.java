package mapper;

import dev.szhuima.agent.Application;
import dev.szhuima.agent.infrastructure.mapper.AiAgentClientMapper;
import dev.szhuima.agent.infrastructure.po.AiAgentClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AiAgentClientMapperTest {

    @Resource
    private AiAgentClientMapper aiAgentClientMapper;

    @org.junit.Test
    public void queryAllAgentClientConfig() {
        List<AiAgentClient> aiAgentClients = aiAgentClientMapper.queryAllAgentClientConfig();
        log.info("aiAgentClients: {}", aiAgentClients);
    }
}
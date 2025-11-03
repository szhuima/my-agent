package dev.szhuima.agent;

import dev.szhuima.agent.domain.support.chain.HandlerChain;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * * @Author: szhuima
 * * @Date    2025/9/11 11:11
 * * @Description
 **/
@Slf4j
@EnableAsync
@SpringBootApplication
@MapperScan("dev.szhuima.agent.infrastructure.mapper")
public class Application implements CommandLineRunner {

    @Resource
    @Qualifier("agentAssemblyChain")
    private HandlerChain agentAssemblyChain;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Application start with args: {}", (Object) args);
        //UserContext.setUserId("10001");
        //agentAssemblyChain.handle(new DefaultChainContext(), AgentAssemblyInput.builder().clientIdList(List.of()).build());
    }
}

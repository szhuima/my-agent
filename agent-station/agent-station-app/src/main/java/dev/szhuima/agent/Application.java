package dev.szhuima.agent;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
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


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Application start with args: {}", (Object) args);
    }
}

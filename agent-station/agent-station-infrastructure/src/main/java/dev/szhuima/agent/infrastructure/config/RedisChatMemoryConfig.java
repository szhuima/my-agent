package dev.szhuima.agent.infrastructure.config;

import com.alibaba.cloud.ai.memory.redis.RedissonRedisChatMemoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * * @Author: szhuima
 * * @Date    2025/11/19 22:41
 * * @Description
 **/
@Configuration
public class RedisChatMemoryConfig {


    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.password}")
    private String password;


    @Bean
    public RedissonRedisChatMemoryRepository redisChatMemoryRepository()
    {
        return RedissonRedisChatMemoryRepository.builder()
                .host(host)
                .port(port)
                .password(password)
                .build();
    }

}

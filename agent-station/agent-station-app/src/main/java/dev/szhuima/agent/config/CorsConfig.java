package dev.szhuima.agent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")           // 所有接口
                        .allowedOrigins("*")         // 允许所有域名
                        .allowedMethods("*")         // 允许所有请求方法
                        .allowedHeaders("*")         // 允许所有请求头
                        .allowCredentials(false)     // allowCredentials 不能和 * 一起使用
                        .maxAge(3600);               // 缓存时间（秒）
            }
        };
    }
}

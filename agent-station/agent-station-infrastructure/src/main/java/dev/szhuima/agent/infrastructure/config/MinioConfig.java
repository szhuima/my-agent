package dev.szhuima.agent.infrastructure.config;

import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MinioConfig {

    private static final Logger log = LoggerFactory.getLogger(MinioConfig.class);

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * 初始化MinIO客户端
     * 配置超时和重试机制，避免连接问题
     */
    @Bean
    public MinioClient minioClient() {
        log.info("初始化MinIO客户端: endpoint={}, accessKey={}", endpoint, accessKey);
        
        // 配置 OkHttpClient 以支持超时和重试
        // 参考：https://blog.csdn.net/jiangxiayouyu/article/details/121827079
        // 问题：服务器返回数据后关闭连接，但客户端将连接放入连接池复用，导致 EOFException
        // 解决方案：
        // 1. 确保 retryOnConnectionFailure 为 true（已设置）
        // 2. 缩短连接池空闲时间，让已关闭的连接尽快被清理
        // 3. 使用较小的连接池，减少连接复用
        
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时30秒
                .writeTimeout(120, TimeUnit.SECONDS)    // 写入超时120秒（大文件上传需要更长时间）
                .readTimeout(120, TimeUnit.SECONDS)     // 读取超时120秒
                .retryOnConnectionFailure(true)        // 连接失败时重试（关键：允许自动重试）
                .followRedirects(true)  // 跟随重定向
                .followSslRedirects(true);  // 跟随SSL重定向
        
        // 根据错误信息 "Broken pipe" 和 "Connection reset by peer"
        // 说明连接在传输过程中被关闭，连接池复用导致问题
        // 完全禁用连接池，每次使用新连接（根据博客建议，这是最稳定的方案）
        httpClientBuilder.connectionPool(new okhttp3.ConnectionPool(0, 1, TimeUnit.SECONDS));
        log.info("禁用连接池，每次使用新连接（避免连接复用导致的 Broken pipe 和 Connection reset 问题）");
        
        OkHttpClient httpClient = httpClientBuilder.build();
        
        MinioClient client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .httpClient(httpClient)  // 使用自定义的HttpClient
                .build();
        
        log.info("MinIO客户端初始化完成");
        return client;
    }
}
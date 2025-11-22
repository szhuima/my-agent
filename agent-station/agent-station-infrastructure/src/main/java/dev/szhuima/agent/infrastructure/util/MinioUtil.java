package dev.szhuima.agent.infrastructure.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import io.minio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Component
public class MinioUtil {

    private static final Logger log = LoggerFactory.getLogger(MinioUtil.class);

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.previewUrl}")
    private String previewUrl;

    private final MinioClient minioClient;

    // 构造器注入MinIO客户端
    public MinioUtil(MinioClient minioClient) {
        this.minioClient = minioClient;
        log.info("MinioUtil 初始化完成，bucketName: {}", bucketName);
    }

    /**
     * 上传图片到MinIO（带重试机制）
     * @param file 前端上传的MultipartFile
     * @return 图片名称
     */
    public String uploadImage(MultipartFile file) throws Exception {
        // 1. 先读取文件内容到字节数组，避免流传输问题
        byte[] fileBytes = file.getBytes();
        long fileSize = file.getSize();
        String contentType = file.getContentType();
        
        // 2. 生成唯一文件名（避免重名覆盖）：UUID + 原文件后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.extName(originalFilename);
        String fileName = IdUtil.simpleUUID() + "." + suffix;

        // 3. 确保桶存在（只检查一次，避免每次上传都检查）
        ensureBucketExists();

        // 4. 使用重试机制上传文件
        int maxRetries = 3;
        Exception lastException = null;
        
        log.info("开始上传文件到MinIO: fileName={}, size={}, contentType={}, bucket={}", 
                fileName, fileSize, contentType, bucketName);
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            log.debug("上传尝试 {}/{}: fileName={}", attempt, maxRetries, fileName);
            try (InputStream inputStream = new java.io.ByteArrayInputStream(fileBytes)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName) // 桶名
                                .object(fileName)   // 存储的文件名
                                .stream(inputStream, fileSize, -1) // 使用字节数组流
                                .contentType(contentType) // 文件MIME类型
                                .build()
                );
                // 上传成功，返回文件名
                log.info("文件上传成功: fileName={}, bucket={}", fileName, bucketName);
                return fileName;
            } catch (java.io.IOException e) {
                // 网络错误，可以重试
                // 包括：Broken pipe, Connection reset by peer, EOFException 等
                lastException = e;
                String errorMsg = e.getMessage();
                log.warn("上传失败 (IOException), 尝试 {}/{}: fileName={}, error={}, errorType={}", 
                        attempt, maxRetries, fileName, errorMsg, e.getClass().getSimpleName());
                
                if (attempt < maxRetries) {
                    // 对于 Broken pipe 和 Connection reset，增加等待时间
                    // 给连接更多时间恢复，避免立即重试
                    long waitTime;
                    if (errorMsg != null && (errorMsg.contains("Broken pipe") || 
                                             errorMsg.contains("Connection reset"))) {
                        // 连接问题，等待更长时间（1秒、2秒、3秒）
                        waitTime = 1000 * attempt;
                        log.info("检测到连接问题 ({}), 等待 {}ms 后重试...", errorMsg, waitTime);
                    } else {
                        // 其他IO错误，使用较短的等待时间（500ms, 1000ms, 1500ms）
                        waitTime = 500 * attempt;
                        log.debug("等待 {}ms 后重试...", waitTime);
                    }
                    
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new Exception("上传被中断", ie);
                    }
                    continue;
                }
            } catch (io.minio.errors.ErrorResponseException e) {
                // MinIO 业务错误
                log.warn("MinIO业务错误: code={}, message={}, fileName={}", 
                        e.errorResponse() != null ? e.errorResponse().code() : "unknown", 
                        e.getMessage(), fileName);
                if (e.errorResponse() != null && e.errorResponse().code().equals("NoSuchBucket")) {
                    // 桶不存在，尝试创建
                    log.info("桶不存在，尝试创建: bucket={}", bucketName);
                    try {
                        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                        log.info("桶创建成功: bucket={}", bucketName);
                        // 创建成功后继续重试
                        if (attempt < maxRetries) {
                            continue;
                        }
                    } catch (Exception bucketException) {
                        log.error("创建桶失败: bucket={}, error={}", bucketName, bucketException.getMessage(), bucketException);
                        throw new Exception("创建桶失败: " + bucketException.getMessage(), bucketException);
                    }
                } else {
                    // 其他业务错误，不重试
                    log.error("MinIO业务错误，不重试: fileName={}, error={}", fileName, e.getMessage(), e);
                    throw new Exception("上传文件到MinIO失败: " + e.getMessage(), e);
                }
            } catch (Exception e) {
                // 其他异常，根据类型决定是否重试
                log.warn("上传异常: 尝试 {}/{}, fileName={}, error={}, errorType={}", 
                        attempt, maxRetries, fileName, e.getMessage(), e.getClass().getName());
                if (isRetryableException(e) && attempt < maxRetries) {
                    lastException = e;
                    try {
                        long waitTime = 500 * attempt;
                        log.debug("等待 {}ms 后重试...", waitTime);
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new Exception("上传被中断", ie);
                    }
                    continue;
                } else {
                    log.error("不可重试的异常: fileName={}, error={}", fileName, e.getMessage(), e);
                    throw new Exception("上传文件到MinIO失败: " + e.getMessage(), e);
                }
            }
        }
        
        // 所有重试都失败了
        log.error("上传失败，已重试 {} 次: fileName={}, lastError={}", 
                maxRetries, fileName, lastException != null ? lastException.getMessage() : "未知错误", lastException);
        throw new Exception("上传文件到MinIO失败，已重试 " + maxRetries + " 次: " + 
                (lastException != null ? lastException.getMessage() : "未知错误"), lastException);
    }
    
    /**
     * 确保桶存在（带缓存，避免频繁检查）
     */
    private volatile boolean bucketChecked = false;
    
    private void ensureBucketExists() throws Exception {
        // 使用双重检查锁定模式，避免频繁检查
        if (!bucketChecked) {
            synchronized (this) {
                if (!bucketChecked) {
                    try {
                        log.debug("检查桶是否存在: bucket={}", bucketName);
                        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
                        log.debug("桶存在检查结果: bucket={}, exists={}", bucketName, exists);
                        if (!exists) {
                            log.info("桶不存在，创建桶: bucket={}", bucketName);
                            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                            log.info("桶创建成功: bucket={}", bucketName);
                        }
                        bucketChecked = true;
                    } catch (Exception e) {
                        log.warn("检查或创建桶失败: bucket={}, error={}", bucketName, e.getMessage(), e);
                        // 如果检查失败，标记为已检查，避免无限重试
                        bucketChecked = true;
                        // 不抛出异常，让上传时再处理
                    }
                }
            }
        }
    }
    
    /**
     * 判断异常是否可重试
     */
    private boolean isRetryableException(Exception e) {
        // 网络相关异常可以重试
        return e instanceof java.io.IOException ||
               e instanceof java.net.SocketTimeoutException ||
               e instanceof java.net.ConnectException ||
               (e.getCause() != null && 
                (e.getCause() instanceof java.io.IOException ||
                 e.getCause() instanceof java.net.SocketTimeoutException));
    }

    /**
     * 根据文件名从MinIO获取图片字节数组
     * @param fileName MinIO中的图片文件名（如5f8a7d9c3b2e1087654321.jpg）
     * @return 图片字节数组
     */
    public byte[] getImageBytes(String fileName) throws Exception {
        // 从MinIO获取文件输入流
        try (GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
             InputStream inputStream = response;
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        }
    }

}
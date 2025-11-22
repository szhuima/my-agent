package dev.szhuima.agent.trigger.http;

import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.infrastructure.util.MinioUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * * @Author: szhuima
 * * @Date    2025/11/22 23:45
 * * @Description
 **/
@RequestMapping("/api/v1/file/upload")
@RestController
public class FileUploadController {
    
    @Resource
    private MinioUtil minioUtil;
    
    /**
     * 上传图片接口
     */
    @PostMapping("/image")
    public Response<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return Response.fail("上传的文件不能为空");
            }
            
            // 检查文件类型是否为图片
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Response.fail("只能上传图片文件");
            }
            
            // 检查文件大小（限制为10MB）
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxSize) {
                return Response.fail("图片大小不能超过10MB");
            }
            
            // 直接同步上传，避免异步导致的连接问题
            String fileName = minioUtil.uploadImage(file);
            
            // 返回上传成功的文件名
            return Response.success(fileName);
            
        } catch (Exception e) {
            // 记录错误日志
            e.printStackTrace();
            String errorMessage = "图片上传失败";
            if (e.getCause() != null) {
                errorMessage += ": " + e.getCause().getMessage();
            }
            return Response.fail(errorMessage);
        }
    }
}
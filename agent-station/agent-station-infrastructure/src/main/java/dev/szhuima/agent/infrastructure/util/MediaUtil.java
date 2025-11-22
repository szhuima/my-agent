package dev.szhuima.agent.infrastructure.util;

import org.springframework.ai.content.Media;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class MediaUtil {

    // 原有通过URL构建的方法（省略）

    /**
     * 通过图片字节数组构建Media对象
     * @param imageBytes 图片字节数组
     * @param mediaType 图片MIME类型
     * @return Media对象
     */
    public Media buildMediaFromBytes(byte[] imageBytes, MediaType mediaType) {
        // Media.fromBytes()通过字节数组构建媒体对象
        return Media.builder()
                .data(imageBytes)
                .mimeType(mediaType)
                .build();
    }

    /**
     * 简化方法：根据字节数组和文件后缀推断MIME类型
     */
    public Media buildMediaFromBytes(byte[] imageBytes, String suffix) {
        MediaType mediaType = switch (suffix.toLowerCase()) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.IMAGE_PNG;
        };
        return Media.builder().data(imageBytes).mimeType(mediaType).build();
    }
}
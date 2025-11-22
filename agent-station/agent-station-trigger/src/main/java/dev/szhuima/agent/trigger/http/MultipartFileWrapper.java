package dev.szhuima.agent.trigger.http;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * MultipartFile包装类，用于在异步线程中使用文件数据
 * 避免跨线程使用原始MultipartFile流的问题
 */
public class MultipartFileWrapper implements MultipartFile {
    
    private final byte[] fileBytes;
    private final String originalFilename;
    private final String contentType;
    private final long size;
    
    public MultipartFileWrapper(byte[] fileBytes, String originalFilename, String contentType, long size) {
        this.fileBytes = fileBytes;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.size = size;
    }
    
    @Override
    public String getName() {
        return "file";
    }
    
    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }
    
    @Override
    public String getContentType() {
        return contentType;
    }
    
    @Override
    public boolean isEmpty() {
        return fileBytes == null || fileBytes.length == 0;
    }
    
    @Override
    public long getSize() {
        return size;
    }
    
    @Override
    public byte[] getBytes() throws IOException {
        return fileBytes;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileBytes);
    }
    
    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        throw new UnsupportedOperationException("transferTo is not supported");
    }
}


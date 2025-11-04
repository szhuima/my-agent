package dev.szhuima.agent.domain.knowledge;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 知识库接口服务
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/10/4 09:11
 */
public interface IKnowledgeService {

    void storeRagFile(String name, String tag, List<MultipartFile> files);

    void deleteKnowledge(Long id);
}

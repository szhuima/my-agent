package dev.szhuima.agent.domain.agent.rag;


import cn.hutool.core.collection.CollectionUtil;
import dev.szhuima.agent.domain.agent.model.valobj.Knowledge;
import dev.szhuima.agent.domain.agent.repository.IAgentRepository;
import dev.szhuima.agent.domain.agent.service.IRagService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 知识库服务
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/10/4 09:12
 */
@Slf4j
@Service
public class RagService implements IRagService {

    @Resource
    @Qualifier("tokenTextSplitter")
    private TextSplitter textSplitter;


    @Resource
    private PgVectorStore vectorStore;

    @Resource
    private IAgentRepository repository;

    @Override
    public void storeRagFile(String name, String tag, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());
            List<Document> documents = documentReader.get();

            String combinedContent = documents.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n"));

            if (CollectionUtil.isEmpty(documents) || combinedContent.isBlank()) {
                log.info("文件 {} 内容为空，跳过存储", file.getOriginalFilename());
                continue;
            }

            // 存储到数据库
            Knowledge knowledge = new Knowledge();
            knowledge.setRagName(name);
            knowledge.setKnowledgeTag(tag);
            knowledge.setContent(combinedContent);
            Long id = repository.saveKnowledge(knowledge);

            List<Document> documentList = textSplitter.apply(documents);

            // 添加知识库标签
            documentList.forEach(doc -> {
                Map<String, Object> metadata = doc.getMetadata();
                metadata.put("tag", tag);
                metadata.put("knowledge_id", id);
            });

            // 存储知识库文件
            vectorStore.accept(documentList);
        }
    }

    @Override
    public void deleteKnowledge(Long id) {
        // 删除知识库文件
        Filter.Expression filterExpression = new FilterExpressionTextParser().parse("knowledge_id == '" + id + "'");
        vectorStore.delete(filterExpression);
    }

}

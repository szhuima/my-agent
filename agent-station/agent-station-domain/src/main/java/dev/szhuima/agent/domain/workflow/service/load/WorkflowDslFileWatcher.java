package dev.szhuima.agent.domain.workflow.service.load;

import dev.szhuima.agent.domain.support.utils.SpringBeanUtils;
import dev.szhuima.agent.domain.workflow.service.WorkflowService;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * * @Author: szhuima
 * * @Date    2025/10/2 10:57
 * * @Description
 **/
@Slf4j
@Service
public class WorkflowDslFileWatcher extends DirectoryWatcher {


    @Value("${agent-station.workflow.dsl-path}")
    private List<String> dslPath;

    @PostConstruct
    public void init() throws IOException {
        log.info("正在加载工作流DSL文件,路径: {}", dslPath);
        super.init();
        registerDirectories(dslPath);
        CompletableFuture.runAsync(() -> startWatching("yml", "yaml"));
    }

    private WorkflowService getWorkflowService() {
        return SpringBeanUtils.getBean(WorkflowService.class);
    }


    @Override
    @SneakyThrows
    public void onFileCreate(Path file) {
        String content = Files.readString(file);
        log.info("onFileCreate, file: {}, content: {}", file, content);
        getWorkflowService().importWorkflow(content);
    }

    @Override
    @SneakyThrows
    public void onFileModify(Path file) {
        String content = Files.readString(file);
        log.info("onFileModify, file: {}, content: {}", file, content);
        getWorkflowService().importWorkflow(content);
    }

    @Override
    @SneakyThrows
    public void onFileDelete(Path file) {
        log.info("onFileDelete, file: {}", file);
        String fileName = file.getFileName().toString();
        String workflowName = getWorkflowService().parseWorkflowName(fileName);
        getWorkflowService().deleteWorkflow(workflowName);
    }
}

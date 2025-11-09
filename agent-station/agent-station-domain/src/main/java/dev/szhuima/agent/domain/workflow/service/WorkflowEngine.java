package dev.szhuima.agent.domain.workflow.service;

import dev.szhuima.agent.domain.workflow.model.Workflow;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowRepository;
import dev.szhuima.agent.domain.workflow.service.executor.NodeExecutionResult;
import dev.szhuima.agent.domain.workflow.service.executor.WorkflowExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * * @Author: szhuima
 * * @Date    2025/10/5 17:26
 * * @Description
 **/
@Slf4j
@Service
public class WorkflowEngine {

    @Resource
    private IWorkflowRepository workflowRepository;


    @Resource
    private WorkflowFactory workflowFactory;

    @Resource
    private WorkflowExecutor workflowExecutor;


    @Resource
    private ThreadPoolExecutor threadPoolExecutor;


    /**
     * 异步执行工作流
     *
     * @param workflow 工作流
     * @param inputParams      输入参数，作为工作流上下文
     */
    public void runWorkflowAsync(Workflow workflow, Map<String, Object> inputParams) {
        CompletableFuture.runAsync(() -> runWorkflow(workflow, inputParams), threadPoolExecutor);
    }


    /**
     * 同步执行工作流
     *
     * @param workflow 工作流
     * @param inputParams      输入参数，作为工作流上下文
     * @return 持久化后的工作流实例 DO
     */
    public NodeExecutionResult runWorkflow(Workflow workflow, Map<String, Object> inputParams) {
        try {
            return workflowExecutor.execute(workflow, inputParams);
        } catch (Exception ex) {
            log.error("工作流执行异常, workflowName={}", workflow.getName(), ex);
            throw ex;
        }
    }


}

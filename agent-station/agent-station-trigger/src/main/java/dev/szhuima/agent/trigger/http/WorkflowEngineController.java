package dev.szhuima.agent.trigger.http;

import dev.szhuima.agent.domain.support.exception.BizException;
import dev.szhuima.agent.domain.workflow.model.WorkflowInstanceDO;
import dev.szhuima.agent.domain.workflow.service.WorkflowEngine;
import dev.szhuima.agent.domain.workflow.service.WorkflowService;
import dev.szhuima.agent.domain.workflow.service.executor.NodeExecutionResult;
import dev.szhuima.agent.infrastructure.repository.WorkflowInstanceRepository;
import io.jsonwebtoken.lang.Collections;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/api/v1/workflow/engine")
@RestController
public class WorkflowEngineController {

    @Resource
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Resource
    private WorkflowService workflowService;

    @Resource
    private WorkflowEngine workflowEngine;


    private WorkflowInstanceDO validate(Long instanceId) {
        WorkflowInstanceDO instance = workflowInstanceRepository.getInstance(instanceId);
        if (instance == null) {
            throw new BizException("工作流实例不存在: " + instanceId);
        }
        return instance;
    }

    /**
     * 异步执行工作流
     *
     * @param instanceId 工作流实例ID
     */
    @PostMapping("/run-async/{instanceId}")
    public void runWorkflowAsync(@PathVariable("instanceId") Long instanceId,
                                 @RequestParam(required = false) Map<String, Object> params,
                                 @RequestBody(required = false) Map<String, Object> body,
                                 HttpServletRequest request) {
        WorkflowInstanceDO workflowInstance = validate(instanceId);
        Map<String, Object> inputParams = new HashMap<>();
        if (!Collections.isEmpty(params)) {
            inputParams.putAll(params);
        }
        if (!Collections.isEmpty(body)) {
            inputParams.putAll(body);
        }
        workflowEngine.runWorkflowAsync(workflowInstance, inputParams);
    }

    /**
     * 同步执行工作流
     *
     * @param instanceId 工作流实例ID
     * @return 工作流实例 DO
     */
    @PostMapping("/run-sync/{instanceId}")
    public NodeExecutionResult runWorkflowSync(@PathVariable("instanceId") Long instanceId,
                                               @RequestParam(required = false) Map<String, Object> params,
                                               @RequestBody(required = false) Map<String, Object> body,
                                               HttpServletRequest request) {
        WorkflowInstanceDO workflowInstance = validate(instanceId);
        Map<String, Object> inputParams = new HashMap<>();
        if (!Collections.isEmpty(params)) {
            inputParams.putAll(params);
        }
        if (!Collections.isEmpty(body)) {
            inputParams.putAll(body);
        }
        return workflowEngine.runWorkflow(workflowInstance, inputParams);
    }

    /**
     * 同步执行最新的工作流实例
     *
     * @param workflowName 工作流名称
     * @return 工作流实例 DO
     */
    @PostMapping("/run-sync/last/{workflowName}")
    public NodeExecutionResult runLastWorkflowInstanceSync(@PathVariable("workflowName") String workflowName,
                                                           @RequestParam(required = false) Map<String, Object> params,
                                                           @RequestBody(required = false) Map<String, Object> body,
                                                           HttpServletRequest request) {
        WorkflowInstanceDO lastInstance = workflowInstanceRepository.getLastInstance(workflowName);
        if (lastInstance == null) {
            throw new IllegalArgumentException("找不到工作流实例");
        }
        WorkflowInstanceDO workflowInstance = validate(lastInstance.getInstanceId());
        Map<String, Object> inputParams = new HashMap<>();
        if (!Collections.isEmpty(params)) {
            inputParams.putAll(params);
        }
        if (!Collections.isEmpty(body)) {
            inputParams.putAll(body);
        }
        return workflowEngine.runWorkflow(workflowInstance, inputParams);
    }

}

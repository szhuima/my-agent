package dev.szhuima.agent.trigger.http;

import com.alibaba.fastjson2.JSON;
import dev.szhuima.agent.domain.workflow.model.TriggerType;
import dev.szhuima.agent.domain.workflow.model.WorkflowInstanceDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowTriggerDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowTriggerHttpConfigDO;
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
import java.util.List;
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


    private WorkflowInstanceDO validate(Long instanceId,
                          Map<String, Object> params,
                          Map<String, Object> body,
                          HttpServletRequest request) {
        WorkflowInstanceDO instance = workflowInstanceRepository.getInstance(instanceId);
        if (instance == null) {
            throw new IllegalArgumentException("工作流实例不存在: " + instanceId);
        }
        List<WorkflowTriggerDO> triggerDOList = workflowService.getTrigger(instance.getWorkflowId(), TriggerType.HTTP);
        if (Collections.isEmpty(triggerDOList)) {
            return instance;
        }

        WorkflowTriggerDO triggerDO = triggerDOList.get(0);

        String config = triggerDO.getConfig();
        WorkflowTriggerHttpConfigDO httpConfig = JSON.parseObject(config, WorkflowTriggerHttpConfigDO.class);

        if (!request.getMethod().equalsIgnoreCase(httpConfig.getMethod())) {
            throw new IllegalArgumentException("请求方法不匹配: " + request.getMethod());
        }

        // 校验params
        if (httpConfig.getParams() != null && !httpConfig.getParams().isEmpty()) {
            for (WorkflowTriggerHttpConfigDO.Field param : httpConfig.getParams()) {
                if (param.getRequired() && !params.containsKey(param.getKey())) {
                    throw new IllegalArgumentException("参数缺失: " + param.getKey());
                }
            }
        }
        // 校验body
        if (httpConfig.getBody() != null && !httpConfig.getBody().isEmpty()) {
            for (WorkflowTriggerHttpConfigDO.Field field : httpConfig.getBody()) {
                if (field.getRequired() && !body.containsKey(field.getKey())) {
                    throw new IllegalArgumentException("请求体参数缺失: " + field.getKey());
                }
            }
        }
        return instance;
    }

     /**
     * 异步执行工作流
     * @param instanceId 工作流实例ID
     */
     @PostMapping("/run-async/{instanceId}")
     public void runWorkflowAsync(@PathVariable("instanceId") Long instanceId,
                                 @RequestParam(required = false) Map<String, Object> params,
                                 @RequestBody(required = false) Map<String, Object> body,
                                 HttpServletRequest request) {
        WorkflowInstanceDO workflowInstance = validate(instanceId, params, body, request);
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
     * @param instanceId 工作流实例ID
     * @return 工作流实例 DO
     */
     @PostMapping("/run-sync/{instanceId}")
     public NodeExecutionResult runWorkflowSync(@PathVariable("instanceId") Long instanceId,
                                               @RequestParam(required = false) Map<String, Object> params,
                                               @RequestBody(required = false) Map<String, Object> body,
                                               HttpServletRequest request) {
        WorkflowInstanceDO workflowInstance = validate(instanceId, params, body, request);
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
        WorkflowInstanceDO workflowInstance = validate(lastInstance.getInstanceId(), params, body, request);
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

package dev.szhuima.agent.trigger.http;

import dev.szhuima.agent.domain.workflow.model.Workflow;
import dev.szhuima.agent.domain.workflow.service.WorkflowEngine;
import dev.szhuima.agent.domain.workflow.service.WorkflowService;
import dev.szhuima.agent.domain.workflow.service.executor.NodeExecutionResult;
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
    private WorkflowService workflowService;

    @Resource
    private WorkflowEngine workflowEngine;


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
        Workflow workflow = workflowService.queryActiveWorkflow(workflowName);

        Map<String, Object> inputParams = new HashMap<>();
        if (!Collections.isEmpty(params)) {
            inputParams.putAll(params);
        }
        if (!Collections.isEmpty(body)) {
            inputParams.putAll(body);
        }
        return workflowEngine.runWorkflow(workflow, inputParams);
    }

}

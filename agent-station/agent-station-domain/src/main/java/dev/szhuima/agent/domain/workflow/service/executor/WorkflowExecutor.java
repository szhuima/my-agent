package dev.szhuima.agent.domain.workflow.service.executor;

import dev.szhuima.agent.domain.workflow.model.*;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowExecutionRepository;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowInstanceRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/9/24 22:49
 * * @Description
 **/
@Slf4j
@Service
public class WorkflowExecutor implements WorkflowExecutorRouter {

    @Resource
    private IWorkflowInstanceRepository workflowInstanceRepository;

    @Resource
    private IWorkflowExecutionRepository workflowExecutionRepository;

    public NodeExecutionResult execute(WorkflowInstanceDO instance, Map<String, Object> inputParams) {
        WorkflowDO workflow = instance.getWorkflowDO();
        WorkflowContext workflowContext = new WorkflowContext(workflow.getMeta());
        workflowContext.putAll(inputParams);
        log.info("开始执行工作流:{},instanceId:{},上下文:{},",workflow.getName(),instance.getInstanceId(),workflowContext);
        WorkflowExecutionDO workflowExecutionDO = WorkflowExecutionDO.builder()
                .workflowInstanceId(instance.getInstanceId())
                .workflowName(workflow.getName())
                .status(WorkflowExecutionDO.Status.RUNNING)
                .startTime(LocalDateTime.now())
                .context(workflowContext)
                .build();

        // 保存工作流执行
        persistWorkflowExecution(workflowExecutionDO);

        WorkflowNodeDO current = workflow.findStartNode();
        NodeExecutionResult nodeExecResult = null;
        while (current != null) {
            try {
                WorkflowNodeExecutor executor = getExecutor(current);
                nodeExecResult = executor.execute(current, workflowContext, workflow);
                // 保存节点执行结果到上下文
                workflowContext.putJSONPath(current.responsePath(), nodeExecResult.getOutput());
                NodeExecutionDO nodeExecutionDO = new NodeExecutionDO(current.getNodeId());
                nodeExecutionDO.setWorkflowExecutionId(workflowExecutionDO.getExecutionId());

                if (nodeExecResult.isCompleted()) {
                    nodeExecutionDO.markCompleted(nodeExecResult.getOutput());
                } else {
                    nodeExecutionDO.markFailed(nodeExecResult.getReason());
                    log.info("{} 节点未执行完成, 原因: {}", current.getName(), nodeExecResult.getReason());
                }
                // 保存工作流执行
                persistWorkflowExecution(workflowExecutionDO);
                persistNodeExecution(instance.getInstanceId(), nodeExecutionDO);
                if (nodeExecResult.isCompleted()) {
                    current = workflow.nextNode(current.getName(), null);
                }  else {
                    break;
                }
            } catch (Exception e) {
                log.error("节点执行异常，节点: {}", current.getName(), e);
                NodeExecutionDO nodeExecutionDO = new NodeExecutionDO(current.getNodeId());
                nodeExecutionDO.markFailed(e.getMessage());
                // 保存工作流执行
                workflowExecutionDO.setStatus(WorkflowExecutionDO.Status.FAILED);
                workflowExecutionDO.setEndTime(LocalDateTime.now());
                workflowExecutionDO.setErrorMessage(e.getMessage());
                persistWorkflowExecution(workflowExecutionDO);
                persistNodeExecution(instance.getInstanceId(), nodeExecutionDO);
                workflowInstanceRepository.updateByInstanceId(instance);
                return nodeExecResult;
            }
        }
        workflowExecutionDO.setStatus(WorkflowExecutionDO.Status.SUCCESS);
        workflowExecutionDO.setEndTime(LocalDateTime.now());
        persistWorkflowExecution(workflowExecutionDO);
        workflowInstanceRepository.updateByInstanceId(instance);
        log.info("工作流执行完成，实例ID: {}", instance.getInstanceId());
        return nodeExecResult;
    }


    private void persistWorkflowExecution(WorkflowExecutionDO executionDO) {
        if (executionDO.getExecutionId() == null) {
            workflowExecutionRepository.saveWorkflowExecution(executionDO);
        } else {
            workflowExecutionRepository.updateWorkflowExecution(executionDO);
        }
    }

    private void persistNodeExecution(Long instanceId, NodeExecutionDO nodeExec) {
        workflowExecutionRepository.saveNodeExecution(instanceId, nodeExec);
    }

}

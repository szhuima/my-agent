package dev.szhuima.agent.domain.workflow.service;

import dev.szhuima.agent.domain.workflow.model.TriggerType;
import dev.szhuima.agent.domain.workflow.model.WorkflowDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowInstanceDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowTriggerDO;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowInstanceRepository;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowRepository;
import dev.szhuima.agent.domain.workflow.service.executor.WorkflowExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * * @Author: szhuima
 * * @Date    2025/9/24 22:41
 * * @Description
 **/
@Slf4j
@Service
public class WorkflowService {

    @Resource
    private IWorkflowRepository workflowRepository;

    @Resource
    private IWorkflowInstanceRepository instanceRepository;

    @Resource
    private WorkflowFactory workflowFactory;

    @Resource
    private WorkflowExecutor workflowExecutor;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;


    /**
     * 部署工作流, 创建工作流实例
     *
     * @param workflowId 工作流模板ID
     * @return 工作流实例ID
     */
    public Long deployWorkflow(Long workflowId) {
        //  获取工作流模板
        WorkflowDO workflowDO = workflowRepository.getById(workflowId);
        if (workflowDO == null) {
            log.error("工作流模板不存在, workflowId={}", workflowId);
            throw new IllegalArgumentException("工作流模板不存在: " + workflowId);
        }
        // 创建工作流实例（领域对象 + 初始化上下文）
        WorkflowInstanceDO workflowInstance = workflowFactory.createWorkflowInstance(workflowDO, workflowDO.getMeta());
        // 持久化工作流实例
        instanceRepository.saveInstance(workflowInstance);

//        List<WorkflowTriggerDO> triggers = workflowDO.getTriggers();
//        if (!Collections.isEmpty(triggers)) {
//            Optional<WorkflowTriggerDO> triggerOptional = triggers.stream().filter(trigger -> trigger.getTriggerType() == TriggerType.RABBITMQ).findFirst();
//            if (triggerOptional.isPresent()) {
//                //
//                WorkflowTriggerDO workflowTriggerDO = triggerOptional.get();
//                rabbitMQTriggerService.deployTrigger(workflowInstance.getInstanceId(), workflowTriggerDO, workflowInstance);
//
//                WorkflowInstanceTriggerDO triggerDO = WorkflowInstanceTriggerDO.builder()
//                        .workflowInstanceId(workflowInstance.getInstanceId())
//                        .workflowTriggerId(workflowTriggerDO.getId())
//                        .status(WorkflowInstanceTriggerDO.Status.RUNNING.name())
//                        .triggerType(workflowTriggerDO.getTriggerType().name())
//                        .lastStartedTime(LocalDateTime.now())
//                        .enabled(1)
//                        .build();
//                instanceRepository.saveInstanceTrigger(triggerDO);
//            }
//        }
        log.info("【{}】 工作流已部署, instanceId={}", workflowDO.getName(), workflowInstance.getInstanceId());
        return workflowInstance.getInstanceId();
    }


    public Long importWorkflow(String workflowDSL) {
        WorkflowDO workflowDO = null;
        try {
            workflowDO = workflowFactory.parseDSL(workflowDSL);
        } catch (Exception e) {
            log.error("工作流配置解析错误, workflowDSL={}", workflowDSL, e);
            throw new IllegalArgumentException("工作流配置解析错误");
        }
        Long workflowId = workflowRepository.saveWorkflow(workflowDO);
        workflowRepository.saveWorkflowDsl(workflowId, workflowDSL);
        workflowDO.getNodes().stream().peek((w) -> w.setWorkflowId(workflowId)).forEach(workflowRepository::saveWorkflowNode);
        workflowDO.getEdges().stream().peek((w) -> w.setWorkflowId(workflowId)).forEach(workflowRepository::saveWorkflowEdge);
        WorkflowDO finalWorkflowDO = workflowDO;
        workflowDO.getTriggers().stream().peek((w) -> {
                    w.setWorkflowName(finalWorkflowDO.getName());
                    w.setWorkflowId(workflowId);
                })
                .forEach(workflowRepository::saveWorkflowTrigger);
        log.info("Workflow saved, workflowName={}, workflowId={}", workflowDO.getName(), workflowId);
        return workflowId;
    }

    public List<WorkflowTriggerDO> getTrigger(Long workflowId, TriggerType triggerType) {
        return workflowRepository.getTrigger(workflowId, triggerType);
    }

    public String parseWorkflowName(String content) {
        return WorkflowDslParser.parseNameFromYaml(content);
    }

    public void deleteWorkflow(String workflowName) {
        workflowRepository.deleteWorkflowByName(workflowName);
    }

    public WorkflowDO queryWorkflowByName(String workflowName) {
        return workflowRepository.getWorkflowByName(workflowName);
    }

}

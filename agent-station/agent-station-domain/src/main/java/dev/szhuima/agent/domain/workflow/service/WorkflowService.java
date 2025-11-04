package dev.szhuima.agent.domain.workflow.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import dev.szhuima.agent.domain.support.exception.BizException;
import dev.szhuima.agent.domain.support.service.DynamicTaskService;
import dev.szhuima.agent.domain.workflow.model.*;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowInstanceRepository;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowRepository;
import dev.szhuima.agent.domain.workflow.service.executor.WorkflowExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    @Resource
    private DynamicTaskService dynamicTaskService;



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

        WorkflowNodeDO startNode = workflowInstance.findStartNode();
        String configJson = startNode.getConfigJson();
        if (StrUtil.isNotEmpty(configJson)) {
            WorkflowStartNodeConfig startNodeConfig = JSON.parseObject(configJson, WorkflowStartNodeConfig.class, JSONReader.Feature.SupportSmartMatch);
            WorkflowStartType startType = startNodeConfig.getStartType();
            String cronExpression = startNodeConfig.getCronExpression();
            if (WorkflowStartType.CRON.equals(startType)) {
                if (StrUtil.isNotEmpty(cronExpression)) {
                    String taskId = String.valueOf(workflowInstance.getInstanceId());
                    dynamicTaskService.startTask(taskId, cronExpression, () -> workflowExecutor.execute(workflowInstance, new HashMap<>()));
                }
            }
        }

        log.info("【{}】 工作流已部署, instanceId={}", workflowDO.getName(), workflowInstance.getInstanceId());
        return workflowInstance.getInstanceId();
    }


    public Long importWorkflow(String workflowDSL) {
        WorkflowDO workflowDO = null;
        try {
            workflowDO = workflowFactory.parseDSL(workflowDSL);
        } catch (Exception e) {
            log.error("工作流配置解析错误, workflowDSL={}", workflowDSL, e);
            throw BizException.of("工作流配置解析错误");
        }
        Long workflowId = workflowRepository.saveWorkflow(workflowDO);
        workflowRepository.saveWorkflowDsl(workflowId, workflowDSL);
        workflowDO.getNodes().stream().peek((w) -> w.setWorkflowId(workflowId)).forEach(workflowRepository::saveWorkflowNode);
        workflowDO.getEdges().stream().peek((w) -> w.setWorkflowId(workflowId)).forEach(workflowRepository::saveWorkflowEdge);
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

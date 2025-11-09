package dev.szhuima.agent.domain.workflow.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import dev.szhuima.agent.domain.support.exception.BizException;
import dev.szhuima.agent.domain.support.service.DynamicTaskService;
import dev.szhuima.agent.domain.workflow.model.*;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowRepository;
import dev.szhuima.agent.domain.workflow.service.executor.WorkflowExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

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
    private WorkflowFactory workflowFactory;

    @Resource
    private WorkflowExecutor workflowExecutor;

    @Resource
    private DynamicTaskService dynamicTaskService;


    /**
     * 激活工作流=
     *
     * @param workflowId 工作流模板ID
     * @return 工作流实例ID
     */
    public Long activeWorkflow(Long workflowId) {
        //  获取工作流模板
        Workflow workflow = workflowRepository.getById(workflowId);
        if (workflow == null) {
            log.error("工作流模板不存在, workflowId={}", workflowId);
            throw new BizException("工作流模板不存在: " + workflowId);
        }

        Integer status = workflow.getStatus();
        if (WorkflowStatus.ACTIVE.getCode().equals(status)) {
            throw new BizException("工作流已激活: " + workflowId);
        }

        WorkflowNodeDO startNode = workflow.findStartNode();
        String configJson = startNode.getConfigJson();
        if (StrUtil.isNotEmpty(configJson)) {
            WorkflowStartNodeConfig startNodeConfig = JSON.parseObject(configJson, WorkflowStartNodeConfig.class, JSONReader.Feature.SupportSmartMatch);
            WorkflowStartType startType = startNodeConfig.getStartType();
            String cronExpression = startNodeConfig.getCronExpression();
            if (WorkflowStartType.CRON.equals(startType)) {
                if (StrUtil.isNotEmpty(cronExpression)) {
                    String taskId = String.valueOf(workflow.getWorkflowId());
                    dynamicTaskService.startTask(taskId, cronExpression, () -> workflowExecutor.execute(workflow, new HashMap<>()));
                }
            }
        }
        workflowRepository.updateWorkflow(workflow);
        log.info("【{}】 工作流已激活, workflowId={}", workflow.getName(), workflowId);
        return workflowId;
    }

    public Long archiveWorkflow(Long workflowId) {
        Workflow workflow = workflowRepository.getById(workflowId);
        if (workflow == null) {
            log.error("工作流模板不存在, workflowId={}", workflowId);
            throw new BizException("工作流模板不存在: " + workflowId);
        }
        Integer status = workflow.getStatus();
        if (WorkflowStatus.ARCHIVED.getCode().equals(status)) {
            throw new BizException("工作流已归档: " + workflowId);
        }
        workflow.setStatus(WorkflowStatus.ARCHIVED.getCode());
        workflowRepository.updateWorkflow(workflow);
        log.info("【{}】 工作流已归档, workflowId={}", workflow.getName(), workflowId);
        return workflowId;
    }

    public Long importWorkflow(String workflowDSL) {
        Workflow workflow = null;
        try {
            workflow = workflowFactory.parseDSL(workflowDSL);
        } catch (Exception e) {
            log.error("工作流配置解析错误, workflowDSL={}", workflowDSL, e);
            throw BizException.of("工作流配置解析错误");
        }
        Long workflowId = workflowRepository.saveWorkflow(workflow);
        workflow.getNodes().stream().peek((w) -> w.setWorkflowId(workflowId)).forEach(workflowRepository::saveWorkflowNode);
        workflow.getEdges().stream().peek((w) -> w.setWorkflowId(workflowId)).forEach(workflowRepository::saveWorkflowEdge);
        log.info("Workflow saved, workflowName={}, workflowId={}", workflow.getName(), workflowId);
        return workflowId;
    }

    public String parseWorkflowName(String content) {
        return WorkflowDslParser.parseNameFromYaml(content);
    }

    public void deleteWorkflow(String workflowName) {
        workflowRepository.deleteWorkflowByName(workflowName);
    }

    public Workflow queryActiveWorkflow(String workflowName) {
        return workflowRepository.getActiveWorkflow(workflowName);
    }

}

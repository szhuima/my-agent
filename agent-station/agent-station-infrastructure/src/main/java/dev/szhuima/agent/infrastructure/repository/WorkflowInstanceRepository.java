package dev.szhuima.agent.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import dev.szhuima.agent.domain.workflow.model.*;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowInstanceRepository;
import dev.szhuima.agent.infrastructure.mapper.WorkflowInstanceMapper;
import dev.szhuima.agent.infrastructure.mapper.WorkflowInstanceTriggerMapper;
import dev.szhuima.agent.infrastructure.po.WorkflowInstance;
import dev.szhuima.agent.infrastructure.po.WorkflowInstanceTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/9/25 10:51
 * * @Description
 **/
@Repository
public class WorkflowInstanceRepository implements IWorkflowInstanceRepository {

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private WorkflowInstanceMapper workflowInstanceMapper;

    @Autowired
    private WorkflowInstanceTriggerMapper workflowInstanceTriggerMapper;

    /**
     * 根据工作流名称获取最新部署的实例
     *
     * @param workflowName
     */
    @Override
    public WorkflowInstanceDO getLastInstance(String workflowName) {
        LambdaQueryWrapper<WorkflowInstance> wrapper = Wrappers.lambdaQuery(WorkflowInstance.class)
                .eq(WorkflowInstance::getWorkflowName, workflowName)
                .orderByDesc(WorkflowInstance::getInstanceId).last("limit 1");
        WorkflowInstance instance = workflowInstanceMapper.selectOne(wrapper);
        return convert2DO(instance);
    }

    @Override
    public List<WorkflowInstanceTriggerDO> findTriggerInstances(Long instanceId, TriggerType triggerType) {
        LambdaQueryWrapper<WorkflowInstanceTrigger> queryWrapper = new LambdaQueryWrapper<WorkflowInstanceTrigger>()
                .eq(instanceId != null, WorkflowInstanceTrigger::getWorkflowInstanceId, instanceId)
                .eq(triggerType != null, WorkflowInstanceTrigger::getTriggerType, triggerType == null ? null : triggerType.name())
                .eq(WorkflowInstanceTrigger::getEnabled, 1);
        List<WorkflowInstanceTrigger> triggers = workflowInstanceTriggerMapper.selectList(queryWrapper);
        List<WorkflowInstanceTriggerDO> triggerDOs = BeanUtil.copyToList(triggers, WorkflowInstanceTriggerDO.class);
        triggerDOs.forEach(triggerDO -> {
            WorkflowTriggerDO workflowTriggerDO = workflowRepository.getTriggerById(triggerDO.getWorkflowTriggerId());
            triggerDO.setWorkflowTriggerDO(workflowTriggerDO);
            WorkflowInstanceDO workflowInstanceDO = getInstance(triggerDO.getWorkflowInstanceId());
            triggerDO.setWorkflowInstanceDO(workflowInstanceDO);
        });
        return triggerDOs;
    }

    /**
     * 保存工作流实例
     *
     * @param workflowInstanceDO
     */
    @Override
    public void saveInstance(WorkflowInstanceDO workflowInstanceDO) {
        WorkflowInstance instance = new WorkflowInstance();
        instance.setInstanceId(workflowInstanceDO.getInstanceId());
        instance.setWorkflowId(workflowInstanceDO.getWorkflowId());
        instance.setStatus(workflowInstanceDO.getStatus().name());
        instance.setWorkflowName(workflowInstanceDO.getWorkflowName());
        workflowInstanceMapper.insert(instance);
        workflowInstanceDO.setInstanceId(instance.getInstanceId());
    }

    /**
     * 获取工作流实例
     *
     * @param instanceId 工作流实例ID
     * @return 工作流实例DO
     */
    @Override
    public WorkflowInstanceDO getInstance(Long instanceId) {
        WorkflowInstance instance = workflowInstanceMapper.selectById(instanceId);
        if (instance == null) {
            return null;
        }
        return convert2DO(instance);
    }

    private WorkflowInstanceDO convert2DO(WorkflowInstance instance) {
        WorkflowInstanceDO workflowInstanceDO = new WorkflowInstanceDO();
        workflowInstanceDO.setInstanceId(instance.getInstanceId());
        workflowInstanceDO.setWorkflowId(instance.getWorkflowId());
        workflowInstanceDO.setStatus(WorkflowInstanceStatus.valueOf(instance.getStatus()));
        WorkflowDO workflowDO = workflowRepository.getById(instance.getWorkflowId());
        if (workflowDO == null) {
            throw new IllegalArgumentException("工作流不存在: " + instance.getWorkflowId());
        }
        workflowInstanceDO.setWorkflowDO(workflowDO);
        return workflowInstanceDO;
    }


    @Override
    public void updateByInstanceId(WorkflowInstanceDO workflowInstance) {
        WorkflowInstance instance = new WorkflowInstance();
        instance.setWorkflowId(workflowInstance.getWorkflowId());
        instance.setInstanceId(workflowInstance.getInstanceId());
        instance.setStatus(workflowInstance.getStatus().name());
        workflowInstanceMapper.updateById(instance);
    }

    @Override
    public void saveInstanceTrigger(WorkflowInstanceTriggerDO triggerDO) {
        WorkflowInstanceTrigger trigger = BeanUtil.copyProperties(triggerDO, WorkflowInstanceTrigger.class);
        workflowInstanceTriggerMapper.insert(trigger);
    }
}

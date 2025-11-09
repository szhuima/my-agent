package dev.szhuima.agent.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import dev.szhuima.agent.domain.workflow.model.Workflow;
import dev.szhuima.agent.domain.workflow.model.WorkflowInstanceDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowInstanceStatus;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowInstanceRepository;
import dev.szhuima.agent.infrastructure.entity.TbWorkflowInstance;
import dev.szhuima.agent.infrastructure.mapper.WorkflowInstanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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


    /**
     * 根据工作流名称获取最新部署的实例
     *
     * @param workflowName
     */
    @Override
    public WorkflowInstanceDO getLastInstance(String workflowName) {
        LambdaQueryWrapper<TbWorkflowInstance> wrapper = Wrappers.lambdaQuery(TbWorkflowInstance.class)
                .eq(TbWorkflowInstance::getWorkflowName, workflowName)
                .orderByDesc(TbWorkflowInstance::getInstanceId).last("limit 1");
        TbWorkflowInstance instance = workflowInstanceMapper.selectOne(wrapper);
        return convert2DO(instance);
    }


    /**
     * 保存工作流实例
     *
     * @param workflowInstanceDO
     */
    @Override
    public void saveInstance(WorkflowInstanceDO workflowInstanceDO) {
        TbWorkflowInstance instance = new TbWorkflowInstance();
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
        TbWorkflowInstance instance = workflowInstanceMapper.selectById(instanceId);
        if (instance == null) {
            return null;
        }
        return convert2DO(instance);
    }

    private WorkflowInstanceDO convert2DO(TbWorkflowInstance instance) {
        WorkflowInstanceDO workflowInstanceDO = new WorkflowInstanceDO();
        workflowInstanceDO.setInstanceId(instance.getInstanceId());
        workflowInstanceDO.setWorkflowId(instance.getWorkflowId());
        workflowInstanceDO.setStatus(WorkflowInstanceStatus.valueOf(instance.getStatus()));
        Workflow workflow = workflowRepository.getById(instance.getWorkflowId());
        if (workflow == null) {
            throw new IllegalArgumentException("工作流不存在: " + instance.getWorkflowId());
        }
        workflowInstanceDO.setWorkflow(workflow);
        return workflowInstanceDO;
    }


    @Override
    public void updateByInstanceId(WorkflowInstanceDO workflowInstance) {
        TbWorkflowInstance instance = new TbWorkflowInstance();
        instance.setWorkflowId(workflowInstance.getWorkflowId());
        instance.setInstanceId(workflowInstance.getInstanceId());
        instance.setStatus(workflowInstance.getStatus().name());
        workflowInstanceMapper.updateById(instance);
    }

}

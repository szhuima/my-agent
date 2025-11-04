package dev.szhuima.agent.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import dev.szhuima.agent.domain.workflow.model.NodeExecutionDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowExecutionDO;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowExecutionRepository;
import dev.szhuima.agent.infrastructure.entity.TbWorkflowExecution;
import dev.szhuima.agent.infrastructure.entity.TbWorkflowNodeExecution;
import dev.szhuima.agent.infrastructure.mapper.WorkflowExecutionMapper;
import dev.szhuima.agent.infrastructure.mapper.WorkflowNodeExecutionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

/**
 * * @Author: szhuima
 * * @Date    2025/10/9 12:34
 * * @Description
 **/
@Repository
public class WorkflowExecutionRepository implements IWorkflowExecutionRepository {

    @Resource
    private WorkflowExecutionMapper workflowExecutionMapper;

    @Resource
    private WorkflowNodeExecutionMapper workflowNodeExecutionMapper;

    @Override
    public void saveWorkflowExecution(WorkflowExecutionDO workflowExecution) {
        TbWorkflowExecution execution = BeanUtil.copyProperties(workflowExecution, TbWorkflowExecution.class);
        workflowExecutionMapper.insert(execution);
        workflowExecution.setExecutionId(execution.getExecutionId());
    }

    @Override
    public void saveNodeExecution(Long instanceId, NodeExecutionDO nodeExec) {
        TbWorkflowNodeExecution nodeExecution = new TbWorkflowNodeExecution();
        nodeExecution.setWorkflowExecutionId(nodeExec.getWorkflowExecutionId());
        nodeExecution.setNodeId(nodeExec.getNodeId());
        nodeExecution.setInstanceId(instanceId);
        nodeExecution.setStatus(nodeExec.getStatus().name());
        nodeExecution.setOutput(JSON.toJSONString(nodeExec.getResult()));
        nodeExecution.setErrorMsg(nodeExec.getErrorMsg());
        workflowNodeExecutionMapper.insert(nodeExecution);
    }

    @Override
    public void updateWorkflowExecution(WorkflowExecutionDO executionDO) {
        TbWorkflowExecution execution = BeanUtil.copyProperties(executionDO, TbWorkflowExecution.class);
        workflowExecutionMapper.updateById(execution);
    }
}

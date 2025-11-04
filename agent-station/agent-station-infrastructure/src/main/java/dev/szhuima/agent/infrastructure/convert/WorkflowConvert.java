package dev.szhuima.agent.infrastructure.convert;

import cn.hutool.core.bean.BeanUtil;
import dev.szhuima.agent.domain.workflow.model.WorkflowDO;
import dev.szhuima.agent.infrastructure.entity.TbWorkflow;

import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/9/27 12:35
 * * @Description
 **/
public interface WorkflowConvert {

    WorkflowDO toWorkflowDO(TbWorkflow workflow);

    List<WorkflowDO> toWorkflowDOList(List<TbWorkflow> workflows);

    default TbWorkflow toWorkflow(WorkflowDO workflowDO) {
        TbWorkflow workflow = new TbWorkflow();
        BeanUtil.copyProperties(workflowDO, workflow);
        return workflow;
    }

    List<TbWorkflow> toWorkflowList(List<WorkflowDO> workflowDOs);


}

package dev.szhuima.agent.infrastructure.convert;

import cn.hutool.core.bean.BeanUtil;
import dev.szhuima.agent.domain.workflow.model.WorkflowDO;
import dev.szhuima.agent.infrastructure.entity.Workflow;

import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/9/27 12:35
 * * @Description
 **/
public interface WorkflowConvert {

    WorkflowDO toWorkflowDO(Workflow workflow);

    List<WorkflowDO> toWorkflowDOList(List<Workflow> workflows);

    default Workflow toWorkflow(WorkflowDO workflowDO) {
        Workflow workflow = new Workflow();
        BeanUtil.copyProperties(workflowDO, workflow);
        return workflow;
    }

    List<Workflow> toWorkflowList(List<WorkflowDO> workflowDOs);


}

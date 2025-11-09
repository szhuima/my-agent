package dev.szhuima.agent.infrastructure.convert;

import cn.hutool.core.bean.BeanUtil;
import dev.szhuima.agent.domain.workflow.model.Workflow;
import dev.szhuima.agent.infrastructure.entity.TbWorkflow;

import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/9/27 12:35
 * * @Description
 **/
public interface WorkflowConvert {

    Workflow toWorkflowDO(TbWorkflow workflow);

    List<Workflow> toWorkflowDOList(List<TbWorkflow> workflows);

    default TbWorkflow toWorkflow(Workflow workflowDO) {
        TbWorkflow workflow = new TbWorkflow();
        BeanUtil.copyProperties(workflowDO, workflow);
        return workflow;
    }

    List<TbWorkflow> toWorkflowList(List<Workflow> workflows);


}

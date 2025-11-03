package dev.szhuima.agent.domain.workflow.service.executor;

import dev.szhuima.agent.domain.support.utils.StringTemplateRender;
import dev.szhuima.agent.domain.workflow.model.WorkflowContext;
import dev.szhuima.agent.domain.workflow.model.WorkflowDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowNodeDO;

public interface WorkflowNodeExecutor extends StringTemplateRender {


    boolean assertCondition(WorkflowNodeDO node, WorkflowContext context);

    /**
     * 执行节点
     * @param node 节点配置
     * @param context 当前工作流上下文
     * @return 执行结果
     */
    NodeExecutionResult execute(WorkflowNodeDO node, WorkflowContext context, WorkflowDO workflowDO);
}

package dev.szhuima.agent.domain.workflow.service.executor;

import com.googlecode.aviator.AviatorEvaluator;
import dev.szhuima.agent.domain.workflow.model.Workflow;
import dev.szhuima.agent.domain.workflow.model.WorkflowContext;
import dev.szhuima.agent.domain.workflow.model.WorkflowNodeDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * * @Author: szhuima
 * * @Date    2025/9/25 10:14
 * * @Description
 **/
@Slf4j
public abstract class AbstractNodeExecutor implements WorkflowNodeExecutor {


    @Override
    public boolean assertCondition(WorkflowNodeDO node, WorkflowContext context) {
        String expression = node.getConditionExpr();
        if (StringUtils.isBlank(expression)) {
            return true;
        }
        Boolean result = (Boolean) AviatorEvaluator.execute(expression, context.getAll());
        log.info("{} 节点断言表达式: {}，结果: {}", node.getName(), expression, result);
        return result;
    }

    @Override
    public NodeExecutionResult execute(WorkflowNodeDO node, WorkflowContext context, Workflow workflow) {
        boolean condition = assertCondition(node, context);
        if (!condition) {
            return NodeExecutionResult.failure("condition unmatched, cannot execute node");
        }
        NodeExecutionResult nodeExecutionResult;
        try {
            nodeExecutionResult = executeNode(node, context, workflow);
        } catch (Exception e) {
            log.error("{} 节点执行异常", node.getName(), e);
            return NodeExecutionResult.failure(e.getMessage());
        }
        return NodeExecutionResult.success(nodeExecutionResult.getOutput());
    }

    public abstract NodeExecutionResult executeNode(WorkflowNodeDO node, WorkflowContext context, Workflow workflow);

}

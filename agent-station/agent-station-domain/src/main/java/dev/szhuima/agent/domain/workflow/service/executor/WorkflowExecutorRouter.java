package dev.szhuima.agent.domain.workflow.service.executor;

import dev.szhuima.agent.domain.support.utils.SpringBeanUtils;
import dev.szhuima.agent.domain.workflow.model.NodeType;
import dev.szhuima.agent.domain.workflow.model.WorkflowNodeDO;

/**
 * * @Author: szhuima
 * * @Date    2025/9/30 15:36
 * * @Description
 **/
public interface WorkflowExecutorRouter {

    default WorkflowNodeExecutor getExecutor(WorkflowNodeDO node) {
        if (node.getType() == NodeType.HTTP_CALL) {
            return SpringBeanUtils.getBean(HttpCallNodeExecutor.class);
        } else if (node.getType() == NodeType.AGENT) {
            return SpringBeanUtils.getBean(AgentNodeExecutor.class);
        } else if (node.getType() == NodeType.BATCH) {
            return SpringBeanUtils.getBean(BatchNodeExecutor.class);
        } else {
            throw new UnsupportedOperationException("不支持的节点类型：" + node.getType());
        }
    }
}

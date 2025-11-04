package dev.szhuima.agent.domain.workflow.model;

import dev.szhuima.agent.domain.support.exception.BizException;
import dev.szhuima.agent.domain.workflow.model.dsl.WorkflowDsl;

import static dev.szhuima.agent.domain.workflow.model.dsl.WorkflowDsl.*;


/**
 * * @Author: szhuima
 * * @Date    2025/9/24 20:30
 * * @Description
 **/
public enum NodeType {
    START(StartNode.class, StartNode.StartConfig.class),
    HTTP(HttpNode.class, HttpNode.HttpConfig.class),
    CODE(CodeNode.class, CodeNode.CodeConfig.class),
    AGENT(AgentNode.class, AgentNode.AgentConfig.class),
    BATCH(BatchNode.class, BatchNode.BatchConfig.class);

    private final Class<? extends WorkflowDsl.BaseNode> nodeClass;
    private final Class configClass;

    NodeType(Class<? extends WorkflowDsl.BaseNode> nodeClass, Class configClass) {
        this.nodeClass = nodeClass;
        this.configClass = configClass;
    }

    public Class<? extends WorkflowDsl.BaseNode> getNodeClass() {
        return nodeClass;
    }

    public Class getConfigClass() {
        return configClass;
    }

    public static NodeType fromType(String type) {
        try {
            return NodeType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw BizException.of(type + "节点类型不存在");
        }
    }

}


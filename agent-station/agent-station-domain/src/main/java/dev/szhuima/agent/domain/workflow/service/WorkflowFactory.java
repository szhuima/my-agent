package dev.szhuima.agent.domain.workflow.service;

import com.alibaba.fastjson.JSON;
import dev.szhuima.agent.domain.workflow.model.*;
import dev.szhuima.agent.domain.workflow.model.dsl.WorkflowDslDO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/9/24 21:42
 * * @Description
 **/
@Service
public class WorkflowFactory {

    /**
     * 创建工作流实例
     *
     * @param workflowDO 工作流DO
     * @return 工作流实例
     */
    public WorkflowInstanceDO createWorkflowInstance(WorkflowDO workflowDO, Map<String, Object> params) {
        WorkflowInstanceDO instance = new WorkflowInstanceDO();
        instance.setWorkflowId(workflowDO.getWorkflowId());
        instance.setWorkflowName(workflowDO.getName());
        instance.setWorkflowDO(workflowDO);
        instance.setStatus(WorkflowInstanceStatus.DEPLOYED);
        instance.setNodeExecutionDOS(new ArrayList<>());
        return instance;
    }

    public String parseWorkflowName(String workflowDSL) {
        return WorkflowDslParser.parseNameFromYaml(workflowDSL);
    }


    public WorkflowDO parseDSL(String workflowDSL) {
        WorkflowDslDO workflowDslDO = WorkflowDslParser.parseFromYaml(workflowDSL);

        List<WorkflowDslDO.BaseTrigger<?>> triggers = workflowDslDO.getTriggers();
        List<WorkflowTriggerDO> workflowTriggerDOList = new ArrayList<>();
        for (WorkflowDslDO.BaseTrigger<?> trigger : triggers) {
            WorkflowTriggerDO workflowTriggerDO = new WorkflowTriggerDO();
            workflowTriggerDO.setTriggerType(trigger.getTriggerType());
            workflowTriggerDO.setConfig(JSON.toJSONString(trigger.getConfig()));
            workflowTriggerDOList.add(workflowTriggerDO);
        }

        WorkflowDO workflowDO = new WorkflowDO();
        workflowDO.setTriggers(workflowTriggerDOList);
        workflowDO.setName(workflowDslDO.getName());
        workflowDO.setMeta(workflowDslDO.getMeta());
        List<WorkflowDslDO.BaseNode<?>> nodes = workflowDslDO.getNodes();
        List<WorkflowNodeDO> workflowNodeDOList = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            WorkflowDslDO.BaseNode<?> node = nodes.get(i);
            WorkflowNodeDO workflowNodeDO = new WorkflowNodeDO();
            workflowNodeDO.setName(node.getName());
            workflowNodeDO.setType(NodeType.valueOf(node.getType()));
            workflowNodeDO.setConfigJson(JSON.toJSONString(node.getConfig()));
            workflowNodeDO.setConditionExpr(node.getCondition());
            workflowNodeDO.setPositionX(node.getPositionX());
            workflowNodeDO.setPositionY(node.getPositionY());
            workflowNodeDO.setStartNode(i == 0); // 第一个节点为起始节点
            workflowNodeDOList.add(workflowNodeDO);
        }
        workflowDO.setNodes(workflowNodeDOList);

        List<WorkflowDslDO.Edge> edges = workflowDslDO.getEdges();
        List<WorkflowEdgeDO> workflowEdgeDOList = new ArrayList<>();
        for (WorkflowDslDO.Edge edge : edges) {
            WorkflowEdgeDO workflowEdgeDO = new WorkflowEdgeDO();
            workflowEdgeDO.setFromNodeName(edge.getFrom());
            workflowEdgeDO.setToNodeName(edge.getTo());
            workflowEdgeDOList.add(workflowEdgeDO);
        }
        workflowDO.setEdges(workflowEdgeDOList);
        return workflowDO;
    }
}

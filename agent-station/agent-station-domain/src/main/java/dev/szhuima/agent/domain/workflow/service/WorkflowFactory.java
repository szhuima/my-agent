package dev.szhuima.agent.domain.workflow.service;

import com.alibaba.fastjson.JSON;
import dev.szhuima.agent.domain.workflow.model.NodeType;
import dev.szhuima.agent.domain.workflow.model.Workflow;
import dev.szhuima.agent.domain.workflow.model.WorkflowEdge;
import dev.szhuima.agent.domain.workflow.model.WorkflowNode;
import dev.szhuima.agent.domain.workflow.model.dsl.WorkflowDsl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/9/24 21:42
 * * @Description
 **/
@Service
public class WorkflowFactory {


    public Workflow parseYml(String ymlContent) throws Exception {
        WorkflowDsl workflowDsl = WorkflowYamlParser.parseFromYaml(ymlContent);
        Workflow workflow = new Workflow();
        workflow.setYmlConfig(ymlContent);
        workflow.setName(workflowDsl.getName());
        workflow.setMeta(workflowDsl.getMeta());
        List<WorkflowDsl.BaseNode<?>> nodes = workflowDsl.getNodes();
        List<WorkflowNode> workflowNodeList = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            WorkflowDsl.BaseNode<?> node = nodes.get(i);
            WorkflowNode workflowNode = new WorkflowNode();
            workflowNode.setName(node.getName());
            workflowNode.setType(NodeType.fromType(node.getType()));
            workflowNode.setConfigJson(JSON.toJSONString(node.getConfig()));
            workflowNode.setConditionExpr(node.getCondition());
            workflowNode.setPositionX(node.getPositionX());
            workflowNode.setPositionY(node.getPositionY());
            workflowNodeList.add(workflowNode);
        }
        workflow.setNodes(workflowNodeList);
        List<WorkflowDsl.Edge> edges = workflowDsl.getEdges();
        List<WorkflowEdge> workflowEdgeList = new ArrayList<>();
        for (WorkflowDsl.Edge edge : edges) {
            WorkflowEdge workflowEdge = new WorkflowEdge();
            workflowEdge.setFromNodeName(edge.getFrom());
            workflowEdge.setToNodeName(edge.getTo());
            workflowEdgeList.add(workflowEdge);
        }
        workflow.setEdges(workflowEdgeList);
        return workflow;
    }

    public Workflow parseJson(String jsonContent) throws Exception {
        return JSON.parseObject(jsonContent, Workflow.class);
    }


}

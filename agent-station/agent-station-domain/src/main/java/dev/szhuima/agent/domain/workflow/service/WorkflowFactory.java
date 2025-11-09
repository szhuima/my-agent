package dev.szhuima.agent.domain.workflow.service;

import com.alibaba.fastjson.JSON;
import dev.szhuima.agent.domain.workflow.model.NodeType;
import dev.szhuima.agent.domain.workflow.model.Workflow;
import dev.szhuima.agent.domain.workflow.model.WorkflowEdgeDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowNodeDO;
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


    public String parseWorkflowName(String workflowDSL) {
        return WorkflowDslParser.parseNameFromYaml(workflowDSL);
    }


    public Workflow parseDSL(String workflowDSL) throws Exception {
        WorkflowDsl workflowDsl = WorkflowDslParser.parseFromYaml(workflowDSL);
        Workflow workflow = new Workflow();
        workflow.setYmlConfig(workflowDSL);
        workflow.setName(workflowDsl.getName());
        workflow.setMeta(workflowDsl.getMeta());
        List<WorkflowDsl.BaseNode<?>> nodes = workflowDsl.getNodes();
        List<WorkflowNodeDO> workflowNodeDOList = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            WorkflowDsl.BaseNode<?> node = nodes.get(i);
            WorkflowNodeDO workflowNodeDO = new WorkflowNodeDO();
            workflowNodeDO.setName(node.getName());
            workflowNodeDO.setType(NodeType.fromType(node.getType()));
            workflowNodeDO.setConfigJson(JSON.toJSONString(node.getConfig()));
            workflowNodeDO.setConditionExpr(node.getCondition());
            workflowNodeDO.setPositionX(node.getPositionX());
            workflowNodeDO.setPositionY(node.getPositionY());
            workflowNodeDOList.add(workflowNodeDO);
        }
        workflow.setNodes(workflowNodeDOList);

        List<WorkflowDsl.Edge> edges = workflowDsl.getEdges();
        List<WorkflowEdgeDO> workflowEdgeDOList = new ArrayList<>();
        for (WorkflowDsl.Edge edge : edges) {
            WorkflowEdgeDO workflowEdgeDO = new WorkflowEdgeDO();
            workflowEdgeDO.setFromNodeName(edge.getFrom());
            workflowEdgeDO.setToNodeName(edge.getTo());
            workflowEdgeDOList.add(workflowEdgeDO);
        }
        workflow.setEdges(workflowEdgeDOList);
        return workflow;
    }
}

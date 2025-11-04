package dev.szhuima.agent.domain.workflow.service;

import com.alibaba.fastjson2.JSON;
import dev.szhuima.agent.domain.workflow.model.NodeType;
import dev.szhuima.agent.domain.workflow.model.dsl.WorkflowDsl;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class WorkflowDslParser {


    public static String parseNameFromYaml(String yamlText) {
        Yaml yaml = new Yaml();
        Object loaded = yaml.load(yamlText);
        if (!(loaded instanceof Map)) {
            throw new IllegalArgumentException("Invalid workflow yaml");
        }
        Map<String, Object> root = (Map<String, Object>) loaded;
        return root.get("name").toString();
    }


    public static WorkflowDsl parseFromYaml(String yamlText) throws Exception {
        Yaml yaml = new Yaml();
        Object loaded = yaml.load(yamlText);
        if (!(loaded instanceof Map)) {
            throw new IllegalArgumentException("Invalid workflow yaml");
        }
        Map<String, Object> root = (Map<String, Object>) loaded;

        WorkflowDsl dsl = new WorkflowDsl();
        dsl.setName((String) root.get("name"));
        Map<String,Object> metaMap = (Map<String,Object>)root.get("meta");
        dsl.setMeta(metaMap);

        List<Object> nodesRaw = (List<Object>) root.get("nodes");
        List<WorkflowDsl.BaseNode<?>> nodes = new ArrayList<>();
        if (nodesRaw != null) {
            for (Object n : nodesRaw) {
                if (!(n instanceof Map)) continue;
                Map<String, Object> nodeMap = (Map<String, Object>) n;
                String type = (String) nodeMap.get("type");
                NodeType nodeType = NodeType.fromType(type);
                String id = (String) nodeMap.get("id");
                String name = (String) nodeMap.get("name");
                String title = (String) nodeMap.get("title");
                String condition = (String) nodeMap.get("condition");
                Map<String, Object> configMap = (Map<String, Object>) nodeMap.get("config");
                String configJson = configMap == null ? null : JSON.toJSONString(configMap);

                if (configJson != null) {
                    WorkflowDsl.BaseNode newInstance = nodeType.getNodeClass().newInstance();

                    Object config = JSON.parseObject(configJson,nodeType.getConfigClass());
                    newInstance.setConfig(config);
                    newInstance.setId(id);
                    newInstance.setType(type);
                    newInstance.setName(name);
                    newInstance.setTitle(title);
                    newInstance.setCondition(condition);
                    nodes.add(newInstance);
                }
            }
        }
        dsl.setNodes(nodes);

        // 解析 edges
        List<Object> edgesRaw = (List<Object>) root.get("edges");
        List<WorkflowDsl.Edge> edges = new ArrayList<>();
        if (edgesRaw != null) {
            for (Object e : edgesRaw) {
                Map<String, Object> em = (Map<String, Object>) e;
                WorkflowDsl.Edge edge = new WorkflowDsl.Edge();
                edge.setFrom((String) em.get("from"));
                edge.setTo((String) em.get("to"));
                edges.add(edge);
            }
        }
        dsl.setEdges(edges);

        return dsl;
    }
}

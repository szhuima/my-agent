package dev.szhuima.agent.domain.workflow.service;

import com.alibaba.fastjson2.JSON;
import java.util.*;

import dev.szhuima.agent.domain.workflow.model.NodeType;
import dev.szhuima.agent.domain.workflow.model.TriggerType;
import dev.szhuima.agent.domain.workflow.model.dsl.WorkflowDslDO;
import org.yaml.snakeyaml.Yaml;

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


    public static WorkflowDslDO parseFromYaml(String yamlText) {
        Yaml yaml = new Yaml();
        Object loaded = yaml.load(yamlText);
        if (!(loaded instanceof Map)) {
            throw new IllegalArgumentException("Invalid workflow yaml");
        }
        Map<String, Object> root = (Map<String, Object>) loaded;

        WorkflowDslDO dsl = new WorkflowDslDO();
        dsl.setName((String) root.get("name"));
        Map<String,Object> metaMap = (Map<String,Object>)root.get("meta");
        dsl.setMeta(metaMap);

        List<Object> triggersRaw = (List<Object>) root.get("triggers");
        List<WorkflowDslDO.BaseTrigger<?>> triggers = new ArrayList<>();
        dsl.setTriggers(triggers);
        if (triggersRaw != null) {
            for (Object t : triggersRaw) {
                if (!(t instanceof Map)) continue;
                Map<String, Object> triggerMap = (Map<String, Object>) t;
                Object o = triggerMap.get("type");
                if (o == null) throw new IllegalArgumentException("Trigger type is null");
                TriggerType triggerType = TriggerType.valueOf((String) o);

                Object configObj = triggerMap.get("config");
                if (configObj == null) {
                    throw new IllegalArgumentException("Invalid trigger config, missing config");
                }
                String configJson = JSON.toJSONString(configObj);

                WorkflowDslDO.BaseTrigger trigger = null;
                switch (triggerType) {
                    case HTTP: {
                        trigger = new WorkflowDslDO.HttpTrigger();
                        trigger.setTriggerType(triggerType);
                        WorkflowDslDO.HttpTrigger.HttpConfig httpConfig = JSON.parseObject(configJson, WorkflowDslDO.HttpTrigger.HttpConfig.class);
                        trigger.setConfig(httpConfig);
                        break;
                    } case RABBITMQ: {
                        trigger = new WorkflowDslDO.RabbitmqTrigger();
                        trigger.setTriggerType(triggerType);
                        WorkflowDslDO.RabbitmqTrigger.RabbitmqConfig rabbitmqConfig = JSON.parseObject(configJson, WorkflowDslDO.RabbitmqTrigger.RabbitmqConfig.class);
                        trigger.setConfig(rabbitmqConfig);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Invalid trigger type: " + triggerType);
                    }
                }
                triggers.add(trigger);
            }
        }

        List<Object> nodesRaw = (List<Object>) root.get("nodes");
        List<WorkflowDslDO.BaseNode<?>> nodes = new ArrayList<>();
        if (nodesRaw != null) {
            for (Object n : nodesRaw) {
                if (!(n instanceof Map)) continue;
                Map<String, Object> nodeMap = (Map<String, Object>) n;
                String type = (String) nodeMap.get("type");

                NodeType nodeType = NodeType.valueOf(type);

                String id = (String) nodeMap.get("id");
                String name = (String) nodeMap.get("name");
                String title = (String) nodeMap.get("title");
                Boolean startNode = (Boolean) nodeMap.get("startNode");
                String condition = (String) nodeMap.get("condition");
                Map<String, Object> configMap = (Map<String, Object>) nodeMap.get("config");
                String configJson = configMap == null ? null : JSON.toJSONString(configMap);

                WorkflowDslDO.BaseNode node = null;

                switch (nodeType) {
                    case FORM: {
                        node = new WorkflowDslDO.FormNode();
                        if (configJson != null) {
                            WorkflowDslDO.FormNode.FormConfig cfg = JSON.parseObject(configJson, WorkflowDslDO.FormNode.FormConfig.class);
                            node.setConfig(cfg);
                        }
                        break;
                    }
                    case HTTP_CALL: {
                        node = new WorkflowDslDO.HttpNode();
                        if (configJson != null) {
                            WorkflowDslDO.HttpNode.HttpConfig cfg = JSON.parseObject(configJson, WorkflowDslDO.HttpNode.HttpConfig.class);
                            node.setConfig(cfg);
                        }
                        break;
                    }
                    case BATCH:
                    case LOOP: {
                        node = new WorkflowDslDO.BatchNode();
                        if (configJson != null) {
                            WorkflowDslDO.BatchNode.BatchConfig cfg = JSON.parseObject(configJson, WorkflowDslDO.BatchNode.BatchConfig.class);
                            node.setConfig(cfg);
                        }
                        break;
                    }
                    case AGENT: {
                        node = new WorkflowDslDO.AgentNode();
                        if (configJson != null) {
                            WorkflowDslDO.AgentNode.AgentConfig cfg = JSON.parseObject(configJson, WorkflowDslDO.AgentNode.AgentConfig.class);
                            node.setConfig(cfg);
                        }
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unsupported node type: " + nodeType);
                    }
                }
                node.setId(id);
                node.setType(type);
                node.setStartNode(startNode);
                node.setName(name);
                node.setTitle(title);
                node.setCondition(condition);
                nodes.add(node);

            }
        }
        dsl.setNodes(nodes);

        // 解析 edges
        List<Object> edgesRaw = (List<Object>) root.get("edges");
        List<WorkflowDslDO.Edge> edges = new ArrayList<>();
        if (edgesRaw != null) {
            for (Object e : edgesRaw) {
                Map<String, Object> em = (Map<String, Object>) e;
                WorkflowDslDO.Edge edge = new WorkflowDslDO.Edge();
                edge.setFrom((String) em.get("from"));
                edge.setTo((String) em.get("to"));
                edges.add(edge);
            }
        }
        dsl.setEdges(edges);

        return dsl;
    }
}

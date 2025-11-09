package dev.szhuima.agent.domain.workflow.model;

import dev.szhuima.agent.domain.support.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * * @Author: szhuima
 * * @Date    2025/9/24 20:47
 * * @Description
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {

    private Long workflowId;

    private String name;

    private Map<String, Object> meta;

    /**
     * 节点列表，key为节点ID
     */
    private List<WorkflowNodeDO> nodes;

    private List<WorkflowEdgeDO> edges;

    private String ymlConfig;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Workflow(Long id, String name, List<WorkflowNodeDO> nodeList, List<WorkflowEdgeDO> edges) {
        if (nodeList == null || edges == null) {
            throw BizException.of("节点和边不能为空");
        }
        this.workflowId = id;
        this.name = name;
        this.nodes = nodeList;
        this.edges = edges;
    }

    // 获取起始节点
    public WorkflowNodeDO findStartNode() {
        return nodes.stream()
                .filter((node) -> node.getType() == NodeType.START)
                .findFirst()
                .orElseThrow(() -> BizException.of("没有启动节点"));
    }

    public WorkflowNodeDO findNodeByName(String name) {
        return nodes.stream()
                .filter(n -> n.getName().equals(name))
                .findFirst()
                .orElse(null);
    }


    // 根据当前节点找到下一个节点（简单版，不考虑条件）
    public List<WorkflowNodeDO> nextNodes(Long nodeId) {
        return edges.stream()
                .filter(e -> e.getFromNodeId().equals(nodeId))
                .map(e -> nodes.stream()
                        .filter(n -> n.getNodeId().equals(e.getToNodeId()))
                        .findFirst()
                        .orElse(null))
                .collect(Collectors.toList());
    }


    public WorkflowNodeDO nextNode(String nodeName, String edgeLabel) {
        return edges.stream()
                .filter(e -> {
                    if (StringUtils.isNotEmpty(edgeLabel)) {
                        return e.getFromNodeName().equals(nodeName) && edgeLabel.equals(e.getLabel());
                    }
                    return e.getFromNodeName().equals(nodeName);
                })
                .map(e -> nodes.stream()
                        .filter(n -> n.getName().equals(e.getToNodeName()))
                        .findFirst()
                        .orElseThrow())
                .findFirst()
                .orElse(null);
    }


}

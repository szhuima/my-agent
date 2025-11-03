package dev.szhuima.agent.domain.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDateTime;

/**
 * 循环节点配置表
 * @TableName workflow_node_config_loop
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowNodeConfigLoopDO {
    /**
     * 循环节点配置主键
     */
    private Long id;

    /**
     * 所属工作流节点ID，关联 workflow_node 表
     */
    private Long nodeId;

    /**
     * 循环计数器在上下文中的变量名，比如 page
     */
    private String counterKey;

    /**
     * 计数器初始值
     */
    private Long startValue;

    /**
     * 计数器每轮递增值
     */
    private Long step;

    /**
     * 可选的循环最大值
     */
    private Long maxValue;

    /**
     * Aviator 表达式，用于判断是否继续循环，例如 httpResult.size == 30
     */
    private String conditionExpr;

    /**
     * 循环体节点ID列表，逗号分隔
     */
    private String loopBodyNodeIds;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
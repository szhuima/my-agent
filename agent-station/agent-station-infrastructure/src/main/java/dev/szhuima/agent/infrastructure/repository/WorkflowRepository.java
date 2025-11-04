package dev.szhuima.agent.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.szhuima.agent.domain.workflow.model.*;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowRepository;
import dev.szhuima.agent.infrastructure.entity.Workflow;
import dev.szhuima.agent.infrastructure.entity.WorkflowDsl;
import dev.szhuima.agent.infrastructure.entity.WorkflowEdge;
import dev.szhuima.agent.infrastructure.entity.WorkflowNode;
import dev.szhuima.agent.infrastructure.mapper.WorkflowDslMapper;
import dev.szhuima.agent.infrastructure.mapper.WorkflowEdgeMapper;
import dev.szhuima.agent.infrastructure.mapper.WorkflowMapper;
import dev.szhuima.agent.infrastructure.mapper.WorkflowNodeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/9/25 10:25
 * * @Description
 **/
@Repository
public class WorkflowRepository implements IWorkflowRepository {

    @Resource
    private WorkflowMapper workflowMapper;

    @Resource
    private WorkflowDslMapper workflowDslMapper;

    @Resource
    private WorkflowNodeMapper workflowNodeMapper;

    @Resource
    private WorkflowEdgeMapper workflowEdgeMapper;


    @Override
    public Long saveWorkflowDsl(Long workflowId, String dsl) {
        WorkflowDsl workflowDsl = new WorkflowDsl();
        workflowDsl.setWorkflowId(workflowId);
        workflowDsl.setContent(dsl);
        workflowDsl.setVersion(1);
        workflowDslMapper.insert(workflowDsl);
        return workflowId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveWorkflow(WorkflowDO workflowDO) {
        LambdaQueryWrapper<Workflow> queryWrapper = new LambdaQueryWrapper<Workflow>()
                .eq(Workflow::getName, workflowDO.getName())
                .orderByDesc(Workflow::getVersion);

        List<Workflow> oldWorkflowList = workflowMapper.selectList(queryWrapper);
        Integer version = 1;
        // 将之前的记录设置为非活动状态
        if (!oldWorkflowList.isEmpty()) {
            oldWorkflowList.stream()
                    .filter(oldWorkflow -> oldWorkflow.getStatus().equals(WorkflowStatus.ACTIVE.getCode()))
                    .forEach(oldWorkflow -> {
                        oldWorkflow.setStatus(WorkflowStatus.INACTIVE.getCode());
                        workflowMapper.updateById(oldWorkflow);
                    });
            version = oldWorkflowList.get(0).getVersion() + 1;
        }
        Workflow workflow = BeanUtil.copyProperties(workflowDO, Workflow.class, "nodes", "edges");
        if (workflowDO.getMeta() != null) {
            workflow.setMetaJson(JSON.toJSONString(workflowDO.getMeta()));
        }
        workflow.setVersion(version);
        workflowMapper.insert(workflow);
        return workflow.getWorkflowId();
    }


    @Override
    public Long saveWorkflowNode(WorkflowNodeDO workflowNode) {
        WorkflowNode workflowNode1 = BeanUtil.copyProperties(workflowNode, WorkflowNode.class);
        workflowNode1.setConditionExpr(workflowNode.getConditionExpr());
        workflowNodeMapper.insert(workflowNode1);
        return workflowNode1.getNodeId();
    }

    @Override
    public Long saveWorkflowEdge(WorkflowEdgeDO workflowEdge) {
        WorkflowEdge workflowEdge1 = BeanUtil.copyProperties(workflowEdge, WorkflowEdge.class);
        workflowEdgeMapper.insert(workflowEdge1);
        return workflowEdge1.getEdgeId();
    }

    @Override
    public WorkflowDO getById(Long workflowId) {
        Workflow workflow = workflowMapper.selectById(workflowId);
        if (workflow == null) {
            return null;
        }
        List<WorkflowNode> workflowNodes = workflowNodeMapper.selectList(new LambdaQueryWrapper<WorkflowNode>().eq(WorkflowNode::getWorkflowId, workflowId));
        List<WorkflowEdge> workflowEdgeList = workflowEdgeMapper.selectList(new LambdaQueryWrapper<WorkflowEdge>().eq(WorkflowEdge::getWorkflowId, workflowId));
        List<WorkflowNodeDO> workflowNodeDOList = BeanUtil.copyToList(workflowNodes, WorkflowNodeDO.class, CopyOptions.create().setConverter((targetType, value) -> {
            if (targetType == NodeType.class) {
                return NodeType.valueOf((String) value);
            }
            if (targetType == Boolean.class || targetType == boolean.class) {
                // 规则：只有值是 1 才表示 true
                if (value instanceof Number) {
                    return ((Number) value).intValue() == 1;
                }
                return false;
            }
            return value;
        }));

        List<WorkflowEdgeDO> workflowEdgeDOList = BeanUtil.copyToList(workflowEdgeList, WorkflowEdgeDO.class);

        return WorkflowDO.builder()
                .workflowId(workflow.getWorkflowId())
                .name(workflow.getName())
                .meta(JSON.parseObject(workflow.getMetaJson(), new TypeReference<Map<String, Object>>() {
                }))
                .nodes(workflowNodeDOList)
                .edges(workflowEdgeDOList)
                .createdAt(workflow.getCreatedAt())
                .updatedAt(workflow.getUpdatedAt())
                .build();

    }


    @Override
    public WorkflowDO getWorkflowByName(String workflowName) {
        Workflow workflow = workflowMapper.selectOne(new LambdaQueryWrapper<Workflow>()
                .eq(Workflow::getName, workflowName)
                .eq(Workflow::getStatus, WorkflowStatus.ACTIVE.getCode())
        );
        if (workflow == null) {
            return null;
        }

        WorkflowDO workflowDO = new WorkflowDO();
        BeanUtil.copyProperties(workflow, workflowDO);
        return workflowDO;
    }

    @Override
    public void deleteWorkflowByName(String workflowName) {
        List<Workflow> workflows = workflowMapper.selectList(new LambdaQueryWrapper<Workflow>()
                .select(Workflow::getWorkflowId)
                .eq(Workflow::getName, workflowName)
        );
        if (workflows.isEmpty()) {
            return;
        }
        List<Long> workflowIdList = workflows.stream().map(Workflow::getWorkflowId).toList();
        for (Long workflowId : workflowIdList) {
            workflowNodeMapper.delete(new LambdaQueryWrapper<WorkflowNode>().eq(WorkflowNode::getWorkflowId, workflowId));
            workflowEdgeMapper.delete(new LambdaQueryWrapper<WorkflowEdge>().eq(WorkflowEdge::getWorkflowId, workflowId));
        }
        workflowMapper.deleteBatchIds(workflowIdList);
    }
}

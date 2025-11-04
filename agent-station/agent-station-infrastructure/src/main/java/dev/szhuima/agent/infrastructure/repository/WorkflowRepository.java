package dev.szhuima.agent.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.szhuima.agent.domain.workflow.model.*;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowRepository;
import dev.szhuima.agent.infrastructure.entity.TbWorkflow;
import dev.szhuima.agent.infrastructure.entity.TbWorkflowDsl;
import dev.szhuima.agent.infrastructure.entity.TbWorkflowEdge;
import dev.szhuima.agent.infrastructure.entity.TbWorkflowNode;
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
        TbWorkflowDsl workflowDsl = new TbWorkflowDsl();
        workflowDsl.setWorkflowId(workflowId);
        workflowDsl.setContent(dsl);
        workflowDsl.setVersion(1);
        workflowDslMapper.insert(workflowDsl);
        return workflowId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveWorkflow(WorkflowDO workflowDO) {
        LambdaQueryWrapper<TbWorkflow> queryWrapper = new LambdaQueryWrapper<TbWorkflow>()
                .eq(TbWorkflow::getName, workflowDO.getName())
                .orderByDesc(TbWorkflow::getVersion);

        List<TbWorkflow> oldWorkflowList = workflowMapper.selectList(queryWrapper);
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
        TbWorkflow workflow = BeanUtil.copyProperties(workflowDO, TbWorkflow.class, "nodes", "edges");
        if (workflowDO.getMeta() != null) {
            workflow.setMetaJson(JSON.toJSONString(workflowDO.getMeta()));
        }
        workflow.setVersion(version);
        workflowMapper.insert(workflow);
        return workflow.getWorkflowId();
    }


    @Override
    public Long saveWorkflowNode(WorkflowNodeDO workflowNode) {
        TbWorkflowNode workflowNode1 = BeanUtil.copyProperties(workflowNode, TbWorkflowNode.class);
        workflowNode1.setConditionExpr(workflowNode.getConditionExpr());
        workflowNodeMapper.insert(workflowNode1);
        return workflowNode1.getNodeId();
    }

    @Override
    public Long saveWorkflowEdge(WorkflowEdgeDO workflowEdge) {
        TbWorkflowEdge workflowEdge1 = BeanUtil.copyProperties(workflowEdge, TbWorkflowEdge.class);
        workflowEdgeMapper.insert(workflowEdge1);
        return workflowEdge1.getEdgeId();
    }

    @Override
    public WorkflowDO getById(Long workflowId) {
        TbWorkflow workflow = workflowMapper.selectById(workflowId);
        if (workflow == null) {
            return null;
        }
        List<TbWorkflowNode> workflowNodes = workflowNodeMapper.selectList(new LambdaQueryWrapper<TbWorkflowNode>().eq(TbWorkflowNode::getWorkflowId, workflowId));
        List<TbWorkflowEdge> workflowEdgeList = workflowEdgeMapper.selectList(new LambdaQueryWrapper<TbWorkflowEdge>().eq(TbWorkflowEdge::getWorkflowId, workflowId));
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
        TbWorkflow workflow = workflowMapper.selectOne(new LambdaQueryWrapper<TbWorkflow>()
                .eq(TbWorkflow::getName, workflowName)
                .eq(TbWorkflow::getStatus, WorkflowStatus.ACTIVE.getCode())
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
        List<TbWorkflow> workflows = workflowMapper.selectList(new LambdaQueryWrapper<TbWorkflow>()
                .select(TbWorkflow::getWorkflowId)
                .eq(TbWorkflow::getName, workflowName)
        );
        if (workflows.isEmpty()) {
            return;
        }
        List<Long> workflowIdList = workflows.stream().map(TbWorkflow::getWorkflowId).toList();
        for (Long workflowId : workflowIdList) {
            workflowNodeMapper.delete(new LambdaQueryWrapper<TbWorkflowNode>().eq(TbWorkflowNode::getWorkflowId, workflowId));
            workflowEdgeMapper.delete(new LambdaQueryWrapper<TbWorkflowEdge>().eq(TbWorkflowEdge::getWorkflowId, workflowId));
        }
        workflowMapper.deleteBatchIds(workflowIdList);
    }
}

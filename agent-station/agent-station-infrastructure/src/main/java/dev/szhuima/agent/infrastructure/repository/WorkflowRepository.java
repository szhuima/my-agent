package dev.szhuima.agent.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.szhuima.agent.domain.workflow.model.*;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowRepository;
import dev.szhuima.agent.infrastructure.entity.TbWorkflow;
import dev.szhuima.agent.infrastructure.entity.TbWorkflowEdge;
import dev.szhuima.agent.infrastructure.entity.TbWorkflowNode;
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
    private WorkflowNodeMapper workflowNodeMapper;

    @Resource
    private WorkflowEdgeMapper workflowEdgeMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveWorkflow(Workflow workflow) {
        TbWorkflow tbWorkflow = BeanUtil.copyProperties(workflow, TbWorkflow.class, "nodes", "edges");
        if (workflow.getMeta() != null) {
            tbWorkflow.setMetaJson(JSON.toJSONString(workflow.getMeta()));
        }
        workflowMapper.insert(tbWorkflow);

        // 保存节点
        List<TbWorkflowNode> tbWorkflowNodes = BeanUtil.copyToList(workflow.getNodes(), TbWorkflowNode.class);
        tbWorkflowNodes.forEach(workflowNodeMapper::insert);
        // 保存边
        List<TbWorkflowEdge> tbWorkflowEdges = BeanUtil.copyToList(workflow.getEdges(), TbWorkflowEdge.class);
        tbWorkflowEdges.forEach(workflowEdgeMapper::insert);

        return tbWorkflow.getWorkflowId();
    }

    @Override
    public Workflow getById(Long workflowId) {
        TbWorkflow workflow = workflowMapper.selectById(workflowId);
        if (workflow == null) {
            return null;
        }
        List<TbWorkflowNode> workflowNodes = workflowNodeMapper.selectList(new LambdaQueryWrapper<TbWorkflowNode>().eq(TbWorkflowNode::getWorkflowId, workflowId));
        List<TbWorkflowEdge> workflowEdgeList = workflowEdgeMapper.selectList(new LambdaQueryWrapper<TbWorkflowEdge>().eq(TbWorkflowEdge::getWorkflowId, workflowId));
        List<WorkflowNode> workflowNodeList = BeanUtil.copyToList(workflowNodes, WorkflowNode.class, CopyOptions.create().setConverter((targetType, value) -> {
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

        List<WorkflowEdge> workflowEdgeDOList = BeanUtil.copyToList(workflowEdgeList, WorkflowEdge.class);

        return Workflow.builder()
                .workflowId(workflow.getWorkflowId())
                .name(workflow.getName())
                .meta(JSON.parseObject(workflow.getMetaJson(), new TypeReference<Map<String, Object>>() {
                }))
                .nodes(workflowNodeList)
                .edges(workflowEdgeDOList)
                .createdAt(workflow.getCreatedAt())
                .updatedAt(workflow.getUpdatedAt())
                .build();

    }


    @Override
    public Workflow getActiveWorkflow(String workflowName) {
        TbWorkflow workflow = workflowMapper.selectOne(new LambdaQueryWrapper<TbWorkflow>()
                .eq(TbWorkflow::getName, workflowName)
                .eq(TbWorkflow::getStatus, WorkflowStatus.ACTIVE.getCode())
        );
        if (workflow == null) {
            return null;
        }

        Workflow workflowDO = new Workflow();
        BeanUtil.copyProperties(workflow, workflowDO);
        return workflowDO;
    }


    @Override
    public void updateWorkflow(Workflow workflow) {
        TbWorkflow tbWorkflow = BeanUtil.copyProperties(workflow, TbWorkflow.class, "nodes", "edges");
        if (workflow.getMeta() != null) {
            tbWorkflow.setMetaJson(JSON.toJSONString(workflow.getMeta()));
        }
        tbWorkflow.setUpdatedAt(workflow.getUpdatedAt());
        workflowMapper.updateById(tbWorkflow);
    }
}

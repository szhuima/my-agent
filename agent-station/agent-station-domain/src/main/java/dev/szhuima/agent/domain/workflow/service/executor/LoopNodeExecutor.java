//package dev.szhuima.agent.domain.workflow.service.executor;
//
//import cn.hutool.core.lang.Assert;
//import com.googlecode.aviator.AviatorEvaluator;
//import dev.szhuima.agent.domain.workflow.model.WorkflowContext;
//import dev.szhuima.agent.domain.workflow.model.WorkflowDO;
//import dev.szhuima.agent.domain.workflow.model.WorkflowNodeConfigLoopDO;
//import dev.szhuima.agent.domain.workflow.model.WorkflowNodeDO;
//import dev.szhuima.agent.domain.workflow.reository.IWorkflowRepository;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import java.util.Map;
//
///**
// * * @Author: szhuima
// * * @Date    2025/9/26 17:02
// * * @Description
// **/
//@Slf4j
//@Service
//public class LoopNodeExecutor extends AbstractNodeExecutor {
//
//    @Resource
//    private IWorkflowRepository workflowRepository;
//
//    @Override
//    public NodeExecutionResult executeNode(WorkflowNodeDO node, WorkflowContext context, WorkflowDO workflowDO) {
//        WorkflowNodeConfigLoopDO loopNode = workflowRepository.getLoopConfigNode(node.getWorkflowId());
//        if (loopNode == null) {
//            log.error("Loop node config not found, workflowId: {}, nodeId: {}", node.getWorkflowId(), node.getNodeId());
//            return NodeExecutionResult.failure("Loop node config not found, workflowId: " + node.getWorkflowId() + ", nodeId: " + node.getNodeId());
//        }
//
//        String loopBodyNodeIds = loopNode.getLoopBodyNodeIds();
//        String[] bodyNodeIds = loopBodyNodeIds.split("[,，]");
//        Assert.isTrue(!Arrays.asList(bodyNodeIds).contains(node.getNodeId().toString()), "Loop body node ids must not contain loop node id");
//
//
//        while (true) {
//            // 将该循环变量放入全局上下文中
//            context.putGlobalValue(Map.of(loopNode.getCounterKey(), loopNode.getStartValue()));
//
//            for (String bodyNodeId : bodyNodeIds) {
//                WorkflowNodeDO bodyNode = workflowRepository.getNodeById(Long.parseLong(bodyNodeId));
//                if (bodyNode == null) {
//                    log.error("Loop body node not found, workflowId: {}, nodeId: {}", node.getWorkflowId(), bodyNodeId);
//                    return NodeExecutionResult.failure("Loop body node not found, workflowId: " + node.getWorkflowId() + ", nodeId: " + bodyNodeId);
//                }
//                NodeExecutionResult bodyNodeResult = executeNode(bodyNode, context, workflowDO);
//                if (!bodyNodeResult.isCompleted()) {
//                    log.error("Loop body node execution failed, workflowId: {}, nodeId: {}, error: {}", node.getWorkflowId(), bodyNodeId, bodyNodeResult.getReason());
//                    return NodeExecutionResult.failure("Loop body node execution failed, workflowId: " + node.getWorkflowId() + ", nodeId: " + bodyNodeId + ", error: " + bodyNodeResult.getReason());
//                }
//            }
//
//            // 检查循环条件是否满足
//            if (StringUtils.isNoneBlank(loopNode.getConditionExpr())) {
//                Boolean continueLoop = (Boolean) AviatorEvaluator.execute(loopNode.getConditionExpr(), context.getAll());
//                if (!continueLoop) {
//                    break;
//                }
//            }
//            // 更新循环变量
//            Long currentValue = (Long) context.getGlobalScope().get(loopNode.getCounterKey());
//            context.putGlobalValue(Map.of(loopNode.getCounterKey(), currentValue + loopNode.getStep()));
//        }
//
//        return NodeExecutionResult.success(null);
//}
//}

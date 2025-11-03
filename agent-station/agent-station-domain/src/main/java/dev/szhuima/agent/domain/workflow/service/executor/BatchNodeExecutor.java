package dev.szhuima.agent.domain.workflow.service.executor;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONReader;
import dev.szhuima.agent.domain.workflow.model.WorkflowContext;
import dev.szhuima.agent.domain.workflow.model.WorkflowDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowNodeConfigBatchDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowNodeDO;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/9/25 20:37
 * * @Description
 **/
@Slf4j
@Service
public class BatchNodeExecutor extends AbstractNodeExecutor implements WorkflowExecutorRouter{


    @Resource
    private IWorkflowRepository workflowRepository;

    /**
     * 执行节点
     *
     * @param node    节点配置
     * @param context 当前工作流上下文
     * @return 执行结果
     */
    @Override
    public NodeExecutionResult executeNode(WorkflowNodeDO node, WorkflowContext context, WorkflowDO workflowDO) {
        String configJson = node.getConfigJson();
        WorkflowNodeConfigBatchDO batchConfigNode = JSON.parseObject(configJson, WorkflowNodeConfigBatchDO.class, JSONReader.Feature.SupportSmartMatch);
        if (batchConfigNode == null) {
            throw new IllegalArgumentException("未找到批量节点配置,node: " + node.getName());
        }
        Object value = context.getJSONPath(batchConfigNode.getItems());
        if (value == null) {
            throw new IllegalArgumentException("上下文中无可遍历元素,path: " + batchConfigNode.getItems());
        }
        if (value instanceof JSONArray list) {
            for (Object o : list) {
                context.putJSONPath(batchConfigNode.getItemKey(), o);
            }
            List<String> bodyNodes = batchConfigNode.getBodyNodes();
            for (String nodeName : bodyNodes) {
                WorkflowNodeDO bodyNodeDO = workflowDO.findNodeByName(nodeName);
                WorkflowNodeExecutor executor = getExecutor(bodyNodeDO);
                log.info("正在执行bodyNode:{}", nodeName);
                NodeExecutionResult result = executor.execute(bodyNodeDO, context, workflowDO);
                if (!result.isCompleted()) {
                    if (batchConfigNode.getErrorStrategy() == WorkflowNodeConfigBatchDO.ErrorStrategy.BREAK) {
                        log.info("bodyNode:{} is break,result:{}", nodeName,result);
                        return result;
                    }
                }
            }
            return NodeExecutionResult.success(null);
        }
        throw new IllegalArgumentException("上下文中可遍历元素类型错误,path: " + batchConfigNode.getItems());
    }
}

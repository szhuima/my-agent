package dev.szhuima.agent.domain.workflow.service.executor;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import dev.szhuima.agent.domain.agent.Agent;
import dev.szhuima.agent.domain.agent.factory.AgentBeanFactory;
import dev.szhuima.agent.domain.agent.repository.IAgentRepository;
import dev.szhuima.agent.domain.workflow.model.WorkflowContext;
import dev.szhuima.agent.domain.workflow.model.WorkflowDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowNodeAgentConfig;
import dev.szhuima.agent.domain.workflow.model.WorkflowNodeDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 智能体节点
 * * @Author: szhuima
 * * @Date    2025/9/25 16:35
 * * @Description
 **/
@Slf4j
@Service
public class AgentNodeExecutor extends AbstractNodeExecutor {


    @Resource
    private IAgentRepository agentRepository;

    @Resource
    private AgentBeanFactory agentBeanFactory;

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
        WorkflowNodeAgentConfig config = JSON.parseObject(configJson, WorkflowNodeAgentConfig.class,
                JSONReader.Feature.SupportSmartMatch);

        Long clientId = config.getClientId();

        List<Agent> agents = agentRepository.queryAgentList(List.of(clientId));

        if (agents.isEmpty()) {
            log.error("未找到客户端配置，clientId：{}", clientId);
            return NodeExecutionResult.failure("未找到客户端配置");
        }
        Agent agent = agents.get(0);

        ChatClient chatClient = agentBeanFactory.getChatClient(clientId);

        String userMessage = config.getUserMessage();
        String sessionId = StringUtils.isEmpty(config.getSessionId()) ? IdUtil.simpleUUID() : config.getSessionId();

        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId));

        if (!StringUtils.isEmpty(agent.getSystemPrompt())) {
            String systemPrompt = render(agent.getSystemPrompt(), context.getAll());
            requestSpec = requestSpec.system(systemPrompt);
        }

        if (!StringUtils.isEmpty(userMessage)) {
            userMessage = render(userMessage, context.getAll());
            requestSpec = requestSpec.user(userMessage);
        }

        String response = requestSpec
                .call()
                .content();

        return NodeExecutionResult.success(response);
    }
}

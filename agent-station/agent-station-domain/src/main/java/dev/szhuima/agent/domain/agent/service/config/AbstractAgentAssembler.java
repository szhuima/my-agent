package dev.szhuima.agent.domain.agent.service.config;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
import dev.szhuima.agent.domain.agent.repository.IAgentRepository;
import dev.szhuima.agent.domain.agent.service.config.factory.AgentBeanFactory;
import dev.szhuima.agent.domain.agent.service.config.handler.AgentConfigLoadHandler;
import dev.szhuima.agent.domain.support.chain.AbstractHandler;
import dev.szhuima.agent.domain.support.chain.ChainContext;
import dev.szhuima.agent.domain.support.chain.HandleResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

/**
 * 抽象的Agent装配处理类
 * * @Author: szhuima
 * * @Date    2025/9/11 17:08
 * * @Description
 **/
@Slf4j
public abstract class AbstractAgentAssembler extends AbstractHandler<AgentAssemblyInput, Void> {

    public static final String AGENT_CLIENT_MODES_KEY = "agentClientModes";
    public static final String AGENT_CLIENT_MCPS_KEY = "agentClientMcps";
    public static final String AGENT_CLIENT_ADVISORS_KEY = "agentClientAdvisors";
    public static final String AGENT_CLIENTS_KEY = "agentClients";

    @Resource
    protected AgentBeanFactory agentBeanFactory;

    @Resource(name = "ttlExecutor")
    protected Executor threadPoolExecutor;

    @Resource
    protected IAgentRepository repository;


    /**
     * 处理Agent装配输入，根据assemblerName判断是否处理
     *
     * @param ctx
     * @param param
     * @return
     */
    @Override
    public HandleResult<Void> handle(ChainContext ctx, AgentAssemblyInput param) {
        String assemblerName = param.getAssemblerName();
        if (StrUtil.isNotEmpty(assemblerName)
                && !AgentConfigLoadHandler.class.getSimpleName().equals(name())
                && !assemblerName.equals(name())) {
            log.info("模型客户端构建, 不处理, {},{}", name(), JSON.toJSONString(param));
            return proceed(ctx, param, HandleResult.keepGoing());
        }
        log.info("模型客户端构建, {},{}", name(), JSON.toJSONString(param));
        return doHandle(ctx, param);
    }

    public abstract HandleResult<Void> doHandle(ChainContext ctx, AgentAssemblyInput param);

}

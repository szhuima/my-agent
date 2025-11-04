package dev.szhuima.agent.domain.agent.service;

import dev.szhuima.agent.domain.agent.model.AgentExecuteParams;
import dev.szhuima.agent.domain.support.utils.StringTemplateRender;

/**
 * * @Author: szhuima
 * * @Date    2025/11/4 15:19
 * * @Description
 **/
public interface AgentExecutor extends StringTemplateRender {

    <T> T executeTask(AgentExecuteParams params, Class<T> returnType);

}

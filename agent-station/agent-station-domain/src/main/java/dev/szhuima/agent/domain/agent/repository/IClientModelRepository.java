package dev.szhuima.agent.domain.agent.repository;

import dev.szhuima.agent.domain.agent.model.ModelApi;

/**
 * * @Author: szhuima
 * * @Date    2025/10/18 16:43
 * * @Description
 **/
public interface IClientModelRepository {

    ModelApi getClientModelById(Long modelId);

}

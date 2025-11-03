package dev.szhuima.agent.domain.agent.repository;

import dev.szhuima.agent.domain.agent.model.valobj.AiClientModelVO;

/**
 * * @Author: szhuima
 * * @Date    2025/10/18 16:43
 * * @Description
 **/
public interface IClientModelRepository {

    AiClientModelVO getClientModelById(Long modelId);

}

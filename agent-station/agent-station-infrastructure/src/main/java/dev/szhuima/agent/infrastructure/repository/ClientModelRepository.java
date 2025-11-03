package dev.szhuima.agent.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientModelVO;
import dev.szhuima.agent.domain.agent.repository.IClientModelRepository;
import dev.szhuima.agent.infrastructure.mapper.AiClientModelMapper;
import dev.szhuima.agent.infrastructure.po.AiClientModel;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

/**
 * * @Author: szhuima
 * * @Date    2025/10/18 16:42
 * * @Description
 **/
@Repository
public class ClientModelRepository implements IClientModelRepository {

    @Resource
    private AiClientModelMapper modelMapper;

    @Override
    public AiClientModelVO getClientModelById(Long modelId) {
        AiClientModel aiClientModel = modelMapper.selectById(modelId);
        if (aiClientModel == null) return null;
        AiClientModelVO aiClientModelVO = BeanUtil.copyProperties(aiClientModel, AiClientModelVO.class);
        return aiClientModelVO;
    }
}

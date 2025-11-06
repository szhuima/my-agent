package dev.szhuima.agent.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import dev.szhuima.agent.domain.agent.model.ModelApi;
import dev.szhuima.agent.domain.agent.repository.IModelApiRepository;
import dev.szhuima.agent.infrastructure.entity.TbModelApi;
import dev.szhuima.agent.infrastructure.mapper.ModelApiMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

/**
 * * @Author: szhuima
 * * @Date    2025/10/18 16:42
 * * @Description
 **/
@Repository
public class ModelApiRepository implements IModelApiRepository {

    @Resource
    private ModelApiMapper modelMapper;

    @Override
    public ModelApi getModelApi(Long modelId) {
        TbModelApi tbModelApi = modelMapper.selectById(modelId);
        if (tbModelApi == null) return null;
        ModelApi aiClientModelVO = BeanUtil.copyProperties(tbModelApi, ModelApi.class);
        return aiClientModelVO;
    }
}

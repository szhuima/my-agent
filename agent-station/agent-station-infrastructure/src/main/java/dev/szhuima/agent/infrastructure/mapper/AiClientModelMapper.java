package dev.szhuima.agent.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.szhuima.agent.infrastructure.po.AiClientModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author jack
* @description 针对表【ai_client_model(AI接口模型配置表)】的数据库操作Mapper
* @createDate 2025-09-11 15:44:06
* @Entity dev.szhuima.agent.infrastructure.po.AiClientModel
*/
@Mapper
public interface AiClientModelMapper extends BaseMapper<AiClientModel> {



    /**
     * 根据ID查询模型配置
     * @param id 模型配置ID
     * @return 模型配置
     */
    AiClientModel queryModelConfigById(Long id);

    /**
     * 根据模型名称查询模型配置
     * @param modelName 模型名称
     * @return 模型配置
     */
    AiClientModel queryModelConfigByName(String modelName);



    /**
     * 根据客户端ID列表查询模型配置
     * @param clientIdList 客户端ID列表
     * @return 模型配置列表
     */
    List<AiClientModel> queryModelConfigByClientIds(List<Long> clientIdList);

    /**
     * 根据条件查询客户端模型列表
     * @param aiClientModel 查询条件
     * @return 客户端模型列表
     */
    List<AiClientModel> queryClientModelList(AiClientModel aiClientModel);



}





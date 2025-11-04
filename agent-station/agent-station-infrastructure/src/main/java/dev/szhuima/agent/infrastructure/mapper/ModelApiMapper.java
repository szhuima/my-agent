package dev.szhuima.agent.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.szhuima.agent.infrastructure.entity.TbModelApi;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jack
 * @description 针对表【ai_client_model(AI接口模型配置表)】的数据库操作Mapper
 * @createDate 2025-09-11 15:44:06
 * @Entity dev.szhuima.agent.infrastructure.po.AiClientModel
 */
@Mapper
public interface ModelApiMapper extends BaseMapper<TbModelApi> {


}





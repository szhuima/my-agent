package dev.szhuima.agent.infrastructure.mapper;

import dev.szhuima.agent.infrastructure.po.AiClient;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author jack
* @description 针对表【ai_client(AI客户端配置表)】的数据库操作Mapper
* @createDate 2025-09-11 15:42:45
* @Entity dev.szhuima.agent.infrastructure.po.AiClient
*/
@Mapper
public interface AiClientMapper extends BaseMapper<AiClient> {

    /**
     * 根据客户端ID更新AI客户端配置
     * @param aiClient AI客户端配置对象
     * @return 影响行数
     */
    int updateByClientId(AiClient aiClient);

    /**
     * 根据ID删除AI客户端配置
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据客户端ID删除AI客户端配置
     * @param clientId 客户端ID
     * @return 影响行数
     */
    int deleteByClientId(String clientId);

    /**
     * 根据ID查询AI客户端配置
     * @param id 主键ID
     * @return AI客户端配置对象
     */
    AiClient queryById(Long id);

    /**
     * 根据客户端ID查询AI客户端配置
     * @param clientId 客户端ID
     * @return AI客户端配置对象
     */
    AiClient queryByClientId(String clientId);

    /**
     * 查询所有启用的AI客户端配置
     * @return AI客户端配置列表
     */
    List<AiClient> queryEnabledClients();

    /**
     * 根据客户端名称查询AI客户端配置
     * @param clientName 客户端名称
     * @return AI客户端配置列表
     */
    List<AiClient> queryByClientName(String clientName);

    /**
     * 查询所有AI客户端配置
     * @return AI客户端配置列表
     */
    List<AiClient> queryAll();

}





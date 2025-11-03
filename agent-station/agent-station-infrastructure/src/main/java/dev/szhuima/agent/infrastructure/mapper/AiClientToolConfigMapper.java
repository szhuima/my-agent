package dev.szhuima.agent.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.szhuima.agent.infrastructure.po.AiClientToolConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author jack
* @description 针对表【ai_client_tool_config(客户端-MCP关联表)】的数据库操作Mapper
* @createDate 2025-09-11 15:44:06
* @Entity dev.szhuima.agent.infrastructure.po.AiClientToolConfig
*/
@Mapper
public interface AiClientToolConfigMapper extends BaseMapper<AiClientToolConfig> {

    /**
     * 查询所有客户端工具配置
     * @return 客户端工具配置列表
     */
    List<AiClientToolConfig> queryAllToolConfig();

    /**
     * 根据ID查询客户端工具配置
     * @param id 客户端工具配置ID
     * @return 客户端工具配置
     */
    AiClientToolConfig queryToolConfigById(Long id);

    /**
     * 根据客户端ID查询工具配置列表
     * @param clientId 客户端ID
     * @return 客户端工具配置列表
     */
    List<AiClientToolConfig> queryToolConfigByClientId(Long clientId);

    /**
     * 插入客户端工具配置
     * @param aiClientToolConfig 客户端工具配置
     * @return 影响行数
     */
    int insert(AiClientToolConfig aiClientToolConfig);

    /**
     * 更新客户端工具配置
     * @param aiClientToolConfig 客户端工具配置
     * @return 影响行数
     */
    int update(AiClientToolConfig aiClientToolConfig);

    /**
     * 根据ID删除客户端工具配置
     * @param id 客户端工具配置ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据客户端ID和工具ID删除客户端工具配置
     * @param clientId 客户端ID
     * @param toolId 工具ID
     * @return 影响行数
     */
    int deleteByClientIdAndToolId(@Param("clientId") Long clientId, @Param("toolId") Long toolId);

    /**
     * 根据客户端ID列表查询工具配置
     * @param clientIdList 客户端ID列表
     * @return 客户端工具配置列表
     */
    List<AiClientToolConfig> queryToolConfigByClientIds(@Param("clientIdList") List<Long> clientIdList);

    /**
     * 根据工具ID查询客户端工具配置列表
     * @param toolId 工具ID
     * @return 客户端工具配置列表
     */
    List<AiClientToolConfig> queryToolConfigByToolId(Long toolId);


    List<AiClientToolConfig> queryToolConfigList(AiClientToolConfig aiClientToolConfig);


}





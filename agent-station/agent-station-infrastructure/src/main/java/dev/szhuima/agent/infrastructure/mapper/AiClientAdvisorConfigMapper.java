package dev.szhuima.agent.infrastructure.mapper;

import dev.szhuima.agent.infrastructure.po.AiClientAdvisorConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author jack
* @description 针对表【ai_client_advisor_config(客户端-顾问关联表)】的数据库操作Mapper
* @createDate 2025-09-11 15:44:06
* @Entity dev.szhuima.agent.infrastructure.po.AiClientAdvisorConfig
*/
@Mapper
public interface AiClientAdvisorConfigMapper extends BaseMapper<AiClientAdvisorConfig> {

    /**
     * 查询所有客户端-顾问关联配置
     * @return 客户端-顾问关联配置列表
     */
    List<AiClientAdvisorConfig> queryAllClientAdvisorConfig();

    /**
     * 根据ID查询客户端-顾问关联配置
     * @param id 关联配置ID
     * @return 客户端-顾问关联配置
     */
    AiClientAdvisorConfig queryClientAdvisorConfigById(Long id);

    /**
     * 根据客户端ID查询关联配置
     * @param clientId 客户端ID
     * @return 客户端-顾问关联配置列表
     */
    List<AiClientAdvisorConfig> queryClientAdvisorConfigByClientId(Long clientId);

    /**
     * 根据顾问ID查询关联配置
     * @param advisorId 顾问ID
     * @return 客户端-顾问关联配置列表
     */
    List<AiClientAdvisorConfig> queryClientAdvisorConfigByAdvisorId(Long advisorId);

    /**
     * 插入客户端-顾问关联配置
     * @param aiClientAdvisorConfig 客户端-顾问关联配置
     * @return 影响行数
     */
    int insert(AiClientAdvisorConfig aiClientAdvisorConfig);

    /**
     * 更新客户端-顾问关联配置
     * @param aiClientAdvisorConfig 客户端-顾问关联配置
     * @return 影响行数
     */
    int update(AiClientAdvisorConfig aiClientAdvisorConfig);

    /**
     * 根据ID删除客户端-顾问关联配置
     * @param id 关联配置ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据客户端ID和顾问ID删除关联配置
     * @param clientId 客户端ID
     * @param advisorId 顾问ID
     * @return 影响行数
     */
    int deleteByClientIdAndAdvisorId(@Param("clientId") Long clientId, @Param("advisorId") Long advisorId);

    /**
     * 根据客户端ID列表查询关联配置
     * @param clientIdList 客户端ID列表
     * @return 客户端-顾问关联配置列表
     */
    List<AiClientAdvisorConfig> queryClientAdvisorConfigByClientIds(@Param("clientIdList") List<Long> clientIdList);

    /**
     * 根据客户端ID列表查询顾问ID列表
     * @param clientIdList 客户端ID列表
     * @return 顾问ID列表
     */
    List<Long> queryAdvisorIdsByClientIds(@Param("clientIdList") List<Long> clientIdList);

    List<AiClientAdvisorConfig> queryClientAdvisorConfigList(AiClientAdvisorConfig aiClientAdvisorConfig);



}





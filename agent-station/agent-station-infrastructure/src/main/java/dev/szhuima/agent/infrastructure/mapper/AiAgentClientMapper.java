package dev.szhuima.agent.infrastructure.mapper;

import dev.szhuima.agent.infrastructure.po.AiAgentClient;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author jack
* @description 针对表【ai_agent_client(智能体-客户端关联表)】的数据库操作Mapper
* @createDate 2025-09-11 15:42:45
* @Entity dev.szhuima.agent.infrastructure.po.AiAgentClient
*/
@Mapper
public interface AiAgentClientMapper extends BaseMapper<AiAgentClient> {

    /**
     * 查询所有智能体-客户端关联配置
     * @return 关联配置列表
     */
    List<AiAgentClient> queryAllAgentClientConfig();

    /**
     * 根据ID查询智能体-客户端关联配置
     * @param id 主键ID
     * @return 关联配置
     */
    AiAgentClient queryAgentClientConfigById(Long id);

    /**
     * 根据智能体ID查询关联配置
     * @param agentId 智能体ID
     * @return 关联配置列表
     */
    List<AiAgentClient> queryAgentClientConfigByAgentId(Long agentId);

    /**
     * 根据客户端ID查询关联配置
     * @param clientId 客户端ID
     * @return 关联配置列表
     */
    List<AiAgentClient> queryAgentClientConfigByClientId(Long clientId);

    /**
     * 根据智能体ID和客户端ID查询关联配置
     * @param agentId 智能体ID
     * @param clientId 客户端ID
     * @return 关联配置列表
     */
    List<AiAgentClient> queryAgentClientConfigByAgentIdAndClientId(Long agentId, Long clientId);

    /**
     * 插入智能体-客户端关联配置
     * @param aiAgentClient 关联配置
     * @return 影响行数
     */
    int insert(AiAgentClient aiAgentClient);

    /**
     * 更新智能体-客户端关联配置
     * @param aiAgentClient 关联配置
     * @return 影响行数
     */
    int update(AiAgentClient aiAgentClient);

    /**
     * 根据ID删除智能体-客户端关联配置
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据智能体ID删除关联配置
     * @param agentId 智能体ID
     * @return 影响行数
     */
    int deleteByAgentId(Long agentId);

    /**
     * 根据客户端ID删除关联配置
     * @param clientId 客户端ID
     * @return 影响行数
     */
    int deleteByClientId(Long clientId);

    /**
     * 根据智能体ID和客户端ID删除关联配置
     * @param agentId 智能体ID
     * @param clientId 客户端ID
     * @return 影响行数
     */
    int deleteByAgentIdAndClientId(Long agentId, Long clientId);

    /**
     * 根据条件查询智能体-客户端关联配置列表
     * @param aiAgentClient 查询条件
     * @return 关联配置列表
     */
    List<AiAgentClient> queryAgentClientList(AiAgentClient aiAgentClient);

}





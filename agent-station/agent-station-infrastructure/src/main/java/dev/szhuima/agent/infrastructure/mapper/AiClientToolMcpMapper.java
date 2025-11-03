package dev.szhuima.agent.infrastructure.mapper;

import dev.szhuima.agent.infrastructure.po.AiClientToolMcp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author jack
* @description 针对表【ai_client_tool_mcp(MCP客户端配置表)】的数据库操作Mapper
* @createDate 2025-09-11 15:44:06
* @Entity dev.szhuima.agent.infrastructure.po.AiClientToolMcp
*/
@Mapper
public interface AiClientToolMcpMapper extends BaseMapper<AiClientToolMcp> {

    /**
     * 查询所有MCP配置
     * @return MCP配置列表
     */
    List<AiClientToolMcp> queryAllMcpConfig();

    /**
     * 根据ID查询MCP配置
     * @param id MCP配置ID
     * @return MCP配置
     */
    AiClientToolMcp queryMcpConfigById(Long id);

    /**
     * 根据MCP名称查询配置
     * @param mcpName MCP名称
     * @return MCP配置
     */
    AiClientToolMcp queryMcpConfigByName(String mcpName);


    /**
     * 更新MCP配置
     * @param aiClientToolMcp MCP配置
     * @return 影响行数
     */
    int update(AiClientToolMcp aiClientToolMcp);


    /**
     * 根据客户端ID列表查询MCP配置
     * @param clientIdList 客户端ID列表
     * @return MCP配置列表
     */
    List<AiClientToolMcp> queryMcpConfigByClientIds(List<Long> clientIdList);

    List<AiClientToolMcp> queryMcpList(AiClientToolMcp aiClientToolMcp);


    /**
     * 插入MCP客户端配置
     * @param aiClientToolMcp MCP客户端配置对象
     * @return 影响行数
     */
    int insert(AiClientToolMcp aiClientToolMcp);

    /**
     * 根据ID更新MCP客户端配置
     * @param aiClientToolMcp MCP客户端配置对象
     * @return 影响行数
     */
    int updateById(AiClientToolMcp aiClientToolMcp);

    /**
     * 根据MCP ID更新MCP客户端配置
     * @param aiClientToolMcp MCP客户端配置对象
     * @return 影响行数
     */
    int updateByMcpId(AiClientToolMcp aiClientToolMcp);

    /**
     * 根据ID删除MCP客户端配置
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据MCP ID删除MCP客户端配置
     * @param mcpId MCP ID
     * @return 影响行数
     */
    int deleteByMcpId(String mcpId);

    /**
     * 根据ID查询MCP客户端配置
     * @param id 主键ID
     * @return MCP客户端配置对象
     */
    AiClientToolMcp queryById(Long id);

    /**
     * 根据MCP ID查询MCP客户端配置
     * @param mcpId MCP ID
     * @return MCP客户端配置对象
     */
    AiClientToolMcp queryByMcpId(String mcpId);

    /**
     * 查询所有MCP客户端配置
     * @return MCP客户端配置列表
     */
    List<AiClientToolMcp> queryAll();

    /**
     * 根据状态查询MCP客户端配置
     * @param status 状态
     * @return MCP客户端配置列表
     */
    List<AiClientToolMcp> queryByStatus(Integer status);

    /**
     * 根据传输类型查询MCP客户端配置
     * @param transportType 传输类型
     * @return MCP客户端配置列表
     */
    List<AiClientToolMcp> queryByTransportType(String transportType);

    /**
     * 查询启用的MCP客户端配置
     * @return MCP客户端配置列表
     */
    List<AiClientToolMcp> queryEnabledMcps();


}





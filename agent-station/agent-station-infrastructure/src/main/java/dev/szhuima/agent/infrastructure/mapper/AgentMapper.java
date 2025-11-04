package dev.szhuima.agent.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.szhuima.agent.infrastructure.entity.TbAgent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface AgentMapper extends BaseMapper<TbAgent> {


    TbAgent queryById(Long id);

    /**
     * 根据客户端ID查询AI客户端配置
     *
     * @param clientId 客户端ID
     * @return AI客户端配置对象
     */
    TbAgent queryByClientId(String clientId);

    /**
     * 查询所有启用的AI客户端配置
     *
     * @return AI客户端配置列表
     */
    List<TbAgent> queryEnabledClients();


    /**
     * 查询所有AI客户端配置
     *
     * @return AI客户端配置列表
     */
    List<TbAgent> queryAll();

}





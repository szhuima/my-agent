package dev.szhuima.agent.infrastructure.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.szhuima.agent.infrastructure.entity.TbMcp;
import dev.szhuima.agent.infrastructure.mapper.McpMapper;
import dev.szhuima.agent.infrastructure.service.TbMcpService;
import org.springframework.stereotype.Service;

/**
* @author jack
* @description 针对表【tb_mcp(MCP客户端配置表)】的数据库操作Service实现
* @createDate 2025-11-04 18:59:10
*/
@Service
public class TbMcpServiceImpl extends ServiceImpl<McpMapper, TbMcp>
    implements TbMcpService{

}





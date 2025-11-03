package dev.szhuima.agent.infrastructure.mapper;

import dev.szhuima.agent.infrastructure.po.WorkflowNodeConfigLoop;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jack
* @description 针对表【workflow_node_config_loop(循环节点配置表)】的数据库操作Mapper
* @createDate 2025-09-26 17:01:41
* @Entity dev.szhuima.agent.infrastructure.po.WorkflowNodeConfigLoop
*/
@Mapper
public interface WorkflowNodeConfigLoopMapper extends BaseMapper<WorkflowNodeConfigLoop> {

}





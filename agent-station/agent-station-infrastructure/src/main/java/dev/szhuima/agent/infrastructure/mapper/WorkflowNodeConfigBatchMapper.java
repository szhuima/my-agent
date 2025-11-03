package dev.szhuima.agent.infrastructure.mapper;

import dev.szhuima.agent.infrastructure.po.WorkflowNodeConfigBatch;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jack
* @description 针对表【workflow_node_config_batch】的数据库操作Mapper
* @createDate 2025-09-25 20:35:22
* @Entity dev.szhuima.agent.infrastructure.po.WorkflowNodeConfigBatch
*/
@Mapper
public interface WorkflowNodeConfigBatchMapper extends BaseMapper<WorkflowNodeConfigBatch> {

}





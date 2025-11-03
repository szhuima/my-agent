package dev.szhuima.agent.infrastructure.mapper;

import dev.szhuima.agent.infrastructure.po.WorkflowInstance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jack
* @description 针对表【workflow_instance】的数据库操作Mapper
* @createDate 2025-09-24 21:38:13
* @Entity dev.szhuima.agent.infrastructure.po.WorkflowInstance
*/
@Mapper
public interface WorkflowInstanceMapper extends BaseMapper<WorkflowInstance> {

}





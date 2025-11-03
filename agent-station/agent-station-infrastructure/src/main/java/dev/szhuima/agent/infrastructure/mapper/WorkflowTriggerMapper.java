package dev.szhuima.agent.infrastructure.mapper;

import dev.szhuima.agent.infrastructure.po.WorkflowTrigger;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jack
* @description 针对表【workflow_trigger】的数据库操作Mapper
* @createDate 2025-10-01 11:51:11
* @Entity dev.szhuima.agent.infrastructure.po.WorkflowTrigger
*/
@Mapper
public interface WorkflowTriggerMapper extends BaseMapper<WorkflowTrigger> {

}





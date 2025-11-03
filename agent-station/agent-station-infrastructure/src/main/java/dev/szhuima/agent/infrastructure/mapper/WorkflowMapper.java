package dev.szhuima.agent.infrastructure.mapper;

import dev.szhuima.agent.infrastructure.po.Workflow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jack
* @description 针对表【workflow】的数据库操作Mapper
* @createDate 2025-09-24 16:03:02
* @Entity dev.szhuima.agent.infrastructure.po.Workflow
*/
@Mapper
public interface WorkflowMapper extends BaseMapper<Workflow> {

}





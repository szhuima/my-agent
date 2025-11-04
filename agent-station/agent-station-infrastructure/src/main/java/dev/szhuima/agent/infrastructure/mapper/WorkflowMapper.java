package dev.szhuima.agent.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.szhuima.agent.infrastructure.entity.TbWorkflow;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jack
 * @description 针对表【workflow】的数据库操作Mapper
 * @createDate 2025-09-24 16:03:02
 * @Entity dev.szhuima.agent.infrastructure.po.Workflow
 */
@Mapper
public interface WorkflowMapper extends BaseMapper<TbWorkflow> {

}





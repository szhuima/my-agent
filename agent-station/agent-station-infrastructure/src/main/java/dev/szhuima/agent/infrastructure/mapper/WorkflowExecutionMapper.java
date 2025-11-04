package dev.szhuima.agent.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.szhuima.agent.infrastructure.entity.TbWorkflowExecution;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jack
 * @description 针对表【workflow_execution】的数据库操作Mapper
 * @createDate 2025-09-25 12:16:25
 * @Entity dev.szhuima.agent.infrastructure.po.WorkflowExecution
 */
@Mapper
public interface WorkflowExecutionMapper extends BaseMapper<TbWorkflowExecution> {

}





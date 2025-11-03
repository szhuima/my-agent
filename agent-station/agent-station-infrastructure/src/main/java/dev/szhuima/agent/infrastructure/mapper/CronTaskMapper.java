package dev.szhuima.agent.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.szhuima.agent.infrastructure.po.CronTask;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jack
* @description 针对表【cron_task】的数据库操作Mapper
* @createDate 2025-09-18 17:30:18
* @Entity dev.szhuima.mcp.server.boss.repository.entity.CronTask
*/
@Mapper
public interface CronTaskMapper extends BaseMapper<CronTask> {

}





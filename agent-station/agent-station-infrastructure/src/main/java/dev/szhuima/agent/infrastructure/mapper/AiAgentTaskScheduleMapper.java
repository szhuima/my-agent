package dev.szhuima.agent.infrastructure.mapper;

import dev.szhuima.agent.infrastructure.po.AiAgentTaskSchedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author jack
* @description 针对表【ai_agent_task_schedule(智能体任务调度配置表)】的数据库操作Mapper
* @createDate 2025-09-11 15:42:45
* @Entity dev.szhuima.agent.infrastructure.po.AiAgentTaskSchedule
*/
@Mapper
public interface AiAgentTaskScheduleMapper extends BaseMapper<AiAgentTaskSchedule> {


    /**
     * 查询所有任务调度配置
     * @return 任务调度配置列表
     */
    List<AiAgentTaskSchedule> queryAllTaskSchedule();

    /**
     * 根据ID查询任务调度配置
     * @param id 任务调度配置ID
     * @return 任务调度配置
     */
    AiAgentTaskSchedule queryTaskScheduleById(Long id);

    /**
     * 根据智能体ID查询任务调度配置
     * @param agentId 智能体ID
     * @return 任务调度配置列表
     */
    List<AiAgentTaskSchedule> queryTaskScheduleByAgentId(Long agentId);

    /**
     * 插入任务调度配置
     * @param aiAgentTaskSchedule 任务调度配置
     * @return 影响行数
     */
    int insert(AiAgentTaskSchedule aiAgentTaskSchedule);

    /**
     * 更新任务调度配置
     * @param aiAgentTaskSchedule 任务调度配置
     * @return 影响行数
     */
    int update(AiAgentTaskSchedule aiAgentTaskSchedule);

    /**
     * 查询所有有效的任务调度配置
     * @return 有效的任务调度配置列表
     */
    List<AiAgentTaskSchedule> queryAllValidTaskSchedule();

    List<Long> queryAllInvalidTaskScheduleIds();

    List<AiAgentTaskSchedule> queryTaskScheduleList(AiAgentTaskSchedule aiAgentTaskSchedule);


}





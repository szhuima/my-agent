package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 
 * @TableName cron_task
 */
@TableName(value ="cron_task")
@Data
public class CronTask {
    /**
     * 
     */
    @TableId
    private Long id;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务参数
     */
    private String taskParam;

    /**
     * cron 表达式
     */
    private String cronExpression;

    /**
     * 运行状态, 0: 未运行， 1: 正在运行
     */
    private Integer runStatus;

    /**
     * 任务进度
     */
    private Long progress;

    /**
     * 任务状态,  0: 未完成 1:已完成  2: 已失败
     */
    private String taskStatus;

    /**
     * 
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
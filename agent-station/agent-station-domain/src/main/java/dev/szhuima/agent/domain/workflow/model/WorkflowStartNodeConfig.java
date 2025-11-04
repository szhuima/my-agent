package dev.szhuima.agent.domain.workflow.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * * @Author: szhuima
 * * @Date    2025/11/3 16:36
 * * @Description
 **/
@Data
public class WorkflowStartNodeConfig {

    // 启动类型：定时调度
    @JSONField(name = "start_type")
    private WorkflowStartType startType;

    // 定时表达式
    @JSONField(name = "cron_expression")
    private String cronExpression;


}

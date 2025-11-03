package dev.szhuima.agent.domain.workflow.model;

/**
 * * @Author: szhuima
 * * @Date    2025/10/1 09:28
 * * @Description
 **/
public enum TriggerType {

    /**
     * 定时触发
     */
    CRON,
    /**
     * HTTP触发
     */
    HTTP,
    /**
     * Rabbitmq消息队列触发
     */
    RABBITMQ


}

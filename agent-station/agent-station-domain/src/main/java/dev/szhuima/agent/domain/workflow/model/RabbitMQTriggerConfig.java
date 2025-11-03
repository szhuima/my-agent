package dev.szhuima.agent.domain.workflow.model;

import lombok.Data;

/**
 * * @Author: szhuima
 * * @Date    2025/10/6 20:25
 * * @Description
 **/
@Data
public class RabbitMQTriggerConfig {

     /**
     * 主机名
     */
    private String host;

    /**
     * 端口号
     */
    private int port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

     /**
     * 队列名
     */
    private String queue;

     /**
     * 并发数 格式：min-max
     */
    private String concurrency;

}

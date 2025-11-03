package dev.szhuima.agent.domain.agent.model.valobj.enums;

/**
 * * @Author: szhuima
 * * @Date    2025/9/14 10:13
 * * @Description
 **/

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgentClientType {

    DEFAULT("默认"),
    ANALYSIS("分析"),
    EXECUTION("执行"),
    SUPERVISOR("监督");

    private final String info;

}

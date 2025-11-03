package dev.szhuima.agent.domain.agent.model.valobj;

import dev.szhuima.agent.domain.agent.model.valobj.enums.AgentClientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 智能体-客户端关联对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentClientConfigVO {

    /**
     * 智能体ID
     */
    private Long agentId;

    /**
     * 客户端ID
     */
    private Long clientId;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 客户端类型
     */
    private AgentClientType clientType;


    /**
     * 序列号(执行顺序)
     */
    private Integer sequence;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 返回格式
     */
    private String returnFormat;

}

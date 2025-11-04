package dev.szhuima.agent.domain.agent;

import dev.szhuima.agent.domain.agent.model.Knowledge;
import dev.szhuima.agent.domain.agent.model.ModelApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 智能体
 * 聚合根
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Agent {

    private Long id;

    private String agentName;

    private String tenantId;

    private String systemPrompt;

    private ModelApi modelApi;

    private Integer memorySize = 0;

    private List<Knowledge> knowledgeList;

}

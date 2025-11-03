package dev.szhuima.agent.domain.agent;

import dev.szhuima.agent.domain.agent.model.valobj.Knowledge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 智能体客户端
 * 聚合根
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentClient {

    private Long clientId;

    private String systemPrompt;

    private String modelId;

    private List<String> mcpIdList;

    private List<String> advisorIdList;

    private Integer memorySize = 0;

    private List<Knowledge> knowledgeList;

}

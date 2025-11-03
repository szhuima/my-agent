package dev.szhuima.agent.domain.agent.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 提示词&动态规划
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-04 21:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiClientSystemPromptVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 提示词内容
     */
    private String promptContent;

}

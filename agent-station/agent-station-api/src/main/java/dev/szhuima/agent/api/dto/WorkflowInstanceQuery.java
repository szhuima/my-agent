package dev.szhuima.agent.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 工作流查询请求 DTO
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/10/02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowInstanceQuery extends PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 工作流ID（唯一标识）
     */
    private Long workflowId;

    private String workflowName;


    /**
     * 状态
     */
    private String status;

}
package dev.szhuima.agent.domain.workflow.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowNodeConfigFormDO {
    /**
     * 
     */
    private Long configFormId;

    /**
     * 表单字段定义 JSON，包含字段名、类型、必填、默认值
     */
    private String formSchema;

    /**
     * 
     */
    private LocalDateTime createdAt;

    /**
     * 
     */
    private LocalDateTime updatedAt;
}
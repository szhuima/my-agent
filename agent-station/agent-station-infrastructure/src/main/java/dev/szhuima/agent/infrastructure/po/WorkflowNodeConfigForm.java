package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName workflow_node_config_form
 */
@TableName(value ="workflow_node_config_form")
@Data
public class WorkflowNodeConfigForm {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
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
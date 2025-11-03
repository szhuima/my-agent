package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName workflow_dsl
 */
@TableName(value ="workflow_dsl")
@Data
public class WorkflowDsl {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long workflowId;

    /**
     * 
     */
    private Integer version;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private LocalDateTime createTime;
}
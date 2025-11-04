package dev.szhuima.agent.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @TableName workflow_dsl
 */
@TableName(value = "tb_workflow_dsl")
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
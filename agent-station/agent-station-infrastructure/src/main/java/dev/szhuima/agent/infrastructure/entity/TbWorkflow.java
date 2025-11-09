package dev.szhuima.agent.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @TableName workflow
 */
@TableName(value = "tb_workflow")
@Data
public class TbWorkflow {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long workflowId;

    /**
     *
     */
    private String name;

    private Integer version;

    /**
     * 元数据
     */
    private String metaJson;

    private String ymlConfig;

    /**
     *
     */
    private String description;

    /**
     *
     */
    private String status;

    /**
     *
     */
    private LocalDateTime createdAt;

    /**
     *
     */
    private LocalDateTime updatedAt;
}
package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName workflow_node_config_batch
 */
@TableName(value ="workflow_node_config_batch")
@Data
public class WorkflowNodeConfigBatch {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String listKeyPath;

    /**
     * 
     */
    private String itemAlias;

    /**
     * 
     */
    private String outputKey;

    /**
     * 
     */
    private Integer parallelism;

    /**
     * 
     */
    private String errorStrategy;

    /**
     * 
     */
    private LocalDateTime createTime;

    /**
     * 
     */
    private LocalDateTime updateTime;
}
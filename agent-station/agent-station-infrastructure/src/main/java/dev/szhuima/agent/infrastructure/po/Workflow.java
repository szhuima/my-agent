package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName workflow
 */
@TableName(value ="workflow")
@Data
public class Workflow {
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
package dev.szhuima.agent.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 工作流响应DTO
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/1/20 10:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工作流ID（唯一标识）
     */
    private Long workflowId;

    /**
     * 工作流名称
     */
    private String name;

    /**
     * 配置描述
     */
    private String description;


    /**
     * 配置版本号
     */
    private Integer version;

    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status;

    /**
     * 部署实例数
     */
    private Long deployCount;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
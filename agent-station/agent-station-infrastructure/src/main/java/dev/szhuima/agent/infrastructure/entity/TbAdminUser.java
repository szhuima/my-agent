package dev.szhuima.agent.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员用户表
 *
 * @TableName admin_user
 */
@TableName(value = "tb_admin_user")
@Data
public class TbAdminUser {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（唯一标识）
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 用户名（登录账号）
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码（加密存储）
     */
    @TableField(value = "password")
    private String password;

    /**
     * 状态(0:禁用,1:启用,2:锁定)
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

}
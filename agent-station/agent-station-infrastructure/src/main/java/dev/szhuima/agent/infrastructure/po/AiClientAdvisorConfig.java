package dev.szhuima.agent.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 客户端-顾问关联表
 * @TableName ai_client_advisor_config
 */
@TableName(value ="ai_client_advisor_config")
@Data
public class AiClientAdvisorConfig {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户端ID
     */
    private Long clientId;

    /**
     * 顾问ID
     */
    private Long advisorId;

    /**
     * 创建时间
     */
    private Date createTime;
}
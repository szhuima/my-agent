package dev.szhuima.agent.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


@TableName(value = "tb_model_api")
@Data
public class TbModelApi {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模型API名称
     */
    private String modelApiName;

    /**
     * 基础URL
     */
    private String baseUrl;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * 完成路径
     */
    private String completionsPath;

    /**
     * 嵌入路径
     */
    private String embeddingsPath;

    /**
     * 模型类型(chat/embedding等)
     */
    private String modelType;

    /**
     * 模型来源(openai/ollama等)
     */
    private String modelSource;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 超时时间(秒)
     */
    private Integer timeout;

    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
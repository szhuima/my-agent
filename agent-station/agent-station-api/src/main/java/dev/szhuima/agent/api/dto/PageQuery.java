package dev.szhuima.agent.api.dto;

import lombok.Data;

/**
 * * @Author: szhuima
 * * @Date    2025/10/15 22:59
 * * @Description
 **/
@Data
public class PageQuery {

    /**
     * 页码（从1开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

}

package dev.szhuima.agent.domain.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/9/25 21:47
 * * @Description
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowNodeConfigBatchDO {

    private Long id;

    /**
     * 遍历哪个JSONPath下的元素
     */
    private String items;

    /**
     * 遍历的每个元素，存放在上下文中的什么key上，会放在全局域下的某个key下
     */
    private String itemKey;

     /**
      * 遍历每个元素后，执行的节点名称列表
      */
    private List<String> bodyNodes;

    /**
     *
     */
    private ErrorStrategy errorStrategy = ErrorStrategy.CONTINUE;

     /**
      * 错误处理策略
      */
    public enum ErrorStrategy {
        CONTINUE,
        BREAK
    }

}

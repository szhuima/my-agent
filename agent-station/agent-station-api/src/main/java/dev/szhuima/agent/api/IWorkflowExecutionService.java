package dev.szhuima.agent.api;

import dev.szhuima.agent.api.dto.PageDTO;
import dev.szhuima.agent.api.dto.WorkflowExecutionDTO;
import dev.szhuima.agent.api.dto.WorkflowExecutionQuery;

/**
 * * @Author: szhuima
 * * @Date    2025/10/15 22:41
 * * @Description
 **/
public interface IWorkflowExecutionService {

    /**
     * 分页查询执行
     *
     * @param query
     * @return
     */
    Response<PageDTO<WorkflowExecutionDTO>> queryList(WorkflowExecutionQuery query);


    /**
     * 删除执行
     *
     * @param executionId
     * @return
     */
    Response<Boolean> deleteExecution(Long executionId);

}

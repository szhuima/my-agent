package dev.szhuima.agent.trigger.http.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.szhuima.agent.api.IWorkflowExecutionService;
import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.api.dto.PageDTO;
import dev.szhuima.agent.api.dto.WorkflowExecutionDTO;
import dev.szhuima.agent.api.dto.WorkflowExecutionQuery;
import dev.szhuima.agent.infrastructure.mapper.WorkflowExecutionMapper;
import dev.szhuima.agent.infrastructure.po.WorkflowExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * * @Author: szhuima
 * * @Date    2025/10/15 22:52
 * * @Description
 **/

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/workflow-execution")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class WorkflowExecutionAdminController extends BaseController implements IWorkflowExecutionService {


    @Resource
    private WorkflowExecutionMapper executionMapper;

    /**
     * 分页查询执行
     *
     * @param query
     * @return
     */
    @Override
    @PostMapping("/query-list")
    public Response<PageDTO<WorkflowExecutionDTO>> queryList(@RequestBody WorkflowExecutionQuery query) {
        Page<WorkflowExecution> pageQuery = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<WorkflowExecution> wrapper = Wrappers.lambdaQuery(WorkflowExecution.class)
                .eq(StrUtil.isNotEmpty(query.getWorkflowName()), WorkflowExecution::getWorkflowName, query.getWorkflowName())
                .eq(query.getInstanceId() != null, WorkflowExecution::getWorkflowInstanceId, query.getInstanceId())
                .orderByDesc(WorkflowExecution::getStartTime);

        IPage<WorkflowExecution> pageResult = executionMapper.selectPage(pageQuery, wrapper);
        PageDTO<WorkflowExecutionDTO> pageDTO = copyPage(pageResult, WorkflowExecutionDTO.class);
        return Response.success(pageDTO);
    }

    /**
     * 删除执行
     *
     * @param executionId
     * @return
     */
    @Override
    @DeleteMapping("/delete/{executionId}")
    public Response<Boolean> deleteExecution(@PathVariable("executionId") Long executionId) {
        int count = executionMapper.deleteById(executionId);
        return Response.success(count > 0);
    }
}

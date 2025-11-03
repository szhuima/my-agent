package dev.szhuima.agent.trigger.http.admin;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.szhuima.agent.api.IWorkflowInstanceService;
import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.api.dto.PageDTO;
import dev.szhuima.agent.api.dto.WorkflowInstanceDTO;
import dev.szhuima.agent.api.dto.WorkflowInstanceQuery;
import dev.szhuima.agent.domain.workflow.model.WorkflowInstanceStatus;
import dev.szhuima.agent.infrastructure.mapper.WorkflowExecutionMapper;
import dev.szhuima.agent.infrastructure.mapper.WorkflowInstanceMapper;
import dev.szhuima.agent.infrastructure.mapper.WorkflowMapper;
import dev.szhuima.agent.infrastructure.po.WorkflowExecution;
import dev.szhuima.agent.infrastructure.po.WorkflowInstance;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/workflow-instance")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class WorkflowInstanceAdminController extends BaseController implements IWorkflowInstanceService {

    @Resource
    private WorkflowMapper workflowMapper;

    @Resource
    private WorkflowInstanceMapper instanceMapper;

    @Resource
    private WorkflowExecutionMapper executionMapper;


    /**
     * 查询工作流实例
     *
     * @param query
     * @return
     */
    @Override
    @PostMapping("/query-list")
    public Response<PageDTO<WorkflowInstanceDTO>> queryInstance(@RequestBody WorkflowInstanceQuery query) {
        LambdaQueryWrapper<WorkflowInstance> wrapper = Wrappers.lambdaQuery(WorkflowInstance.class)
                .eq(query.getWorkflowId() != null, WorkflowInstance::getWorkflowId, query.getWorkflowId())
                .eq(StringUtils.isNotEmpty(query.getWorkflowName()), WorkflowInstance::getWorkflowName, query.getWorkflowName());
        IPage<WorkflowInstance> page = new Page<>(query.getPageNum(), query.getPageSize());

        IPage<WorkflowInstance> pageResult = instanceMapper.selectPage(page, wrapper);

        List<WorkflowInstance> records = pageResult.getRecords();

        List<WorkflowInstanceDTO> workflowInstanceDTOS = BeanUtil.copyToList(records, WorkflowInstanceDTO.class);

        if (CollectionUtil.isNotEmpty(workflowInstanceDTOS)) {
            for (WorkflowInstanceDTO workflowInstanceDTO : workflowInstanceDTOS) {
                LambdaQueryWrapper<WorkflowExecution> eq = Wrappers.lambdaQuery(WorkflowExecution.class)
                        .eq(WorkflowExecution::getWorkflowInstanceId, workflowInstanceDTO.getInstanceId());
                Long executionCount = executionMapper.selectCount(eq);
                workflowInstanceDTO.setExecutionCount(executionCount);
            }
        }

        PageDTO<WorkflowInstanceDTO> pageDTO = new PageDTO<>();
        pageDTO.setSize(pageResult.getSize());
        pageDTO.setTotal(pageResult.getTotal());
        pageDTO.setCurrent(pageResult.getCurrent());
        pageDTO.setRecords(workflowInstanceDTOS);

        return Response.success(pageDTO);
    }

    /**
     * 卸载工作流实例
     *
     * @param instanceId
     * @return
     */
    @Override
    @PostMapping("/un-deploy/{instanceId}")
    public Response<Boolean> unDeploy(@PathVariable("instanceId") Long instanceId) {
        LambdaUpdateWrapper<WorkflowInstance> wrapper = Wrappers.lambdaUpdate(WorkflowInstance.class)
                .eq(WorkflowInstance::getInstanceId, instanceId)
                .set(WorkflowInstance::getStatus, WorkflowInstanceStatus.UN_DEPLOYED.name());
        int update = instanceMapper.update(wrapper);
        return Response.success(update > 0);
    }

    /**
     * 部署工作流实例
     *
     * @param instanceId
     * @return
     */
    @Override
    @PostMapping("/deploy/{instanceId}")
    public Response<Boolean> deploy(@PathVariable("instanceId") Long instanceId) {
        LambdaUpdateWrapper<WorkflowInstance> wrapper = Wrappers.lambdaUpdate(WorkflowInstance.class)
                .eq(WorkflowInstance::getInstanceId, instanceId)
                .set(WorkflowInstance::getStatus, WorkflowInstanceStatus.DEPLOYED.name());
        int update = instanceMapper.update(wrapper);
        return Response.success(update > 0);
    }

    /**
     * 删除工作流实例
     *
     * @param instanceId
     * @return
     */
    @Override
    @DeleteMapping("/delete/{instanceId}")
    @Transactional(rollbackFor = Exception.class)
    public Response<Boolean> deleteInstance(@PathVariable("instanceId") Long instanceId) {
        if (instanceId == null) {
            throw new IllegalArgumentException("instanceId不能为空");
        }

        // 删除工作流实例执行记录
        LambdaQueryWrapper<WorkflowExecution> instanceIdEq = Wrappers.lambdaQuery(WorkflowExecution.class)
                .eq(WorkflowExecution::getWorkflowInstanceId, instanceId);
        int delete = executionMapper.delete(instanceIdEq);

        LambdaQueryWrapper<WorkflowInstance> instanceWrapper = Wrappers.lambdaQuery(WorkflowInstance.class)
                .eq(WorkflowInstance::getInstanceId, instanceId);
        instanceMapper.delete(instanceWrapper);
        return Response.success(true);
    }
}

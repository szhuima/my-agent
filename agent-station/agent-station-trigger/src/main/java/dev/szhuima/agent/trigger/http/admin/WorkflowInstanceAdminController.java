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
import dev.szhuima.agent.infrastructure.entity.TbWorkflowExecution;
import dev.szhuima.agent.infrastructure.entity.TbWorkflowInstance;
import dev.szhuima.agent.infrastructure.mapper.WorkflowExecutionMapper;
import dev.szhuima.agent.infrastructure.mapper.WorkflowInstanceMapper;
import dev.szhuima.agent.infrastructure.mapper.WorkflowMapper;
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
        LambdaQueryWrapper<TbWorkflowInstance> wrapper = Wrappers.lambdaQuery(TbWorkflowInstance.class)
                .eq(query.getWorkflowId() != null, TbWorkflowInstance::getWorkflowId, query.getWorkflowId())
                .eq(StringUtils.isNotEmpty(query.getWorkflowName()), TbWorkflowInstance::getWorkflowName, query.getWorkflowName());
        IPage<TbWorkflowInstance> page = new Page<>(query.getPageNum(), query.getPageSize());

        IPage<TbWorkflowInstance> pageResult = instanceMapper.selectPage(page, wrapper);

        List<TbWorkflowInstance> records = pageResult.getRecords();

        List<WorkflowInstanceDTO> workflowInstanceDTOS = BeanUtil.copyToList(records, WorkflowInstanceDTO.class);

        if (CollectionUtil.isNotEmpty(workflowInstanceDTOS)) {
            for (WorkflowInstanceDTO workflowInstanceDTO : workflowInstanceDTOS) {
                LambdaQueryWrapper<TbWorkflowExecution> eq = Wrappers.lambdaQuery(TbWorkflowExecution.class)
                        .eq(TbWorkflowExecution::getWorkflowInstanceId, workflowInstanceDTO.getInstanceId());
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
        LambdaUpdateWrapper<TbWorkflowInstance> wrapper = Wrappers.lambdaUpdate(TbWorkflowInstance.class)
                .eq(TbWorkflowInstance::getInstanceId, instanceId)
                .set(TbWorkflowInstance::getStatus, WorkflowInstanceStatus.UN_DEPLOYED.name());
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
        LambdaUpdateWrapper<TbWorkflowInstance> wrapper = Wrappers.lambdaUpdate(TbWorkflowInstance.class)
                .eq(TbWorkflowInstance::getInstanceId, instanceId)
                .set(TbWorkflowInstance::getStatus, WorkflowInstanceStatus.DEPLOYED.name());
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
        LambdaQueryWrapper<TbWorkflowExecution> instanceIdEq = Wrappers.lambdaQuery(TbWorkflowExecution.class)
                .eq(TbWorkflowExecution::getWorkflowInstanceId, instanceId);
        int delete = executionMapper.delete(instanceIdEq);

        LambdaQueryWrapper<TbWorkflowInstance> instanceWrapper = Wrappers.lambdaQuery(TbWorkflowInstance.class)
                .eq(TbWorkflowInstance::getInstanceId, instanceId);
        instanceMapper.delete(instanceWrapper);
        return Response.success(true);
    }
}

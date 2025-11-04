package dev.szhuima.agent.trigger.http.admin;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.szhuima.agent.api.IWorkflowAdminService;
import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.api.dto.PageDTO;
import dev.szhuima.agent.api.dto.WorkflowQueryRequestDTO;
import dev.szhuima.agent.api.dto.WorkflowResponseDTO;
import dev.szhuima.agent.domain.support.exception.BizException;
import dev.szhuima.agent.domain.workflow.model.WorkflowInstanceStatus;
import dev.szhuima.agent.domain.workflow.service.WorkflowService;
import dev.szhuima.agent.infrastructure.entity.Workflow;
import dev.szhuima.agent.infrastructure.entity.WorkflowDsl;
import dev.szhuima.agent.infrastructure.entity.WorkflowInstance;
import dev.szhuima.agent.infrastructure.entity.WorkflowNode;
import dev.szhuima.agent.infrastructure.mapper.WorkflowDslMapper;
import dev.szhuima.agent.infrastructure.mapper.WorkflowInstanceMapper;
import dev.szhuima.agent.infrastructure.mapper.WorkflowMapper;
import dev.szhuima.agent.infrastructure.mapper.WorkflowNodeMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/workflow")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class WorkflowAdminController extends BaseController implements IWorkflowAdminService {

    @Resource
    private WorkflowMapper workflowMapper;

    @Resource
    private WorkflowInstanceMapper instanceMapper;

    @Resource
    private WorkflowDslMapper workflowDslMapper;

    @Resource
    private WorkflowNodeMapper workflowNodeMapper;
    @Autowired
    private WorkflowService workflowService;

    /**
     * 查询工作流
     *
     * @param request
     * @return
     */
    @Override
    @PostMapping("/query-list")
    public Response<PageDTO<WorkflowResponseDTO>> queryWorkflow(@RequestBody WorkflowQueryRequestDTO request) {
        IPage<Workflow> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Workflow> wrapper = Wrappers.lambdaQuery(Workflow.class)
                .eq(request.getWorkflowId() != null, Workflow::getWorkflowId, request.getWorkflowId())
                .eq(request.getWorkflowName() != null, Workflow::getName, request.getWorkflowName())
                .eq(request.getStatus() != null, Workflow::getStatus, request.getStatus())
                .orderByDesc(Workflow::getUpdatedAt);
        IPage<Workflow> workflowIPage = workflowMapper.selectPage(page, wrapper);
        PageDTO<Workflow> workflowPage = convertPage(workflowIPage);

        List<Workflow> records = workflowPage.getRecords();
        List<WorkflowResponseDTO> workflowResponseDTOS = BeanUtil.copyToList(records, WorkflowResponseDTO.class);

        if (CollectionUtil.isNotEmpty(workflowResponseDTOS)) {

            workflowResponseDTOS.stream()
                    .forEach((workflowResponseDTO -> {
                        LambdaQueryWrapper<WorkflowInstance> eq = Wrappers.lambdaQuery(WorkflowInstance.class)
                                .eq(WorkflowInstance::getWorkflowId, workflowResponseDTO.getWorkflowId())
                                .eq(WorkflowInstance::getStatus, WorkflowInstanceStatus.DEPLOYED.name());
                        Long count = instanceMapper.selectCount(eq);
                        workflowResponseDTO.setDeployCount(count);
                    }));
        }

        PageDTO<WorkflowResponseDTO> pageDTO = new PageDTO<>();
        pageDTO.setCurrent(workflowPage.getCurrent());
        pageDTO.setSize(workflowPage.getSize());
        pageDTO.setTotal(workflowPage.getTotal());
        pageDTO.setRecords(workflowResponseDTOS);

        return Response.success(pageDTO);
    }

    /**
     * 查询工作流定义
     *
     * @param workflowId
     * @return
     */
    @Override
    @GetMapping("/get-dsl/{workflowId}")
    public Response<String> queryDSL(@PathVariable("workflowId") Long workflowId) {
        LambdaQueryWrapper<WorkflowDsl> wrapper = Wrappers.lambdaQuery(WorkflowDsl.class)
                .select(WorkflowDsl::getContent)
                .eq(WorkflowDsl::getWorkflowId, workflowId);
        WorkflowDsl workflowDsl = workflowDslMapper.selectOne(wrapper);
        String content = workflowDsl != null ? workflowDsl.getContent() : null;
        return Response.success(content);
    }

    /**
     * 从DSL文件中导入工作流
     *
     * @param dslTContent dsl 配置内容
     * @return
     */
    @Override
    @PostMapping("/import")
    public Response<String> importWorkflowFromDsl(@RequestBody String dslTContent) {
        Long instanceId = workflowService.importWorkflow(dslTContent);
        return Response.success(instanceId);
    }


    /**
     * 删除指定ID的工作流
     *
     * @param workflowId 工作流ID
     * @return
     */
    @Override
    @DeleteMapping("/delete/{workflowId}")
    @Transactional(rollbackFor = Exception.class)
    public Response deleteWorkflow(@PathVariable("workflowId") Long workflowId) {
        if (workflowId == null) {
            throw new BizException("工作流ID不能为空");
        }

        // 检查该工作流下是否有工作流实例
        LambdaQueryWrapper<WorkflowInstance> intWrapper = Wrappers.lambdaQuery(WorkflowInstance.class)
                .eq(WorkflowInstance::getWorkflowId, workflowId);
        Long count = instanceMapper.selectCount(intWrapper);
        if (count > 0) {
            return Response.illegalParameter("该工作流下有正在运行的实例, 需要先删除该工作流下全部实例");
        }

        LambdaQueryWrapper<WorkflowNode> wrapper = Wrappers.lambdaQuery(WorkflowNode.class)
                .eq(WorkflowNode::getWorkflowId, workflowId);
        workflowNodeMapper.delete(wrapper);

        LambdaQueryWrapper<WorkflowDsl> wrapper1 = Wrappers.lambdaQuery(WorkflowDsl.class)
                .eq(WorkflowDsl::getWorkflowId, workflowId);
        workflowDslMapper.delete(wrapper1);

        workflowMapper.deleteById(workflowId);

        return Response.success(true);
    }


    /**
     * 部署工作流, 创建工作流实例
     *
     * @param workflowId 工作流模板ID
     * @return 工作流实例ID
     */
    @PostMapping("/deploy/{workflowId}")
    public Response<Long> deployWorkflow(@PathVariable("workflowId") Long workflowId) {
        Long instanceId = workflowService.deployWorkflow(workflowId);
        return Response.success(instanceId);
    }
}

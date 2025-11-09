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
import dev.szhuima.agent.domain.workflow.service.WorkflowService;
import dev.szhuima.agent.infrastructure.entity.TbWorkflow;
import dev.szhuima.agent.infrastructure.entity.TbWorkflowNode;
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
        IPage<TbWorkflow> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<TbWorkflow> wrapper = Wrappers.lambdaQuery(TbWorkflow.class)
                .eq(request.getWorkflowId() != null, TbWorkflow::getWorkflowId, request.getWorkflowId())
                .eq(request.getWorkflowName() != null, TbWorkflow::getName, request.getWorkflowName())
                .eq(request.getStatus() != null, TbWorkflow::getStatus, request.getStatus())
                .orderByDesc(TbWorkflow::getUpdatedAt);
        IPage<TbWorkflow> workflowIPage = workflowMapper.selectPage(page, wrapper);
        PageDTO<TbWorkflow> workflowPage = convertPage(workflowIPage);

        List<TbWorkflow> records = workflowPage.getRecords();
        List<WorkflowResponseDTO> workflowResponseDTOS = BeanUtil.copyToList(records, WorkflowResponseDTO.class);

        if (CollectionUtil.isNotEmpty(workflowResponseDTOS)) {

            workflowResponseDTOS.stream()
                    .forEach((workflowResponseDTO -> {
                        workflowResponseDTO.setDeployCount(0L);
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
        LambdaQueryWrapper<TbWorkflow> wrapper = Wrappers.lambdaQuery(TbWorkflow.class)
                .select(TbWorkflow::getYmlConfig)
                .eq(TbWorkflow::getWorkflowId, workflowId);
        TbWorkflow workflow = workflowMapper.selectOne(wrapper);
        String content = workflow != null ? workflow.getYmlConfig() : null;
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
        LambdaQueryWrapper<TbWorkflowNode> wrapper = Wrappers.lambdaQuery(TbWorkflowNode.class)
                .eq(TbWorkflowNode::getWorkflowId, workflowId);
        workflowNodeMapper.delete(wrapper);
        workflowMapper.deleteById(workflowId);
        return Response.success(true);
    }


    /**
     * 激活工作流
     *
     * @param workflowId 工作流模板ID
     * @return 工作流ID
     */
    @PostMapping("/active/{workflowId}")
    public Response<Long> activeWorkflow(@PathVariable("workflowId") Long workflowId) {
        workflowService.activeWorkflow(workflowId);
        return Response.success(workflowId);
    }

    /**
     * 归档工作流
     *
     * @param workflowId 工作流模板ID
     * @return 工作流ID
     */
    @PostMapping("/archive/{workflowId}")
    public Response<Long> archiveWorkflow(@PathVariable("workflowId") Long workflowId) {
        workflowService.archiveWorkflow(workflowId);
        return Response.success(workflowId);
    }

    /**
     * 激活工作流
     *
     * @param workflowId 工作流模板ID
     * @return 工作流实例ID
     */
    @PostMapping("/deploy/{workflowId}")
    public Response<Long> deployWorkflow(@PathVariable("workflowId") Long workflowId) {
        Long instanceId = workflowService.activeWorkflow(workflowId);
        return Response.success(instanceId);
    }
}

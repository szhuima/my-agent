package dev.szhuima.agent.trigger.http.admin;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import dev.szhuima.agent.api.ErrorCode;
import dev.szhuima.agent.api.McpAdminService;
import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.api.dto.AiClientToolMcpRequestDTO;
import dev.szhuima.agent.api.dto.McpQueryRequestDTO;
import dev.szhuima.agent.api.dto.McpResponseDTO;
import dev.szhuima.agent.domain.agent.model.Mcp;
import dev.szhuima.agent.domain.agent.model.McpTransportType;
import dev.szhuima.agent.infrastructure.entity.TbMcp;
import dev.szhuima.agent.infrastructure.factory.McpClientFactory;
import dev.szhuima.agent.infrastructure.mapper.McpMapper;
import dev.szhuima.agent.infrastructure.repository.McpRepository;
import dev.szhuima.agent.infrastructure.util.McpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MCP客户端配置管理控制器
 *
 * @author szhuima
 * @description MCP客户端配置管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/ai-client-tool-mcp")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class McpAdminController implements McpAdminService {

    @Resource
    private McpMapper mcpMapper;

    @Resource
    private McpRepository repository;

    @Resource
    private McpClientFactory mcpClientFactory;

    @Override
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public Response<Boolean> createAiClientToolMcp(@RequestBody AiClientToolMcpRequestDTO request) {
        log.info("创建MCP客户端配置请求：{}", request);

        JSONObject config = JSON.parseObject(request.getConfig());

        // DTO转PO
        TbMcp TbMcp = convertToAiClientToolMcp(request);
        TbMcp.setCreateTime(LocalDateTime.now());
        TbMcp.setUpdateTime(LocalDateTime.now());

        int result = mcpMapper.insert(TbMcp);

        Mcp mcp = repository.getMcp(TbMcp.getId());

        // 初始化MCP客户端
        mcpClientFactory.initMcpClient(mcp);

        return Response.<Boolean>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .info(ErrorCode.SUCCESS.getInfo())
                .data(result > 0)
                .build();
    }

    @Override
    @PutMapping("/update-by-id")
    @Transactional(rollbackFor = Exception.class)
    public Response<Boolean> updateAiClientToolMcpById(@RequestBody AiClientToolMcpRequestDTO request) {
        log.info("根据ID更新MCP客户端配置请求：{}", request);

        if (request.getId() == null) {
            return Response.<Boolean>builder()
                    .code(ErrorCode.BIZ_ERROR.getCode())
                    .info("ID不能为空")
                    .data(false)
                    .build();
        }

        // DTO转PO
        TbMcp TbMcp = convertToAiClientToolMcp(request);
        TbMcp.setUpdateTime(LocalDateTime.now());

        int result = mcpMapper.updateById(TbMcp);

        Mcp mcp = repository.getMcp(TbMcp.getId());

        // 更新MCP客户端
        mcpClientFactory.refresh(mcp);

        return Response.<Boolean>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .info(ErrorCode.SUCCESS.getInfo())
                .data(result > 0)
                .build();
    }


    @Override
    @DeleteMapping("/delete-by-id/{id}")
    public Response<Boolean> deleteAiClientToolMcpById(@PathVariable Long id) {
        try {
            log.info("根据ID删除MCP客户端配置：{}", id);

            int result = mcpMapper.deleteById(id);

            return Response.<Boolean>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(result > 0)
                    .build();
        } catch (Exception e) {
            log.error("根据ID删除MCP客户端配置失败", e);
            return Response.<Boolean>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }


    @Override
    @GetMapping("/query-by-id/{id}")
    public Response<McpResponseDTO> queryAiClientToolMcpById(@PathVariable Long id) {
        try {
            log.info("根据ID查询MCP客户端配置：{}", id);

            TbMcp TbMcp = mcpMapper.selectById(id);

            if (TbMcp == null) {
                return Response.<McpResponseDTO>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .info(ErrorCode.SUCCESS.getInfo())
                        .data(null)
                        .build();
            }

            McpResponseDTO responseDTO = convertToDTO(TbMcp);

            return Response.<McpResponseDTO>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
        } catch (Exception e) {
            log.error("根据ID查询MCP客户端配置失败", e);
            return Response.<McpResponseDTO>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }


    @Override
    @GetMapping("/query-enabled")
    public Response<List<McpResponseDTO>> queryEnabledAiClientToolMcps() {
        log.info("查询启用的MCP客户端配置");

        LambdaQueryWrapper<TbMcp> wrapper = Wrappers.lambdaQuery(TbMcp.class).eq(TbMcp::getStatus, 1);
        List<TbMcp> aiClientToolMcps = mcpMapper.selectList(wrapper);

        List<McpResponseDTO> responseDTOs = aiClientToolMcps.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return Response.<List<McpResponseDTO>>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .info(ErrorCode.SUCCESS.getInfo())
                .data(responseDTOs)
                .build();
    }

    @Override
    @PostMapping("/query-list")
    public Response<List<McpResponseDTO>> queryAiClientToolMcpList(@RequestBody McpQueryRequestDTO request) {
        log.info("根据查询条件查询MCP客户端配置列表：{}", request);

        // 构建查询条件
        LambdaQueryWrapper<TbMcp> wrapper = Wrappers.lambdaQuery(TbMcp.class);

        // 根据状态查询
        if (request.getStatus() != null) {
            wrapper.eq(TbMcp::getStatus, request.getStatus());
        }

        // 执行查询
        List<TbMcp> aiClientToolMcps = mcpMapper.selectList(wrapper);

        List<McpResponseDTO> responseDTOs = aiClientToolMcps.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return Response.success(responseDTOs);
    }


    private TbMcp convertToAiClientToolMcp(AiClientToolMcpRequestDTO requestDTO) {
        TbMcp TbMcp = new TbMcp();
        BeanUtils.copyProperties(requestDTO, TbMcp);
        return TbMcp;
    }

    /**
     * PO转响应DTO对象
     *
     * @param tbmcp PO对象
     * @return 响应DTO
     */
    private McpResponseDTO convertToDTO(TbMcp tbmcp) {
        McpResponseDTO responseDTO = new McpResponseDTO();
        BeanUtils.copyProperties(tbmcp, responseDTO);

        JSONObject mcpConfig = JSON.parseObject(tbmcp.getConfig());
        JSONObject mcpServers = mcpConfig.getJSONObject("mcpServers");

        String mcpName = McpUtil.inferMcpName(mcpServers);
        McpTransportType transportType = McpUtil.inferTransportType(mcpServers);

        responseDTO.setMcpName(mcpName);
        responseDTO.setTransportType(transportType.getValue());
        responseDTO.setTransportConfig(tbmcp.getConfig());

        return responseDTO;
    }

}
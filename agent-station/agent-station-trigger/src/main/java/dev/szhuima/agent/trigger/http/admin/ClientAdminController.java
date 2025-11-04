package dev.szhuima.agent.trigger.http.admin;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import dev.szhuima.agent.api.ErrorCode;
import dev.szhuima.agent.api.IAiClientAdminService;
import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.api.dto.AiClientQueryRequestDTO;
import dev.szhuima.agent.api.dto.AiClientRequestDTO;
import dev.szhuima.agent.api.dto.AiClientResponseDTO;
import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
import dev.szhuima.agent.domain.support.chain.DefaultChainContext;
import dev.szhuima.agent.domain.support.chain.HandlerChain;
import dev.szhuima.agent.infrastructure.mapper.*;
import dev.szhuima.agent.infrastructure.po.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI客户端管理控制器
 *
 * @author szhuima
 * @description AI客户端配置管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/ai-client")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ClientAdminController implements IAiClientAdminService {

    @Resource
    private AiClientMapper aiClientMapper;

    @Resource
    private AiClientModelMapper modelMapper;

    @Resource
    private AiClientAdvisorMapper advisorMapper;

    @Resource
    private AiClientAdvisorConfigMapper clientAdvisorConfigMapper;

    @Resource
    private AiClientToolConfigMapper clientToolConfigMapper;

    @Resource
    private AiClientToolMcpMapper toolMapper;

    @Resource
    private AiKnowledgeMapper knowledgeMapper;

    @Resource
    private AiClientKnowledgeConfigMapper clientKnowledgeConfigMapper;



    @Resource
    @Qualifier("agentAssemblyChain")
    private HandlerChain<AgentAssemblyInput, Void> agentAssemblyChain;


    @Override
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public Response<Boolean> createAiClient(@RequestBody AiClientRequestDTO request) {
        log.info("创建AI客户端配置请求：{}", request);

        Long modelId = request.getModelId();
        if (modelId == null) {
            throw new IllegalArgumentException("模型未选择");
        }
        AiClientModel aiClientModel = modelMapper.selectById(modelId);
        if (aiClientModel == null) {
            throw new IllegalArgumentException("模型不存在");
        }

        // DTO转PO
        AiClient aiClient = convertToAiClient(request);
        aiClient.setCreateTime(LocalDateTime.now());
        aiClient.setUpdateTime(LocalDateTime.now());

        int result = aiClientMapper.insert(aiClient);

//        // 保存 顾问配置
//        List<Long> advisorIds = request.getAdvisorIds();
//        if (CollectionUtil.isNotEmpty(advisorIds)) {
//            for (Long advisorId : advisorIds) {
//                AiClientAdvisorConfig advisorConfig = new AiClientAdvisorConfig();
//                advisorConfig.setClientId(aiClient.getId());
//                advisorConfig.setAdvisorId(advisorId);
//                clientAdvisorConfigMapper.insert(advisorConfig);
//            }
//        }

        // 保存 知识库
        List<Long> knowledgeIds = request.getKnowledgeIds();
        if (CollectionUtil.isNotEmpty(knowledgeIds)) {
            for (Long knowledgeId : knowledgeIds) {
                AiKnowledge knowledge = knowledgeMapper.selectById(knowledgeId);
                if (knowledge == null) {
                    throw new IllegalArgumentException("知识库不存在");
                }
                AiClientKnowledgeConfig knowledgeConfig = new AiClientKnowledgeConfig();
                knowledgeConfig.setClientId(aiClient.getId());
                knowledgeConfig.setKnowledgeId(knowledgeId);
                clientKnowledgeConfigMapper.insert(knowledgeConfig);
            }
        }

        // 保存MCP工具配置
        List<Long> mcpToolIds = request.getMcpToolIds();
        if (CollectionUtil.isNotEmpty(mcpToolIds)) {
            for (Long mcpToolId : mcpToolIds) {
                AiClientToolConfig mcpToolConfig = new AiClientToolConfig();
                mcpToolConfig.setClientId(aiClient.getId());
                mcpToolConfig.setToolId(mcpToolId);
                clientToolConfigMapper.insert(mcpToolConfig);
            }
        }

        // 注意这里需要等待之前的事物进行提交
        // 手动触发事务提交后再执行异步
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                agentAssemblyChain.handleWithEmptyCtx(AgentAssemblyInput.builder().clientIdList(List.of(aiClient.getId())).build());
                log.info("触发模型客户端构建, {}", aiClient.getId());
            }
        });

        return Response.<Boolean>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .info(ErrorCode.SUCCESS.getInfo())
                .data(result > 0)
                .build();
    }

    @Override
    @PutMapping("/update-by-id")
    @Transactional(rollbackFor = Exception.class)
    public Response<Boolean> updateAiClientById(@RequestBody AiClientRequestDTO request) {
        log.info("根据ID更新AI客户端配置请求：{}", request);

        if (request.getId() == null) {
            return Response.<Boolean>builder()
                    .code(ErrorCode.BIZ_ERROR.getCode())
                    .info("ID不能为空")
                    .data(false)
                    .build();
        }

        // DTO转PO
        AiClient aiClient = convertToAiClient(request);
        aiClient.setUpdateTime(LocalDateTime.now());

        int result = aiClientMapper.updateById(aiClient);

        // 先删除之前的顾问配置ID
        LambdaQueryWrapper<AiClientAdvisorConfig> wrapper = Wrappers.lambdaQuery(AiClientAdvisorConfig.class)
                .eq(AiClientAdvisorConfig::getClientId, aiClient.getId());
        clientAdvisorConfigMapper.delete(wrapper);

        // 保存 新顾问配置
        List<Long> advisorIds = request.getAdvisorIds();
        if (CollectionUtil.isNotEmpty(advisorIds)) {
            for (Long advisorId : advisorIds) {
                AiClientAdvisorConfig advisorConfig = new AiClientAdvisorConfig();
                advisorConfig.setClientId(aiClient.getId());
                advisorConfig.setAdvisorId(advisorId);
                clientAdvisorConfigMapper.insert(advisorConfig);
            }
        }

        // 先删除之前的MCP工具配置ID
        LambdaQueryWrapper<AiClientToolConfig> toolWrapper = Wrappers.lambdaQuery(AiClientToolConfig.class)
                .eq(AiClientToolConfig::getClientId, aiClient.getId());
        clientToolConfigMapper.delete(toolWrapper);

        // 保存 新MCP工具配置
        List<Long> mcpToolIds = request.getMcpToolIds();
        if (CollectionUtil.isNotEmpty(mcpToolIds)) {
            for (Long mcpToolId : mcpToolIds) {
                AiClientToolConfig mcpToolConfig = new AiClientToolConfig();
                mcpToolConfig.setClientId(aiClient.getId());
                mcpToolConfig.setToolId(mcpToolId);
                clientToolConfigMapper.insert(mcpToolConfig);
            }
        }

        // 先删除之前的知识库配置ID
        LambdaQueryWrapper<AiClientKnowledgeConfig> knowledgeWrapper = Wrappers.lambdaQuery(AiClientKnowledgeConfig.class)
                .eq(AiClientKnowledgeConfig::getClientId, aiClient.getId());
        clientKnowledgeConfigMapper.delete(knowledgeWrapper);

        // 保存 新知识库配置
        List<Long> knowledgeIds = request.getKnowledgeIds();
        if (CollectionUtil.isNotEmpty(knowledgeIds)) {
            for (Long knowledgeId : knowledgeIds) {
                AiKnowledge knowledge = knowledgeMapper.selectById(knowledgeId);
                if (knowledge == null) {
                    throw new IllegalArgumentException("知识库不存在");
                }
                AiClientKnowledgeConfig knowledgeConfig = new AiClientKnowledgeConfig();
                knowledgeConfig.setClientId(aiClient.getId());
                knowledgeConfig.setKnowledgeId(knowledgeId);
                clientKnowledgeConfigMapper.insert(knowledgeConfig);
            }
        }

        // 注意这里需要等待之前的事物进行提交
        // 手动触发事务提交后再执行异步
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                agentAssemblyChain.handle(new DefaultChainContext(), AgentAssemblyInput.builder().clientIdList(List.of(aiClient.getId())).build());
            }
        });

        return Response.<Boolean>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .info(ErrorCode.SUCCESS.getInfo())
                .data(result > 0)
                .build();
    }

    @Override
    @PutMapping("/update-by-client-id")
    public Response<Boolean> updateAiClientByClientId(@RequestBody AiClientRequestDTO request) {
        try {
            log.info("根据客户端ID更新AI客户端配置请求：{}", request);

            if (request.getId() == null) {
                return Response.<Boolean>builder()
                        .code(ErrorCode.BIZ_ERROR.getCode())
                        .info("客户端ID不能为空")
                        .data(false)
                        .build();
            }

            // DTO转PO
            AiClient aiClient = convertToAiClient(request);
            aiClient.setUpdateTime(LocalDateTime.now());

            int result = aiClientMapper.updateById(aiClient);

            return Response.<Boolean>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(result > 0)
                    .build();
        } catch (Exception e) {
            log.error("根据客户端ID更新AI客户端配置失败", e);
            return Response.<Boolean>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @Override
    @DeleteMapping("/delete-by-id/{id}")
    public Response<Boolean> deleteAiClientById(@PathVariable Long id) {
        try {
            log.info("根据ID删除AI客户端配置请求：{}", id);

            int result = aiClientMapper.deleteById(id);

            return Response.<Boolean>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(result > 0)
                    .build();
        } catch (Exception e) {
            log.error("根据ID删除AI客户端配置失败", e);
            return Response.<Boolean>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @Override
    @DeleteMapping("/delete-by-client-id/{clientId}")
    public Response<Boolean> deleteAiClientByClientId(@PathVariable String clientId) {
        try {
            log.info("根据客户端ID删除AI客户端配置请求：{}", clientId);

            int result = aiClientMapper.deleteByClientId(clientId);

            return Response.<Boolean>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(result > 0)
                    .build();
        } catch (Exception e) {
            log.error("根据客户端ID删除AI客户端配置失败", e);
            return Response.<Boolean>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @Override
    @GetMapping("/query-by-id/{id}")
    public Response<AiClientResponseDTO> queryAiClientById(@PathVariable Long id) {
        try {
            log.info("根据ID查询AI客户端配置请求：{}", id);

            AiClient aiClient = aiClientMapper.queryById(id);

            if (aiClient == null) {
                return Response.<AiClientResponseDTO>builder()
                        .code(ErrorCode.UN_ERROR.getCode())
                        .info("未找到对应的AI客户端配置")
                        .data(null)
                        .build();
            }

            // PO转DTO
            AiClientResponseDTO responseDTO = convertToAiClientResponseDTO(aiClient);

            return Response.<AiClientResponseDTO>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
        } catch (Exception e) {
            log.error("根据ID查询AI客户端配置失败", e);
            return Response.<AiClientResponseDTO>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    @Override
    @GetMapping("/query-by-client-id/{clientId}")
    public Response<AiClientResponseDTO> queryAiClientByClientId(@PathVariable String clientId) {
        try {
            log.info("根据客户端ID查询AI客户端配置请求：{}", clientId);

            AiClient aiClient = aiClientMapper.queryByClientId(clientId);

            if (aiClient == null) {
                return Response.<AiClientResponseDTO>builder()
                        .code(ErrorCode.UN_ERROR.getCode())
                        .info("未找到对应的AI客户端配置")
                        .data(null)
                        .build();
            }

            // PO转DTO
            AiClientResponseDTO responseDTO = convertToAiClientResponseDTO(aiClient);

            return Response.<AiClientResponseDTO>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
        } catch (Exception e) {
            log.error("根据客户端ID查询AI客户端配置失败", e);
            return Response.<AiClientResponseDTO>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    @Override
    @GetMapping("/query-enabled")
    public Response<List<AiClientResponseDTO>> queryEnabledAiClients() {
        try {
            log.info("查询所有启用的AI客户端配置");

            List<AiClient> aiClients = aiClientMapper.queryEnabledClients();

            List<AiClientResponseDTO> responseDTOs = aiClients.stream()
                    .map(this::convertToAiClientResponseDTO)
                    .collect(Collectors.toList());

            return Response.<List<AiClientResponseDTO>>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(responseDTOs)
                    .build();
        } catch (Exception e) {
            log.error("查询所有启用的AI客户端配置失败", e);
            return Response.<List<AiClientResponseDTO>>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    @Override
    @PostMapping("/query-list")
    public Response<List<AiClientResponseDTO>> queryAiClientList(@RequestBody AiClientQueryRequestDTO request) {
        log.info("根据条件查询AI客户端配置列表请求：{}", request);

        LambdaQueryWrapper<AiClient> clientWrapper = Wrappers.lambdaQuery(AiClient.class)
                .eq(request.getClientId() != null, AiClient::getId, request.getClientId())
                .eq(StrUtil.isNotEmpty(request.getClientName()), AiClient::getClientName, request.getClientName())
                .eq(request.getStatus() != null, AiClient::getStatus, request.getStatus())
                .orderByDesc(AiClient::getUpdateTime);

        List<AiClient> aiClients = aiClientMapper.selectList(clientWrapper);

        // 分页处理（简单实现）
        if (request.getPageNum() != null && request.getPageSize() != null) {
            int start = (request.getPageNum() - 1) * request.getPageSize();
            int end = Math.min(start + request.getPageSize(), aiClients.size());
            if (start < aiClients.size()) {
                aiClients = aiClients.subList(start, end);
            } else {
                aiClients = List.of();
            }
        }

        List<AiClientResponseDTO> responseDTOs = aiClients.stream()
                .map(this::convertToAiClientResponseDTO)
                .peek((client) -> {
                    AiClientModel aiClientModel = modelMapper.selectById(client.getModelId());
                    if (aiClientModel != null) {
                        client.setModelName(aiClientModel.getModelApiName());
                    }
                    LambdaQueryWrapper<AiClientAdvisorConfig> wrapper = Wrappers.lambdaQuery(AiClientAdvisorConfig.class)
                            .eq(AiClientAdvisorConfig::getClientId, client.getId());

                    // 获取顾问ID
                    List<Long> advisorIds = clientAdvisorConfigMapper.selectList(wrapper).stream().map(AiClientAdvisorConfig::getAdvisorId).toList();
                    if (CollectionUtil.isNotEmpty(advisorIds)) {
                        client.setAdvisorIds(advisorIds);
                        List<String> advisorNames = advisorMapper.selectBatchIds(advisorIds).stream().map(AiClientAdvisor::getAdvisorName).toList();
                        client.setAdvisorNames(advisorNames);
                    }

                    // 获取工具ID
                    LambdaQueryWrapper<AiClientToolConfig> toolWrapper = Wrappers.lambdaQuery(AiClientToolConfig.class)
                            .eq(AiClientToolConfig::getClientId, client.getId());
                    List<Long> toolIds = clientToolConfigMapper.selectList(toolWrapper).stream().map(AiClientToolConfig::getToolId).toList();
                    if (CollectionUtil.isNotEmpty(toolIds)) {
                        client.setMcpToolIds(toolIds);
                        List<String> toolNames = toolMapper.selectBatchIds(toolIds).stream().map(AiClientToolMcp::getMcpName).toList();
                        client.setMcpToolNames(toolNames);
                    }

                    // 获取知识库ID
                    LambdaQueryWrapper<AiClientKnowledgeConfig> knowledgeWrapper = Wrappers.lambdaQuery(AiClientKnowledgeConfig.class)
                            .eq(AiClientKnowledgeConfig::getClientId, client.getId());
                    List<Long> knowledgeIds = clientKnowledgeConfigMapper.selectList(knowledgeWrapper).stream().map(AiClientKnowledgeConfig::getKnowledgeId).toList();
                    if (CollectionUtil.isNotEmpty(knowledgeIds)) {
                        client.setKnowledgeIds(knowledgeIds);
                    }
                })
                .collect(Collectors.toList());

        return Response.<List<AiClientResponseDTO>>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .info(ErrorCode.SUCCESS.getInfo())
                .data(responseDTOs)
                .build();
    }

    @Override
    @GetMapping("/query-all")
    public Response<List<AiClientResponseDTO>> queryAllAiClients() {
        try {
            log.info("查询所有AI客户端配置");

            List<AiClient> aiClients = aiClientMapper.queryAll();

            List<AiClientResponseDTO> responseDTOs = aiClients.stream()
                    .map(this::convertToAiClientResponseDTO)
                    .collect(Collectors.toList());

            return Response.<List<AiClientResponseDTO>>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(responseDTOs)
                    .build();
        } catch (Exception e) {
            log.error("查询所有AI客户端配置失败", e);
            return Response.<List<AiClientResponseDTO>>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    /**
     * DTO转PO对象
     */
    private AiClient convertToAiClient(AiClientRequestDTO requestDTO) {
        AiClient aiClient = new AiClient();
        BeanUtils.copyProperties(requestDTO, aiClient);
        return aiClient;
    }

    /**
     * PO转DTO对象
     */
    private AiClientResponseDTO convertToAiClientResponseDTO(AiClient aiClient) {
        AiClientResponseDTO responseDTO = new AiClientResponseDTO();
        BeanUtils.copyProperties(aiClient, responseDTO);
        return responseDTO;
    }

}
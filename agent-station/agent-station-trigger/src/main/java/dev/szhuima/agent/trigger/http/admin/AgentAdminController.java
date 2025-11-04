package dev.szhuima.agent.trigger.http.admin;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import dev.szhuima.agent.api.ErrorCode;
import dev.szhuima.agent.api.IAgentAdminService;
import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.api.dto.AiClientQueryRequestDTO;
import dev.szhuima.agent.api.dto.AiClientRequestDTO;
import dev.szhuima.agent.api.dto.AiClientResponseDTO;
import dev.szhuima.agent.infrastructure.entity.TbAgent;
import dev.szhuima.agent.infrastructure.entity.TbAgentKnowledgeConfig;
import dev.szhuima.agent.infrastructure.entity.TbKnowledge;
import dev.szhuima.agent.infrastructure.entity.TbModelApi;
import dev.szhuima.agent.infrastructure.mapper.AgentKnowledgeConfigMapper;
import dev.szhuima.agent.infrastructure.mapper.AgentMapper;
import dev.szhuima.agent.infrastructure.mapper.KnowledgeMapper;
import dev.szhuima.agent.infrastructure.mapper.ModelApiMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/v1/admin/ai-client")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AgentAdminController implements IAgentAdminService {

    @Resource
    private AgentMapper agentMapper;

    @Resource
    private ModelApiMapper modelMapper;


    @Resource
    private KnowledgeMapper knowledgeMapper;

    @Resource
    private AgentKnowledgeConfigMapper clientKnowledgeConfigMapper;


    @Override
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public Response<Boolean> createAiClient(@RequestBody AiClientRequestDTO request) {
        log.info("创建AI客户端配置请求：{}", request);

        Long modelId = request.getModelId();
        if (modelId == null) {
            throw new IllegalArgumentException("模型未选择");
        }
        TbModelApi tbModelApi = modelMapper.selectById(modelId);
        if (tbModelApi == null) {
            throw new IllegalArgumentException("模型不存在");
        }

        // DTO转PO
        TbAgent tbAgent = convertToAiClient(request);
        tbAgent.setCreateTime(LocalDateTime.now());
        tbAgent.setUpdateTime(LocalDateTime.now());

        int result = agentMapper.insert(tbAgent);

        // 保存 知识库
        List<Long> knowledgeIds = request.getKnowledgeIds();
        if (CollectionUtil.isNotEmpty(knowledgeIds)) {
            for (Long knowledgeId : knowledgeIds) {
                TbKnowledge knowledge = knowledgeMapper.selectById(knowledgeId);
                if (knowledge == null) {
                    throw new IllegalArgumentException("知识库不存在");
                }
                TbAgentKnowledgeConfig knowledgeConfig = new TbAgentKnowledgeConfig();
                knowledgeConfig.setClientId(tbAgent.getId());
                knowledgeConfig.setKnowledgeId(knowledgeId);
                clientKnowledgeConfigMapper.insert(knowledgeConfig);
            }
        }

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
        TbAgent tbAgent = convertToAiClient(request);
        tbAgent.setUpdateTime(LocalDateTime.now());

        int result = agentMapper.updateById(tbAgent);


        // 先删除之前的知识库配置ID
        LambdaQueryWrapper<TbAgentKnowledgeConfig> knowledgeWrapper = Wrappers.lambdaQuery(TbAgentKnowledgeConfig.class)
                .eq(TbAgentKnowledgeConfig::getClientId, tbAgent.getId());
        clientKnowledgeConfigMapper.delete(knowledgeWrapper);

        // 保存 新知识库配置
        List<Long> knowledgeIds = request.getKnowledgeIds();
        if (CollectionUtil.isNotEmpty(knowledgeIds)) {
            for (Long knowledgeId : knowledgeIds) {
                TbKnowledge knowledge = knowledgeMapper.selectById(knowledgeId);
                if (knowledge == null) {
                    throw new IllegalArgumentException("知识库不存在");
                }
                TbAgentKnowledgeConfig knowledgeConfig = new TbAgentKnowledgeConfig();
                knowledgeConfig.setClientId(tbAgent.getId());
                knowledgeConfig.setKnowledgeId(knowledgeId);
                clientKnowledgeConfigMapper.insert(knowledgeConfig);
            }
        }


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
            TbAgent tbAgent = convertToAiClient(request);
            tbAgent.setUpdateTime(LocalDateTime.now());

            int result = agentMapper.updateById(tbAgent);

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

            int result = agentMapper.deleteById(id);

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
    @GetMapping("/query-by-id/{id}")
    public Response<AiClientResponseDTO> queryAiClientById(@PathVariable Long id) {
        try {
            log.info("根据ID查询AI客户端配置请求：{}", id);

            TbAgent tbAgent = agentMapper.queryById(id);

            if (tbAgent == null) {
                return Response.<AiClientResponseDTO>builder()
                        .code(ErrorCode.UN_ERROR.getCode())
                        .info("未找到对应的AI客户端配置")
                        .data(null)
                        .build();
            }

            // PO转DTO
            AiClientResponseDTO responseDTO = convertToAiClientResponseDTO(tbAgent);

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
    @GetMapping("/query-enabled")
    public Response<List<AiClientResponseDTO>> queryEnabledAiClients() {
        try {
            log.info("查询所有启用的AI客户端配置");

            List<TbAgent> tbAgents = agentMapper.queryEnabledClients();

            List<AiClientResponseDTO> responseDTOs = tbAgents.stream()
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

        LambdaQueryWrapper<TbAgent> clientWrapper = Wrappers.lambdaQuery(TbAgent.class)
                .eq(request.getClientId() != null, TbAgent::getId, request.getClientId())
                .eq(StrUtil.isNotEmpty(request.getClientName()), TbAgent::getAgentName, request.getClientName())
                .eq(request.getStatus() != null, TbAgent::getStatus, request.getStatus())
                .orderByDesc(TbAgent::getUpdateTime);

        List<TbAgent> tbAgents = agentMapper.selectList(clientWrapper);

        // 分页处理（简单实现）
        if (request.getPageNum() != null && request.getPageSize() != null) {
            int start = (request.getPageNum() - 1) * request.getPageSize();
            int end = Math.min(start + request.getPageSize(), tbAgents.size());
            if (start < tbAgents.size()) {
                tbAgents = tbAgents.subList(start, end);
            } else {
                tbAgents = List.of();
            }
        }

        List<AiClientResponseDTO> responseDTOs = tbAgents.stream()
                .map(this::convertToAiClientResponseDTO)
                .peek((client) -> {
                    TbModelApi tbModelApi = modelMapper.selectById(client.getModelId());
                    if (tbModelApi != null) {
                        client.setModelName(tbModelApi.getModelApiName());
                    }

                    // 获取知识库ID
                    LambdaQueryWrapper<TbAgentKnowledgeConfig> knowledgeWrapper = Wrappers.lambdaQuery(TbAgentKnowledgeConfig.class)
                            .eq(TbAgentKnowledgeConfig::getClientId, client.getId());
                    List<Long> knowledgeIds = clientKnowledgeConfigMapper.selectList(knowledgeWrapper).stream().map(TbAgentKnowledgeConfig::getKnowledgeId).toList();
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


    /**
     * DTO转PO对象
     */
    private TbAgent convertToAiClient(AiClientRequestDTO requestDTO) {
        TbAgent tbAgent = new TbAgent();
        BeanUtils.copyProperties(requestDTO, tbAgent);
        return tbAgent;
    }

    /**
     * PO转DTO对象
     */
    private AiClientResponseDTO convertToAiClientResponseDTO(TbAgent tbAgent) {
        AiClientResponseDTO responseDTO = new AiClientResponseDTO();
        BeanUtils.copyProperties(tbAgent, responseDTO);
        return responseDTO;
    }

}
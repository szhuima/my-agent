package dev.szhuima.agent.trigger.http.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.szhuima.agent.api.ErrorCode;
import dev.szhuima.agent.api.IAiClientModelAdminService;
import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.api.dto.*;
import dev.szhuima.agent.domain.agent.model.ModelApi;
import dev.szhuima.agent.domain.agent.repository.IModelApiRepository;
import dev.szhuima.agent.domain.support.exception.BizException;
import dev.szhuima.agent.infrastructure.entity.TbModelApi;
import dev.szhuima.agent.infrastructure.factory.ChatModelFactory;
import dev.szhuima.agent.infrastructure.mapper.AgentMapper;
import dev.szhuima.agent.infrastructure.mapper.ModelApiMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI客户端模型管理控制器
 *
 * @author szhuima
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/model-api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ModelApiController extends BaseController implements IAiClientModelAdminService {

    @Resource
    private ModelApiMapper modelApiMapper;

    @Resource
    private IModelApiRepository modelApiRepository;

    @Resource
    private AgentMapper clientMapper;

    @Resource
    private ChatModelFactory chatModelFactory;

    /**
     * 非流式输出的聊天，具备对话记忆功能
     *
     * @param request
     * @return
     */
    @PostMapping("/chat-non-stream")
    public Response<ChatMessageResponse> nonStreamChat(@RequestBody ModelApiChatRequest request) {
        if (request.getModelApiId() == null || StrUtil.isBlank(request.getUserMessage())) {
            throw new BizException("modelApiId 非法参数");
        }
        ModelApi modelApi = modelApiRepository.getModelApi(request.getModelApiId());
        if (modelApi == null) {
            throw BizException.of("该模型API不存在");
        }

        ChatModel chatModel = chatModelFactory.getOrCreate(modelApi);

        String result = chatModel.call(request.getUserMessage());

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .content(result)
                .build();
        return Response.success(chatMessageResponse);
    }


    /**
     * 非流式输出的聊天，具备对话记忆功能
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody ModelApiChatRequest request) {
        if (request.getModelApiId() == null || StrUtil.isBlank(request.getUserMessage())) {
            throw new BizException("modelApiId 非法参数");
        }
        ModelApi modelApi = modelApiRepository.getModelApi(request.getModelApiId());
        if (modelApi == null) {
            throw BizException.of("该模型API不存在");
        }

        ChatModel chatModel = chatModelFactory.getOrCreate(modelApi);
        Flux<String> stream = chatModel.stream(request.getUserMessage());

        return stream;
    }


    @Override
    @PostMapping("/create")
    public Response<Boolean> createAiClientModel(@RequestBody AiClientModelRequestDTO request) {
        log.info("创建AI客户端模型配置请求：{}", request);

        // DTO转PO
        TbModelApi tbModelApi = convertToAiClientModel(request);
        tbModelApi.setCreateTime(LocalDateTime.now());
        tbModelApi.setUpdateTime(LocalDateTime.now());

        int result = modelApiMapper.insert(tbModelApi);

        return Response.<Boolean>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .info(ErrorCode.SUCCESS.getInfo())
                .data(result > 0)
                .build();
    }

    @Override
    @PostMapping("/update-by-id")
    public Response<Boolean> updateAiClientModelById(@RequestBody AiClientModelRequestDTO request) {
        log.info("根据ID更新AI客户端模型配置请求：{}", request);

        if (request.getId() == null) {
            return Response.<Boolean>builder()
                    .code(ErrorCode.BIZ_ERROR.getCode())
                    .info("ID不能为空")
                    .data(false)
                    .build();
        }

        // DTO转PO
        TbModelApi tbModelApi = convertToAiClientModel(request);
        tbModelApi.setUpdateTime(LocalDateTime.now());

        int result = modelApiMapper.updateById(tbModelApi);

        return Response.<Boolean>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .info(ErrorCode.SUCCESS.getInfo())
                .data(result > 0)
                .build();
    }


    @Override
    @DeleteMapping("/delete-by-id/{id}")
    public Response<Boolean> deleteAiClientModelById(@PathVariable Long id) {
        try {
            log.info("根据ID删除AI客户端模型配置请求：{}", id);

            int result = modelApiMapper.deleteById(id);

            return Response.<Boolean>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(result > 0)
                    .build();
        } catch (Exception e) {
            log.error("根据ID删除AI客户端模型配置失败", e);
            return Response.<Boolean>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @Override
    @DeleteMapping("/delete-by-model-id/{modelId}")
    public Response<Boolean> deleteAiClientModelByModelId(@PathVariable String modelId) {
        try {
            log.info("根据模型ID删除AI客户端模型配置请求：{}", modelId);

            int result = modelApiMapper.deleteById(modelId);

            return Response.<Boolean>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(result > 0)
                    .build();
        } catch (Exception e) {
            log.error("根据模型ID删除AI客户端模型配置失败", e);
            return Response.<Boolean>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @Override
    @GetMapping("/query-by-id/{id}")
    public Response<AiClientModelResponseDTO> queryAiClientModelById(@PathVariable Long id) {
        try {
            log.info("根据ID查询AI客户端模型配置请求：{}", id);

            TbModelApi tbModelApi = modelApiMapper.selectById(id);

            if (tbModelApi == null) {
                return Response.<AiClientModelResponseDTO>builder()
                        .code(ErrorCode.UN_ERROR.getCode())
                        .info("未找到对应的AI客户端模型配置")
                        .data(null)
                        .build();
            }

            // PO转DTO
            AiClientModelResponseDTO responseDTO = convertToAiClientModelResponseDTO(tbModelApi);

            return Response.<AiClientModelResponseDTO>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
        } catch (Exception e) {
            log.error("根据ID查询AI客户端模型配置失败", e);
            return Response.<AiClientModelResponseDTO>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    @Override
    @GetMapping("/query-by-model-id/{modelId}")
    public Response<AiClientModelResponseDTO> queryAiClientModelByModelId(@PathVariable String modelId) {
        try {
            log.info("根据模型ID查询AI客户端模型配置请求：{}", modelId);

            TbModelApi tbModelApi = modelApiMapper.selectById(modelId);

            if (tbModelApi == null) {
                return Response.<AiClientModelResponseDTO>builder()
                        .code(ErrorCode.UN_ERROR.getCode())
                        .info("未找到对应的AI客户端模型配置")
                        .data(null)
                        .build();
            }

            // PO转DTO
            AiClientModelResponseDTO responseDTO = convertToAiClientModelResponseDTO(tbModelApi);

            return Response.<AiClientModelResponseDTO>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
        } catch (Exception e) {
            log.error("根据模型ID查询AI客户端模型配置失败", e);
            return Response.<AiClientModelResponseDTO>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    @Override
    @GetMapping("/query-by-model-type/{modelType}")
    public Response<List<AiClientModelResponseDTO>> queryAiClientModelsByModelType(@PathVariable String modelType) {
        try {
            log.info("根据模型类型查询AI客户端模型配置列表请求：{}", modelType);

            LambdaQueryWrapper<TbModelApi> wrapper = Wrappers.lambdaQuery(TbModelApi.class)
                    .eq(TbModelApi::getModelType, modelType);

            List<TbModelApi> tbModelApis = modelApiMapper.selectList(wrapper);

            // PO转DTO
            List<AiClientModelResponseDTO> responseDTOs = tbModelApis.stream()
                    .map(this::convertToAiClientModelResponseDTO)
                    .collect(Collectors.toList());

            return Response.<List<AiClientModelResponseDTO>>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(responseDTOs)
                    .build();
        } catch (Exception e) {
            log.error("根据模型类型查询AI客户端模型配置列表失败", e);
            return Response.<List<AiClientModelResponseDTO>>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    @Override
    @GetMapping("/query-enabled")
    public Response<List<AiClientModelResponseDTO>> queryEnabledAiClientModels() {
        try {
            log.info("查询所有启用的AI客户端模型配置请求");

            LambdaQueryWrapper<TbModelApi> wrapper = Wrappers.lambdaQuery(TbModelApi.class)
                    .eq(TbModelApi::getStatus, 1);

            List<TbModelApi> tbModelApis = modelApiMapper.selectList(wrapper);

            // PO转DTO
            List<AiClientModelResponseDTO> responseDTOs = tbModelApis.stream()
                    .map(this::convertToAiClientModelResponseDTO)
                    .collect(Collectors.toList());

            return Response.<List<AiClientModelResponseDTO>>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(responseDTOs)
                    .build();
        } catch (Exception e) {
            log.error("查询所有启用的AI客户端模型配置失败", e);
            return Response.<List<AiClientModelResponseDTO>>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    @Override
    @PostMapping("/query-list")
    public Response<PageDTO<AiClientModelResponseDTO>> queryAiClientModelList(@RequestBody AiClientModelQueryRequestDTO request) {
        IPage<TbModelApi> page = new Page<>(request.getPageNum(), request.getPageSize());

        // 根据不同条件查询
        LambdaQueryWrapper<TbModelApi> wrapper = Wrappers.lambdaQuery(TbModelApi.class)
                .eq(StringUtils.hasText(request.getModelId()), TbModelApi::getId, request.getModelId())
                .eq(StringUtils.hasText(request.getModelType()), TbModelApi::getModelType, request.getModelType())
                .eq(request.getStatus() != null, TbModelApi::getStatus, request.getStatus())
                .eq(StringUtils.hasText(request.getModelApiName()), TbModelApi::getModelApiName, request.getModelApiName())
                .orderByDesc(TbModelApi::getUpdateTime);

        // 查询AI客户端模型配置列表
        IPage<TbModelApi> aiClientModelIPage = modelApiMapper.selectPage(page, wrapper);
        PageDTO<AiClientModelResponseDTO> pageDTO = copyPage(aiClientModelIPage, AiClientModelResponseDTO.class);


        return Response.success(pageDTO);
    }

    @Override
    @GetMapping("/query-all")
    public Response<List<AiClientModelResponseDTO>> queryAllAiClientModels() {
        try {
            log.info("查询所有AI客户端模型配置请求");

            List<TbModelApi> tbModelApis = modelApiMapper.selectList(Wrappers.emptyWrapper());

            // PO转DTO
            List<AiClientModelResponseDTO> responseDTOs = tbModelApis.stream()
                    .map(this::convertToAiClientModelResponseDTO)
                    .collect(Collectors.toList());

            return Response.<List<AiClientModelResponseDTO>>builder()
                    .code(ErrorCode.SUCCESS.getCode())
                    .info(ErrorCode.SUCCESS.getInfo())
                    .data(responseDTOs)
                    .build();
        } catch (Exception e) {
            log.error("查询所有AI客户端模型配置失败", e);
            return Response.<List<AiClientModelResponseDTO>>builder()
                    .code(ErrorCode.UN_ERROR.getCode())
                    .info(ErrorCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    /**
     * DTO转PO对象
     */
    private TbModelApi convertToAiClientModel(AiClientModelRequestDTO requestDTO) {
        TbModelApi tbModelApi = new TbModelApi();
        BeanUtils.copyProperties(requestDTO, tbModelApi);
        return tbModelApi;
    }

    /**
     * PO转DTO对象
     */
    private AiClientModelResponseDTO convertToAiClientModelResponseDTO(TbModelApi tbModelApi) {
        AiClientModelResponseDTO responseDTO = new AiClientModelResponseDTO();
        BeanUtils.copyProperties(tbModelApi, responseDTO);
        return responseDTO;
    }

}
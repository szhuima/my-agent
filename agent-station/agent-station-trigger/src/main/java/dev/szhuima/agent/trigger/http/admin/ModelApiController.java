package dev.szhuima.agent.trigger.http.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.szhuima.agent.api.IAiClientModelAdminService;
import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.api.ResponseCode;
import dev.szhuima.agent.api.dto.AiClientModelQueryRequestDTO;
import dev.szhuima.agent.api.dto.AiClientModelRequestDTO;
import dev.szhuima.agent.api.dto.AiClientModelResponseDTO;
import dev.szhuima.agent.api.dto.PageDTO;
import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
import dev.szhuima.agent.domain.agent.service.config.handler.AgentModelAssembler;
import dev.szhuima.agent.domain.support.chain.DefaultChainContext;
import dev.szhuima.agent.domain.support.chain.HandlerChain;
import dev.szhuima.agent.infrastructure.mapper.AiClientMapper;
import dev.szhuima.agent.infrastructure.mapper.AiClientModelMapper;
import dev.szhuima.agent.infrastructure.po.AiClient;
import dev.szhuima.agent.infrastructure.po.AiClientModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI客户端模型管理控制器
 *
 * @author szhuima
 * @description AI客户端模型配置管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/ai-client-model")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ModelApiController extends BaseController implements IAiClientModelAdminService {

    @Resource
    private AiClientModelMapper aiClientModelDao;

    @Resource
    private AiClientMapper clientMapper;

    @Resource
    @Qualifier("agentAssemblyChain")
    private HandlerChain agentAssemblyChain;

    @Override
    @PostMapping("/create")
    public Response<Boolean> createAiClientModel(@RequestBody AiClientModelRequestDTO request) {
        log.info("创建AI客户端模型配置请求：{}", request);

        // DTO转PO
        AiClientModel aiClientModel = convertToAiClientModel(request);
        aiClientModel.setCreateTime(LocalDateTime.now());
        aiClientModel.setUpdateTime(LocalDateTime.now());

        int result = aiClientModelDao.insert(aiClientModel);

        return Response.<Boolean>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(result > 0)
                .build();
    }

    @Override
    @PostMapping("/update-by-id")
    public Response<Boolean> updateAiClientModelById(@RequestBody AiClientModelRequestDTO request) {
        log.info("根据ID更新AI客户端模型配置请求：{}", request);

        if (request.getId() == null) {
            return Response.<Boolean>builder()
                    .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                    .info("ID不能为空")
                    .data(false)
                    .build();
        }

        // DTO转PO
        AiClientModel aiClientModel = convertToAiClientModel(request);
        aiClientModel.setUpdateTime(LocalDateTime.now());

        int result = aiClientModelDao.updateById(aiClientModel);

        // 重新装配
        LambdaQueryWrapper<AiClient> wrapper = Wrappers.lambdaQuery(AiClient.class)
                .eq(AiClient::getModelId, aiClientModel.getId())
                .select(AiClient::getId);
        List<Long> clientIds = clientMapper.selectList(wrapper)
                .stream().map(AiClient::getId).toList();

        if (!clientIds.isEmpty()) {
            AgentAssemblyInput input = AgentAssemblyInput.builder()
                    .clientIdList(clientIds)
                    .assemblerName(AgentModelAssembler.class.getSimpleName())
                    .build();
            agentAssemblyChain.handle(new DefaultChainContext(), input);
        }

        return Response.<Boolean>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(result > 0)
                .build();
    }


    @Override
    @DeleteMapping("/delete-by-id/{id}")
    public Response<Boolean> deleteAiClientModelById(@PathVariable Long id) {
        try {
            log.info("根据ID删除AI客户端模型配置请求：{}", id);

            int result = aiClientModelDao.deleteById(id);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result > 0)
                    .build();
        } catch (Exception e) {
            log.error("根据ID删除AI客户端模型配置失败", e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @Override
    @DeleteMapping("/delete-by-model-id/{modelId}")
    public Response<Boolean> deleteAiClientModelByModelId(@PathVariable String modelId) {
        try {
            log.info("根据模型ID删除AI客户端模型配置请求：{}", modelId);

            int result = aiClientModelDao.deleteById(modelId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result > 0)
                    .build();
        } catch (Exception e) {
            log.error("根据模型ID删除AI客户端模型配置失败", e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @Override
    @GetMapping("/query-by-id/{id}")
    public Response<AiClientModelResponseDTO> queryAiClientModelById(@PathVariable Long id) {
        try {
            log.info("根据ID查询AI客户端模型配置请求：{}", id);

            AiClientModel aiClientModel = aiClientModelDao.selectById(id);

            if (aiClientModel == null) {
                return Response.<AiClientModelResponseDTO>builder()
                        .code(ResponseCode.UN_ERROR.getCode())
                        .info("未找到对应的AI客户端模型配置")
                        .data(null)
                        .build();
            }

            // PO转DTO
            AiClientModelResponseDTO responseDTO = convertToAiClientModelResponseDTO(aiClientModel);

            return Response.<AiClientModelResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
        } catch (Exception e) {
            log.error("根据ID查询AI客户端模型配置失败", e);
            return Response.<AiClientModelResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    @Override
    @GetMapping("/query-by-model-id/{modelId}")
    public Response<AiClientModelResponseDTO> queryAiClientModelByModelId(@PathVariable String modelId) {
        try {
            log.info("根据模型ID查询AI客户端模型配置请求：{}", modelId);

            AiClientModel aiClientModel = aiClientModelDao.selectById(modelId);

            if (aiClientModel == null) {
                return Response.<AiClientModelResponseDTO>builder()
                        .code(ResponseCode.UN_ERROR.getCode())
                        .info("未找到对应的AI客户端模型配置")
                        .data(null)
                        .build();
            }

            // PO转DTO
            AiClientModelResponseDTO responseDTO = convertToAiClientModelResponseDTO(aiClientModel);

            return Response.<AiClientModelResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
        } catch (Exception e) {
            log.error("根据模型ID查询AI客户端模型配置失败", e);
            return Response.<AiClientModelResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

//    @Override
//    @GetMapping("/query-by-api-id/{apiId}")
//    public Response<List<AiClientModelResponseDTO>> queryAiClientModelsByApiId(@PathVariable String apiId) {
//        try {
//            log.info("根据API配置ID查询AI客户端模型配置列表请求：{}", apiId);
//
//            Wrappers.lambdaQuery(AiClientModel.class)
//                    .eq(AiClientModel::);
//
//            List<AiClientModel> aiClientModels = aiClientModelDao.selectList(apiId);
//
//            // PO转DTO
//            List<AiClientModelResponseDTO> responseDTOs = aiClientModels.stream()
//                    .map(this::convertToAiClientModelResponseDTO)
//                    .collect(Collectors.toList());
//
//            return Response.<List<AiClientModelResponseDTO>>builder()
//                    .code(ResponseCode.SUCCESS.getCode())
//                    .info(ResponseCode.SUCCESS.getInfo())
//                    .data(responseDTOs)
//                    .build();
//        } catch (Exception e) {
//            log.error("根据API配置ID查询AI客户端模型配置列表失败", e);
//            return Response.<List<AiClientModelResponseDTO>>builder()
//                    .code(ResponseCode.UN_ERROR.getCode())
//                    .info(ResponseCode.UN_ERROR.getInfo())
//                    .data(null)
//                    .build();
//        }
//    }

    @Override
    @GetMapping("/query-by-model-type/{modelType}")
    public Response<List<AiClientModelResponseDTO>> queryAiClientModelsByModelType(@PathVariable String modelType) {
        try {
            log.info("根据模型类型查询AI客户端模型配置列表请求：{}", modelType);

            LambdaQueryWrapper<AiClientModel> wrapper = Wrappers.lambdaQuery(AiClientModel.class)
                    .eq(AiClientModel::getModelType, modelType);

            List<AiClientModel> aiClientModels = aiClientModelDao.selectList(wrapper);

            // PO转DTO
            List<AiClientModelResponseDTO> responseDTOs = aiClientModels.stream()
                    .map(this::convertToAiClientModelResponseDTO)
                    .collect(Collectors.toList());

            return Response.<List<AiClientModelResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(responseDTOs)
                    .build();
        } catch (Exception e) {
            log.error("根据模型类型查询AI客户端模型配置列表失败", e);
            return Response.<List<AiClientModelResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    @Override
    @GetMapping("/query-enabled")
    public Response<List<AiClientModelResponseDTO>> queryEnabledAiClientModels() {
        try {
            log.info("查询所有启用的AI客户端模型配置请求");

            LambdaQueryWrapper<AiClientModel> wrapper = Wrappers.lambdaQuery(AiClientModel.class)
                    .eq(AiClientModel::getStatus, 1);

            List<AiClientModel> aiClientModels = aiClientModelDao.selectList(wrapper);

            // PO转DTO
            List<AiClientModelResponseDTO> responseDTOs = aiClientModels.stream()
                    .map(this::convertToAiClientModelResponseDTO)
                    .collect(Collectors.toList());

            return Response.<List<AiClientModelResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(responseDTOs)
                    .build();
        } catch (Exception e) {
            log.error("查询所有启用的AI客户端模型配置失败", e);
            return Response.<List<AiClientModelResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    @Override
    @PostMapping("/query-list")
    public Response<PageDTO<AiClientModelResponseDTO>> queryAiClientModelList(@RequestBody AiClientModelQueryRequestDTO request) {
        IPage<AiClientModel> page = new Page<>(request.getPageNum(), request.getPageSize());

        // 根据不同条件查询
        LambdaQueryWrapper<AiClientModel> wrapper = Wrappers.lambdaQuery(AiClientModel.class)
                .eq(StringUtils.hasText(request.getModelId()), AiClientModel::getId, request.getModelId())
                .eq(StringUtils.hasText(request.getModelType()), AiClientModel::getModelType, request.getModelType())
                .eq(request.getStatus() != null, AiClientModel::getStatus, request.getStatus())
                .eq(StringUtils.hasText(request.getModelApiName()), AiClientModel::getModelApiName, request.getModelApiName())
                .orderByDesc(AiClientModel::getUpdateTime);

        // 查询AI客户端模型配置列表
        IPage<AiClientModel> aiClientModelIPage = aiClientModelDao.selectPage(page, wrapper);
        PageDTO<AiClientModelResponseDTO> pageDTO = copyPage(aiClientModelIPage, AiClientModelResponseDTO.class);


        return Response.success(pageDTO);
    }

    @Override
    @GetMapping("/query-all")
    public Response<List<AiClientModelResponseDTO>> queryAllAiClientModels() {
        try {
            log.info("查询所有AI客户端模型配置请求");

            List<AiClientModel> aiClientModels = aiClientModelDao.selectList(Wrappers.emptyWrapper());

            // PO转DTO
            List<AiClientModelResponseDTO> responseDTOs = aiClientModels.stream()
                    .map(this::convertToAiClientModelResponseDTO)
                    .collect(Collectors.toList());

            return Response.<List<AiClientModelResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(responseDTOs)
                    .build();
        } catch (Exception e) {
            log.error("查询所有AI客户端模型配置失败", e);
            return Response.<List<AiClientModelResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    /**
     * DTO转PO对象
     */
    private AiClientModel convertToAiClientModel(AiClientModelRequestDTO requestDTO) {
        AiClientModel aiClientModel = new AiClientModel();
        BeanUtils.copyProperties(requestDTO, aiClientModel);
        return aiClientModel;
    }

    /**
     * PO转DTO对象
     */
    private AiClientModelResponseDTO convertToAiClientModelResponseDTO(AiClientModel aiClientModel) {
        AiClientModelResponseDTO responseDTO = new AiClientModelResponseDTO();
        BeanUtils.copyProperties(aiClientModel, responseDTO);
        return responseDTO;
    }

}
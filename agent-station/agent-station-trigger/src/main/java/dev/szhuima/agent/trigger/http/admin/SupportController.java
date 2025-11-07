package dev.szhuima.agent.trigger.http.admin;

import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.domain.agent.model.ModelSource;
import dev.szhuima.agent.domain.agent.model.ModelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/10/25 22:03
 * * @Description
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/support")
public class SupportController {


    @GetMapping("/model-source/query-list")
    public Response<List<ModelSource>> queryModelSourceList() {
        List<ModelSource> modelSources = Arrays.stream(ModelSource.values()).toList();
        return Response.success(modelSources);
    }


    @GetMapping("/model-type/query-list")
    public Response<List<ModelType>> queryModelTypeList() {
        List<ModelType> modelTypes = Arrays.stream(ModelType.values()).toList();
        return Response.success(modelTypes);
    }

}

package dev.szhuima.agent.domain.workflow.service.executor;

import com.alibaba.fastjson2.JSON;
import dev.szhuima.agent.domain.workflow.model.WorkflowContext;
import dev.szhuima.agent.domain.workflow.model.WorkflowDO;
import dev.szhuima.agent.domain.workflow.model.WorkflowNodeConfigHttp;
import dev.szhuima.agent.domain.workflow.model.WorkflowNodeDO;
import dev.szhuima.agent.domain.workflow.reository.IWorkflowRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/9/24 22:03
 * * @Description
 **/
@Slf4j
@Component
public class HttpNodeExecutor extends AbstractNodeExecutor {

    @Resource
    private IWorkflowRepository workflowRepository;

    private final WebClient webClient;

    public HttpNodeExecutor(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public NodeExecutionResult executeNode(WorkflowNodeDO node, WorkflowContext context, WorkflowDO workflowDO) {
        String configJson = node.getConfigJson();
        WorkflowNodeConfigHttp config = JSON.parseObject(configJson, WorkflowNodeConfigHttp.class);

        if (config == null) {
            return NodeExecutionResult.failure("http config not found");
        }
        try {
            // 渲染 url、queryParams、body
            Map<String, Object> all = context.getAll();
            String renderedUrl = render(config.getUrl(), all);
            String renderedBody = render(config.getBody(), all);
            Map<String, String> renderedParams = new HashMap<>();
            if (config.getParams() != null) {
                config.getParams().forEach((k, v) -> {
                    renderedParams.put(k, render(v.toString(), all));
                });
            }
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(renderedUrl);
            if (!renderedParams.isEmpty()) {
                renderedParams.forEach(builder::queryParam);
            }
            URI uri = builder.build().toUri();
            Map<String, String> reqHeaders = config.getHeaders();
            HttpHeaders httpHeaders = new HttpHeaders();
            if (reqHeaders != null) {
                reqHeaders.forEach(httpHeaders::add);
            }
            ResponseEntity<String> response = webClient.method(HttpMethod.valueOf(config.getMethod()))
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> h.addAll(httpHeaders))
                    .bodyValue(renderedBody == null ? BodyInserters.empty() : renderedBody)
                    .retrieve()
                    .toEntity(String.class)
                    .block(); // 同步执行

            if (response == null || response.getStatusCode() != HttpStatus.OK) {
                return NodeExecutionResult.failure("http call failed");
            }
            log.info("【{}】 uri:{}, params:{}, body:{}, response: {}", node.getName(), uri, renderedParams, renderedBody, response.getBody());

            HttpHeaders headers = response.getHeaders();
            MediaType contentType = headers.getContentType();
            if (contentType != null && contentType.includes(MediaType.APPLICATION_JSON)) {
                Object bodyObj = JSON.parse(response.getBody());
                return NodeExecutionResult.success(bodyObj);
            }
            return NodeExecutionResult.success(response.getBody());
        } catch (Exception e) {
            log.error("http call failed, node name: {}", node.getName(), e);
            return NodeExecutionResult.failure("http call failed: " + e.getMessage());
        }
    }
}

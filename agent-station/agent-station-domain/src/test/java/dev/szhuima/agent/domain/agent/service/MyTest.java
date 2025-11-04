package dev.szhuima.agent.domain.agent.service;

import com.googlecode.aviator.AviatorEvaluator;
import dev.szhuima.agent.domain.workflow.service.executor.HttpNodeExecutor;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/9/26 08:55
 * * @Description
 **/
public class MyTest {


    @Test
    public void testAviator() {

        // 构造上下文
        Map<String, Object> env = new HashMap<>();
        env.put("resume.matchScore", 0.5);
        env.put("job.minScore", 0.8);
        env.put("candidate.name", "Tom");

        // 1. 断言逻辑
        String expression = "resume.matchScore >= job.minScore? '符合要求' : '不符合要求'";
        String result = (String) AviatorEvaluator.execute(expression, env);
        System.out.println("是否满足条件: " + result);

    }


    @Test
    public void testRender() {
        String template = """
                {
                  "keyword": "{{root.keywords}}",
                  "pageSize": 1,
                  "page": 1,
                  "city": "{{root.city}}"
                }
                """;
        Map<String, Object> root = Map.of("keywords", "Jobs", "city", "北京");
        Map<String, Map<String, Object>> context = Map.of("root", root);
        String rendered = new HttpNodeExecutor(WebClient.builder()).render(template, context);
        System.out.println(rendered);
    }
}

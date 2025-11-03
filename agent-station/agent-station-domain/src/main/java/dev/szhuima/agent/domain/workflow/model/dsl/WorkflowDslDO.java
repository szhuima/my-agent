package dev.szhuima.agent.domain.workflow.model.dsl;

import com.alibaba.fastjson2.annotation.JSONField;
import dev.szhuima.agent.domain.workflow.model.TriggerType;
import lombok.Data;

import java.util.List;
import java.util.Map;

// 顶层 DSL
@Data
public class WorkflowDslDO {

    private String name;
    private String title;
    private Map<String,Object> meta;
    private List<BaseNode<?>> nodes;
    private List<Edge> edges;
    private List<BaseTrigger<?>> triggers;

    @Data
    public static class Edge {
        private String from;
        private String to;
    }

    @Data
    public static abstract class BaseTrigger<T> {
        private TriggerType triggerType;
        private T config;
    }

    // HTTP 触发节点
    @Data
    public static class HttpTrigger extends BaseTrigger<HttpTrigger.HttpConfig> {
//        private HttpConfig config;

        @Data
        public static class HttpConfig {
            private String url;
            private String method;
            private Map<String,String> headers;
            private List<Field> params;
            private List<Field> body;

            @Data
            public static class Field {
                private String key;
                private String type;
                private Boolean required;
                private String defaultValue;
            }
        }
    }

    // rabbitmq 触发节点
    @Data
    public static class RabbitmqTrigger extends BaseTrigger<RabbitmqTrigger.RabbitmqConfig> {
//        private RabbitmqConfig config;

        @Data
        public static class RabbitmqConfig {
            private String host;
            private Integer port;
            private String username;
            private String password;
            private String queue;
        }
    }



    @Data
    public static abstract class BaseNode<T> {
        private String id;
        private String type;
        private String name;
        private String title;
        private String condition;
        private Integer positionX;
        private Integer positionY;
        private Boolean startNode = false;
        private T config;
    }

    // 表单节点
    @Data
    public static class FormNode extends BaseNode<FormNode.FormConfig> {
//        private FormConfig config;

        @Data
        public static class FormConfig {
            private List<Field> fields;

            @Data
            public static class Field {
                private String key;
                private String type;
                private String title;
            }
        }
    }

    // HTTP 节点
    @Data
    public static class HttpNode extends BaseNode<HttpNode.HttpConfig> {
//        private HttpConfig config;

        @Data
        public static class HttpConfig {
            private String url;
            private String method;
            private Map<String,String> headers;
            private Map<String, Object> params; // 支持任意结构（模板字符串、对象）
            private Map<String, Object> body; // 支持任意结构（模板字符串、对象）
        }
    }


    // 批处理 / 遍历 节点
    @Data
    public static class BatchNode extends BaseNode<BatchNode.BatchConfig> {
//        private BatchConfig config;

        @Data
        public static class BatchConfig {
            private String items;                   // "{{ http_job_list.response }}"

            @JSONField(name = "item_key")
            private String itemKey;                 // item_key -> itemKey

            @JSONField(name = "body_nodes")
            private List<String> bodyNodes;         // body_nodes -> bodyNodes
        }
    }

    // Agent 节点
    @Data
    public static class AgentNode extends BaseNode<AgentNode.AgentConfig> {
//        private AgentConfig config;

        @Data
        public static class AgentConfig {
            @JSONField(name = "client_id")
            private Long clientId;

            @JSONField(name = "user_prompt")
            private String userPrompt;
        }
    }
}

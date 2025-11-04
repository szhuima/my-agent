package dev.szhuima.agent.domain.workflow.model.dsl;

import com.alibaba.fastjson2.annotation.JSONField;
import com.microsoft.schemas.office.visio.x2012.main.TriggerType;
import lombok.Data;

import java.util.List;
import java.util.Map;

// 顶层 DSL
@Data
public class WorkflowDsl {

    private String name;
    private String title;
    private Map<String, Object> meta;
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

        @Data
        public static class HttpConfig {
            private String url;
            private String method;
            private Map<String, String> headers;
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
        @JSONField(name = "position_x")
        private Integer positionX;
        @JSONField(name = "position_y")
        private Integer positionY;
        private T config;
    }

    // 启动节点
    @Data
    public static class StartNode extends BaseNode<StartNode.StartConfig> {

        @Data
        public static class StartConfig {
            // 启动类型：定时调度
            @JSONField(name = "start_type")
            private String startType;

            // 定时表达式
            @JSONField(name = "cron_expression")
            private String cronExpression;
        }
    }

    // 代码节点
    @Data
    public static class CodeNode extends BaseNode<CodeNode.CodeConfig> {

        @Data
        public static class CodeConfig {
            private String language;
            @JSONField(name = "code_content")
            private String codeContent;
        }
    }

    // HTTP 节点
    @Data
    public static class HttpNode extends BaseNode<HttpNode.HttpConfig> {
        @Data
        public static class HttpConfig {
            private String url;
            private String method;
            private Map<String, String> headers;
            private Map<String, Object> params; // 支持任意结构（模板字符串、对象）
            private Map<String, Object> body; // 支持任意结构（模板字符串、对象）
        }
    }


    // 批处理 / 遍历 节点
    @Data
    public static class BatchNode extends BaseNode<BatchNode.BatchConfig> {

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
        @Data
        public static class AgentConfig {
            @JSONField(name = "client_id")
            private Long clientId;

            @JSONField(name = "user_prompt")
            private String userPrompt;
        }
    }
}

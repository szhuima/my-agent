package dev.szhuima.agent.domain.workflow.service.executor;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

import java.util.Map;


@Data
public class NodeExecutionResult {

    private boolean completed;

    private Object output;

    private String reason;


    public static NodeExecutionResult conditionUnMatch(String message) {
        NodeExecutionResult result = new NodeExecutionResult();
        result.setCompleted(true);
        result.setReason(message);
        return result;
    }

    public static NodeExecutionResult success(Object output) {
        NodeExecutionResult result = new NodeExecutionResult();
        result.setCompleted(true);
        result.setOutput(output);
        return result;
    }

    public static NodeExecutionResult failure(String message) {
        NodeExecutionResult result = new NodeExecutionResult();
        result.setCompleted(false);
        result.setReason(message);
        return result;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}

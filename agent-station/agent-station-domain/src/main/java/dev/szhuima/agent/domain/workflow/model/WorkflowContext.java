package dev.szhuima.agent.domain.workflow.model;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// 运行时上下文（存储执行过程中的数据）
@Data
public class WorkflowContext {

    private Map<String, Object> variables = new LinkedHashMap<>();


    public static WorkflowContext fromString(String contextStr) {
        if (contextStr == null || contextStr.isEmpty()) {
            return new WorkflowContext();
        }
        Map<String, Object> contextMap = JSONObject.parseObject(contextStr);
        return new WorkflowContext(contextMap);
    }


    public WorkflowContext() {
    }

    public WorkflowContext(Map<String, Object> initialVariables) {
        if (CollectionUtil.isNotEmpty(initialVariables)) {
            variables.putAll(initialVariables);
        }
    }


    private void put(String key, Object value) {
        variables.put(key, value);
    }

    public Object get(String key) {
        return variables.get(key);
    }

    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(variables);
    }

    public void putAll(Map<String, Object> variables) {
        this.variables.putAll(variables);
    }


    public Object getJSONPath(String keyPath) {
        if (keyPath == null || keyPath.isEmpty()) {
            return null;
        }
        keyPath = keyPath.replace("-", "_");
        Object eval = JSONPath.eval(variables, keyPath);
        return eval;
    }

    public void putJSONPath(String keyPath, Object value) {
        keyPath = keyPath.replace("-", "_");
        JSONPath.set(variables, keyPath, value);
    }


    public void clear() {
        variables.clear();
    }


    @Override
    public String toString() {
        return JSON.toJSONString(variables);
    }

}

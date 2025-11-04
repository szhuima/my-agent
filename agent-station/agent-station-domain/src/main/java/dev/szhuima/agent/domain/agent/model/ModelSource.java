package dev.szhuima.agent.domain.agent.model;

import dev.szhuima.agent.domain.support.exception.BizException;

/**
 * * @Author: szhuima
 * * @Date    2025/10/25 21:56
 * * @Description
 **/
public enum ModelSource {
    OPENAI("openai"),
    OLLAMA("ollama");

    private final String name;

    ModelSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ModelSource fromName(String name) {
        for (ModelSource modelSource : ModelSource.values()) {
            if (modelSource.getName().equals(name)) {
                return modelSource;
            }
        }
        throw BizException.of("不支持模型厂商:" + name);
    }
}

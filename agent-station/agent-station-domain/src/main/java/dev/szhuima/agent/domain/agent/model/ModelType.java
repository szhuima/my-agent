package dev.szhuima.agent.domain.agent.model;

/**
 * * @Author: szhuima
 * * @Date    2025/10/25 21:56
 * * @Description
 **/
public enum ModelType {
    CHAT("chat"),
    EMBEDDING("embedding");

    private final String name;

    ModelType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

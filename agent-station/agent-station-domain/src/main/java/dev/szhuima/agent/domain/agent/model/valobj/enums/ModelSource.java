package dev.szhuima.agent.domain.agent.model.valobj.enums;

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
}

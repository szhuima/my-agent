package dev.szhuima.agent.domain.agent.model;

/**
 * * @Author: szhuima
 * * @Date    2025/11/7 00:25
 * * @Description
 **/
public enum McpTransportType {

    SSE("sse"),
    STUDIO("studio");

    private final String value;
    McpTransportType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static McpTransportType fromValue(String value) {
        for (McpTransportType type : McpTransportType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

}

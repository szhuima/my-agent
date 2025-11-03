package dev.szhuima.agent.domain.agent.model.valobj.enums;

/**
 * * @Author: szhuima
 * * @Date    2025/10/23 15:24
 * * @Description
 **/
public enum AdvisorType {

    /**
     * 对话记忆
     */
    CHAT_MEMORY("chat_memory"),

    /**
     * RAG 回答
     */
    RAG_ANSWER("rag_answer");

    private final String code;

    AdvisorType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}

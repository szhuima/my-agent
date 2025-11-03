package dev.szhuima.agent.domain.support.chain;

import java.util.Map;

public interface ChainContext {

    Map<String,Object> getAll();

    <T> T getValue(String key);

    Object get(String key);

    void put(String key, Object value);

    void putAll(Map<String, Object> data);

    default <T> T getAs(String key, Class<T> type) {
        Object v = get(key);
        if (v == null) return null;
        if (type.isInstance(v)) return type.cast(v);
        return null;
    }
}
package dev.szhuima.agent.domain.support.chain;

import java.util.HashMap;
import java.util.Map;

public class DefaultChainContext implements ChainContext {
    private final Map<String, Object> data = new HashMap<>();


    @Override
    public Map<String, Object> getAll() {
        return data;
    }

    @Override
    public <T> T getValue(String key) {
        return (T) get(key);
    }

    @Override
    public Object get(String key) {
        return data.get(key);
    }


    @Override
    public void put(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public void putAll(Map<String, Object> data) {
        this.data.putAll(data);
    }
}
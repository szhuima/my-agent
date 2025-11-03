package dev.szhuima.agent.infrastructure.util;

import com.alibaba.ttl.TransmittableThreadLocal;

public class UserContext {

    private static final TransmittableThreadLocal<String> USER_ID_HOLDER = new TransmittableThreadLocal<>();

    public static String getCurrentUserId() {
        return USER_ID_HOLDER.get();
    }

    public static void setUserId(String userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static void clear() {
        USER_ID_HOLDER.remove();
    }
}

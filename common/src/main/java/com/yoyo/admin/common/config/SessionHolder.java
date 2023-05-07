package com.yoyo.admin.common.config;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Session会话
 */
public class SessionHolder {

    private static final ConcurrentHashMap<Long, String> _webSessionMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Long, String> getWebMap() {
        return _webSessionMap;
    }

    public static String getWebAesKey() {
        return "cbbvystbbc521cbff452dd6fb77204cc";
    }

    public static String getWebSessionId(Long userId) {
        return _webSessionMap.get(userId);
    }

    public static void addWebSession(Long userId, String sessionId) {
        _webSessionMap.put(userId, sessionId);
    }

    public static void removeWebSession(Long userId) {
        _webSessionMap.remove(userId);
    }

}

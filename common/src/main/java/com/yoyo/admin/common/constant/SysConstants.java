package com.yoyo.admin.common.constant;

import java.util.*;

/**
 * 系统全局变量
 */
public class SysConstants {

    /**
     * 默认id
     */
    public static final Long DEFAULT_ID = 1L;

    /**
     * 默认管理员角色Code
     */
    public static final String DEFAULT_ADMIN_ROLE = "superAdmin";

    /**
     * 默认管理员用户名
     */
    public static final String DEFAULT_ADMIN_USER = "admin";

    /**
     * 空Url
     */
    public static final String EMPTY_URL = "#";

    /**
     * 中括号
     */
    public static final String BRACKET = "[]";

    /**
     * 本地IP
     */
    public static final String LOCAL_IP = "127.0.0.1";

    /**
     * unknown
     */
    public static final String UNKNOWN = "unknown";

    /**
     * 导入数据标题
     */
    public static Map<String, String> headerAlias = new HashMap<>();

    /**
     * 配置文件选择
     */
    public final static class CONFIG_CHOOSE{
        public static String DEV = "dev" ;      // 测试环境
        public static String PROD = "prod";     // 正式环境
    }

}

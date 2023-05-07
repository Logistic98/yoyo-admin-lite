package com.yoyo.admin.common.enums;

import lombok.Getter;

/**
 * 状态类型
 */
@Getter
public enum StatusTypeEnum implements CodeEnum {

    /**
     * 启用
     */
    ENABLED(0, "启用"),

    /**
     * 停用
     */
    DISABLED(1, "停用");

    private Integer code;

    private String message;

    StatusTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}

package com.yoyo.admin.common.enums;

import lombok.Getter;

/**
 * 操作状态
 */
@Getter
public enum OperStatusEnum implements CodeEnum {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),

    /**
     * 失败
     */
    FAIL(1, "失败");

    private Integer code;

    private String message;

    OperStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

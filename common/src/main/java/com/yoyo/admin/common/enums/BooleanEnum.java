package com.yoyo.admin.common.enums;

import lombok.Getter;

/**
 * 布尔类型
 */
@Getter
public enum BooleanEnum implements CodeEnum  {

    /**
     * 否
     */
    NO(0, "否"),

    /**
     * 是
     */
    YES(1, "是");

    private Integer code;

    private String message;

    BooleanEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}

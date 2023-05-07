package com.yoyo.admin.common.enums;

import lombok.Getter;

/**
 * 性别类别
 */
@Getter
public enum GenderTypeEnum implements CodeEnum {

    /**
     * 女
     */
    WOMAN(0, "女"),

    /**
     * 男
     */
    MAN(1, "男");

    private Integer code;

    private String message;

    GenderTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}

package com.yoyo.admin.common.enums;

import lombok.Getter;

/**
 * 排序类型
 */
@Getter
public enum SortTypeEnum implements CodeEnum {

    /**
     * 倒序
     */
    DESC(0, "倒序"),

    /**
     * 正序
     */
    ASC(1, "正序");

    private Integer code;

    private String message;

    SortTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}

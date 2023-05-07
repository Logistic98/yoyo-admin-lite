package com.yoyo.admin.common.enums;

import lombok.Getter;

/**
 * 显示类型
 */
@Getter
public enum VisibleTypeEnum implements CodeEnum {

    /**
     * 显示
     */
    VISIBLE(0, "显示"),

    /**
     * 隐藏
     */
    HIDE(1, "隐藏");

    private Integer code;

    private String message;

    VisibleTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}

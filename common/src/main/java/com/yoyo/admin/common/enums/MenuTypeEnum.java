package com.yoyo.admin.common.enums;

import lombok.Getter;

/**
 * 菜单类别
 */
@Getter
public enum MenuTypeEnum implements CodeEnum {

    /**
     * 主菜单
     */
    MAIN(0, "主菜单"),

    /**
     * 子菜单
     */
    CHILD(1, "子菜单"),

    /**
     * 按钮
     */
    BUTTON(2, "按钮");

    private Integer code;

    private String message;

    MenuTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}

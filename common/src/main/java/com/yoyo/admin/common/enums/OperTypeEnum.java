package com.yoyo.admin.common.enums;

import lombok.Getter;

/**
 * 操作类型
 */
@Getter
public enum OperTypeEnum implements CodeEnum {
    
    /**
     * 其它
     */
    OTHER(0, "其它"),

    /**
     * 新增
     */
    ADD(1, "新增"),

    /**
     * 修改
     */
    UPDATE(2, "修改"),

    /**
     * 删除
     */
    DELETE(3, "删除"),

    /**
     * 授权
     */
    GRANT(4, "授权"),

    /**
     * 导出
     */
    EXPORT(5, "导出"),

    /**
     * 导入
     */
    IMPORT(6, "导入"),

    /**
     * 强退
     */
    FORCE(7, "强退"),

    /**
     * 生成代码
     */
    GENCODE(8, "生成代码"),
    
    /**
     * 清空
     */
    CLEAN(9, "清空");

    private Integer code;

    private String message;

    OperTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}

package com.yoyo.admin.common.logger.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作类型
 */
public enum OperationType {
    Create(1, "新增"), Update(2, "更新"), Delete(3, "删除"),
    Login(4, "登录"), Logout(5, "登出");

    private final Integer id;
    private final String name;

    OperationType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonValue
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * 按id获取
     * @param id
     * @return
     */
    @JsonCreator
    public static OperationType getById(Integer id) {
        if (id == null) {
            return null;
        }
        for (OperationType item : OperationType.values()) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 获取列表
     * @return
     */
    public static List<Map<String, Object>> listAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OperationType item : OperationType.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("name", item.getName());
            list.add(map);
        }
        return list;
    }

    /**
     * 解析从body中提取的操作类型id参数为操作类型
     *
     * @param idObject    id对象
     * @param isNecessary 是否是必填，一般新建时为true，更新时为false
     * @return 操作类型
     * @throws RuntimeException 用于返回给前端的错误提示
     */
    public static OperationType parseBodyPlatformParam(Object idObject, boolean isNecessary) throws RuntimeException {
        if (idObject == null) {
            if (isNecessary) {
                throw new RuntimeException("未指定操作类型id");
            } else {
                return null;
            }
        }
        int id;
        try {
            id = Integer.parseInt(idObject.toString());
        } catch (Exception ex) {
            throw new RuntimeException("指定的操作类型id格式不正确");
        }
        OperationType operationType = getById(id);
        if (operationType == null) {
            throw new RuntimeException("指定的操作类型id不正确");
        }
        return operationType;
    }
}

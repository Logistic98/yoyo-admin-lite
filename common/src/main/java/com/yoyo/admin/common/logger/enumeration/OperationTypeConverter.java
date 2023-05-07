package com.yoyo.admin.common.logger.enumeration;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 操作类型转换
 */
@Converter
public class OperationTypeConverter implements AttributeConverter<OperationType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(OperationType enumItem) {
        // 这里要加上判空，不然实体类的相应字段为空时会报错
        if (enumItem == null) {
            return null;
        }
        return enumItem.getId();
    }

    @Override
    public OperationType convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return OperationType.getById(id);
    }
}

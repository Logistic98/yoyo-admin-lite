package com.yoyo.admin.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.yoyo.admin.common.utils.DateTimeUtils;

import java.io.IOException;
import java.util.Date;

/**
 * JSON日期转换器
 */
public class JsonDateConverter extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
        Date targetDate = null;
        String originDate = p.getText().trim();
        if (!originDate.isEmpty()) {
            targetDate = DateTimeUtils.autoParseDate(originDate);
            if (targetDate == null) {
                throw new InvalidFormatException(p, "日期格式错误", originDate, Date.class);
            }
        }
        return targetDate;
    }

    @Override
    public Class<?> handledType() {
        return Date.class;
    }
}

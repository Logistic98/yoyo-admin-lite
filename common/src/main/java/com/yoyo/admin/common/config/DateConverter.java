package com.yoyo.admin.common.config;

import com.yoyo.admin.common.utils.DateTimeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * 日期转换器
 */
@Configuration
public class DateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        return DateTimeUtils.autoParseDate(source);
    }

}

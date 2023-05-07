package com.yoyo.admin.redis_common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisEntity {

    //用于生成key的key模板，例：TBoxData_%s_%s，每个占位符（%s）用带有@RedisKey注解的字段的值填充
    String keyTemplate() default "";

    long expire() default 0;//过期时间，单位：秒

}

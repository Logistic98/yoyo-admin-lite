package com.yoyo.admin.minio_common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MinioServiceAspect {

    @Before(value="execution(* com.yoyo.admin.minio_common.service.MinioService.*(..))")
    public void beforeAdvice(JoinPoint joinPoint){
        System.out.println("MinioServiceAspect | Before MinioService method got called");
    }

    @After(value="execution(* com.yoyo.admin.minio_common.service.MinioService.*(..))")
    public void afterAdvice(JoinPoint joinPoint){
        System.out.println("MinioServiceAspect | After MinioService method got called");
    }

    @AfterReturning(value="execution(* com.yoyo.admin.minio_common.service.MinioService.*(..))")
    public void afterReturningAdvice(JoinPoint joinPoint){
        System.out.println("MinioServiceAspect | AfterReturning MinioService method got called");
    }
}

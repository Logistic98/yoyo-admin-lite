package com.yoyo.admin.web_manage.config;

import com.yoyo.admin.web_manage.config.security.WebAuthenticationHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        return new ResponseEntity<>(e.getBindingResult().getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity entityNotFoundException(EntityNotFoundException e) {
        // 打印堆栈信息
        return new ResponseEntity<>("{\"message\":\"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
    }

    /**
     * 访问异常
     * 用的是spring security的{@link AccessDeniedException}, 目前未发现对spring security的过滤器有什么影响
     * 可能与自定义配置{@link WebAuthenticationHandler}有关系
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity accessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>("{\"message\":\"" + e.getMessage() + "\"}", HttpStatus.FORBIDDEN);
    }
}

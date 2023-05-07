package com.yoyo.admin.web_manage.config;

import com.yoyo.admin.common.domain.User;
import com.yoyo.admin.common.service.UserService;
import com.yoyo.admin.common.utils.IpUtils;
import com.yoyo.admin.common.logger.service.OperationLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 操作日志切面
 */
@Aspect
@Component
public class OperationLogAspect {
    private OperationLogService operationLogService;
    private UserService userService;

    @Autowired
    public void setOperationLogService(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @After(value = "@annotation(com.yoyo.admin.common.logger.annotation.CreateLog) ||" +
            "@annotation(com.yoyo.admin.common.logger.annotation.UpdateLog) ||" +
            "@annotation(com.yoyo.admin.common.logger.annotation.DeleteLog)")
    public void afterMethod(JoinPoint joinPoint) {
        Long userId = null;
        String userName = null;
        String ipAddress = null;
        User user = userService.current();
        if (user != null) {
            userId = user.getId();
            userName = user.getName();
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        if (requestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            ipAddress = IpUtils.getRequestIpAddress(request);
        }
        operationLogService.create(joinPoint, userId, null, userName, ipAddress);
    }

}

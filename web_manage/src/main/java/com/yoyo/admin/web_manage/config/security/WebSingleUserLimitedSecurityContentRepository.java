package com.yoyo.admin.web_manage.config.security;

import com.yoyo.admin.common.config.AuthenticationToken;
import com.yoyo.admin.common.config.SessionHolder;
import com.yoyo.admin.common.domain.User;
import com.yoyo.admin.web_manage.config.HostConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * 是否限制单个用户登录的配置
 */
@Configuration
public class WebSingleUserLimitedSecurityContentRepository implements SecurityContextRepository {

    private HostConfig hostConfig;

    @Autowired
    public void setHostConfig(HostConfig hostConfig) {
        this.hostConfig = hostConfig;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        HttpSession session = request.getSession(true);
        SecurityContext context = null;
        boolean needCreateEmptyContext = true;
        Object object = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (object instanceof SecurityContext) {
            context = (SecurityContext) object;
            //如果需要校验用户sessionId，需要取出用户Id
            if (hostConfig.getUseSingleUserLimit()) {
                Authentication authentication = context.getAuthentication();
                if (authentication.isAuthenticated()) {
                    Object principal = authentication.getPrincipal();
                    if (principal instanceof User) {
                        User user = (User) principal;
                        String sessionId = SessionHolder.getWebSessionId(user.getId());
                        if (sessionId != null && sessionId.equals(session.getId())) {
                            needCreateEmptyContext = false;
                        }
                    }
                }
            } else {
                needCreateEmptyContext = false;
            }
        }
        //如果用户的认证信息已失效，重建一个未认证的context
        if (needCreateEmptyContext) {
            context = SecurityContextHolder.createEmptyContext();
            AuthenticationToken authenticationToken = new AuthenticationToken(new HashMap<>());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            context.setAuthentication(authenticationToken);
        }
        return context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return true;
    }


}

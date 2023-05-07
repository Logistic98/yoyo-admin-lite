package com.yoyo.admin.web_manage.config.security;

import com.yoyo.admin.common.config.AuthenticationToken;
import com.yoyo.admin.common.config.SessionHolder;
import com.yoyo.admin.common.utils.RequestBodyUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户验证过滤器
 */
public class UsernameAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    protected UsernameAuthenticationFilter() {
        super(new AntPathRequestMatcher("/auth/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        //获取用户名和密码
        Map<String, Object> body = RequestBodyUtils.getBodyMapFromRequest(httpServletRequest, SessionHolder.getWebAesKey());
        Map<String, String> principal = new HashMap<>();
        Object username = body.get("username");
        Object password = body.get("password");
        if (username != null && password != null) {
            principal.put("username", username.toString().trim());
            principal.put("password", password.toString().trim());
        }
        AuthenticationToken authenticationToken = new AuthenticationToken(principal);
        authenticationToken.setDetails(authenticationDetailsSource.buildDetails(httpServletRequest));
        return this.getAuthenticationManager().authenticate(authenticationToken);
    }
}

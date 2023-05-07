package com.yoyo.admin.web_manage.config.security;

import com.yoyo.admin.common.config.SessionHolder;
import com.yoyo.admin.common.domain.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 适用于典型web的安全验证处理器
 */
@Configuration
public class WebAuthenticationHandler implements AuthenticationSuccessHandler, LogoutSuccessHandler,
        AuthenticationFailureHandler, AuthenticationEntryPoint, AccessDeniedHandler {

    /**
     * 登入成功时的处理(AuthenticationSuccessHandler)
     * @param httpServletRequest
     * @param httpServletResponse
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        Object o = authentication.getPrincipal();
        if (o instanceof User) {
            User user = (User) o;
            SessionHolder.addWebSession(user.getId(), httpServletRequest.getSession().getId());
            HttpSession httpSession = httpServletRequest.getSession(true);
            httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        }
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        String s = "{\"message\":\"登录成功\"}";
        PrintWriter out = httpServletResponse.getWriter();
        out.flush();
        out.write(s, 0, s.length());
        out.flush();
        out.close();
    }

    /**
     * 登出成功时的处理(LogoutSuccessHandler)
     * @param httpServletRequest
     * @param httpServletResponse
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        Object o = authentication.getPrincipal();
        if (o instanceof User) {
            User user = (User) o;
            SessionHolder.removeWebSession(user.getId());
        }
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        PrintWriter out = httpServletResponse.getWriter();
        out.write("{\"message\":\"登出成功\"}");
        out.flush();
        out.close();
    }

    /**
     * 登录失败时的处理(AuthenticationFailureHandler)
     * @param httpServletRequest
     * @param httpServletResponse
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setStatus(400);
        String s = "{\"message\":\"" + e.getMessage() + "\"}";
        PrintWriter out = httpServletResponse.getWriter();
        out.write(s, 0, s.length());
        out.flush();
        out.close();
    }

    /**
     * 访问敏感资源无法验证身份时的处理 未鉴权(AuthenticationEntryPoint)
     * @param httpServletRequest
     * @param httpServletResponse
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setStatus(403);
    }

    /**
     * 访问敏感资源权限不足时的处理(AccessDeniedHandler)
     * @param httpServletRequest
     * @param httpServletResponse
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.setStatus(403);
    }

}

package com.yoyo.admin.web_manage.config.security;

import com.yoyo.admin.common.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 用户验证配置
 */
@Configuration
public class UsernameAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private UserService userService;
    private WebAuthenticationHandler webAuthenticationHandler;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setWebAuthenticationHandler(WebAuthenticationHandler webAuthenticationHandler) {
        this.webAuthenticationHandler = webAuthenticationHandler;
    }

    @Override
    public void configure(HttpSecurity builder){
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        UsernameAuthenticationFilter usernameAuthenticationFilter = new UsernameAuthenticationFilter();
        usernameAuthenticationFilter.setAuthenticationManager(authenticationManager);
        usernameAuthenticationFilter.setAuthenticationSuccessHandler(webAuthenticationHandler);
        usernameAuthenticationFilter.setAuthenticationFailureHandler(webAuthenticationHandler);
        UsernameAuthenticationProvider usernameAuthenticationProvider = new UsernameAuthenticationProvider(userService);
        builder
                .authenticationProvider(usernameAuthenticationProvider)
                .addFilterBefore(usernameAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}

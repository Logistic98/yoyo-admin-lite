package com.yoyo.admin.web_manage.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Web安全配置
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UsernameAuthenticationConfig usernameAuthenticationConfig;
    private WebAuthenticationHandler webAuthenticationHandler;
    private WebSingleUserLimitedSecurityContentRepository webSingleUserLimitedSecurityContentRepository;

    @Autowired
    public void setUsernameAuthenticationConfig(UsernameAuthenticationConfig usernameAuthenticationConfig) {
        this.usernameAuthenticationConfig = usernameAuthenticationConfig;
    }

    @Autowired
    public void setWebAuthenticationHandler(WebAuthenticationHandler webAuthenticationHandler) {
        this.webAuthenticationHandler = webAuthenticationHandler;
    }

    @Autowired
    public void setWebSingleUserLimitedSecurityContentRepository(WebSingleUserLimitedSecurityContentRepository webSingleUserLimitedSecurityContentRepository) {
        this.webSingleUserLimitedSecurityContentRepository = webSingleUserLimitedSecurityContentRepository;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .headers()
                //允许嵌入iframe
                .frameOptions()
                .disable()
                .and()
                .csrf()
                .disable()
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .securityContext()
                //自定义授权方式
                .securityContextRepository(webSingleUserLimitedSecurityContentRepository)
                .and()
                .authorizeRequests()
                .antMatchers("/api/**")
                .authenticated()
                .anyRequest()
                .permitAll()
                .and()
                // 使用此套配置执行认证
                .apply(usernameAuthenticationConfig)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(webAuthenticationHandler)
                .accessDeniedHandler(webAuthenticationHandler)
                .and()
                .logout()
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler(webAuthenticationHandler);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/druid/**")
                .antMatchers("/doc.html**")
                .antMatchers("/webjars/**")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/v2/**")
                .antMatchers("/supports/**")
                .antMatchers("/uploads/**");
    }

}

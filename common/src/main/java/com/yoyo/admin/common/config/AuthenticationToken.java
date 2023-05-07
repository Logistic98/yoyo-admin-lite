package com.yoyo.admin.common.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Token验证
 */
public class AuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    /**
     * 构建未经认证的token
     * @param principal
     */
    public AuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
        //必须用父类的方法
        super.setAuthenticated(false);
    }

    /**
     * 构建已经过认证的token
     * @param principal
     * @param authorities
     */
    public AuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        //必须用父类的方法
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }

}

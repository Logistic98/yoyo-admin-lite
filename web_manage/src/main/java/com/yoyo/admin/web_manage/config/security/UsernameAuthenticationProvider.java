package com.yoyo.admin.web_manage.config.security;

import cn.hutool.core.util.ObjectUtil;
import com.yoyo.admin.common.config.AuthenticationToken;
import com.yoyo.admin.common.domain.User;
import com.yoyo.admin.common.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统登录
 */
public class UsernameAuthenticationProvider implements AuthenticationProvider {
    private Map<Object, Object> loginErrorMap = new HashMap<Object, Object>();

    /**
     * 锁定时间 10分钟
     */
    private Long lockTime = 10 * 60 * 1000L;

    /**
     * 登录时间间隔 5分钟
     */
    private Long loginIntervalTime = 5 * 60 * 1000L;

    private final UserService userService;

    public UsernameAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            return authentication;
        }
        HashMap<?, ?> principal = (HashMap<?, ?>) authentication.getPrincipal();
        Object username = principal.get("username");
        Object password = principal.get("password");

        if (isLocked(username)) {
            throw new UsernameNotFoundException("该账号已被锁定10分钟，请稍后再试");
        }
        if (username == null || password == null || username.toString().isEmpty() || password.toString().isEmpty()) {
            int errorCount = addLoginErrorInfo(username);
            if (errorCount == 3) {
                throw new UsernameNotFoundException("密码错误，该账号已被锁定，请10分钟后再试");
            } else {
                throw new UsernameNotFoundException("用户名或密码不正确");
            }
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User userDetails = this.userService.loadUserByUsername(username.toString());
        if (userDetails == null || !encoder.matches(password.toString(), userDetails.getPassword())) {
            int errorCount = addLoginErrorInfo(username);
            if (errorCount == 3) {
                throw new UsernameNotFoundException("密码错误，该账号已被锁定，请10分钟后再试");
            } else {
                throw new UsernameNotFoundException("用户名或密码不正确");
            }
        } else {
            loginErrorMap.remove(username);
        }
        AuthenticationToken authenticationToken = new AuthenticationToken(userDetails, userService.getAuthorities(userDetails));
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    /**
     * 保存登录错误信息
     * @param username
     * @return
     */
    private int addLoginErrorInfo(Object username) {
        int count = 0;
        if (ObjectUtil.isNotEmpty(username)) {
            int loginCount = 1;
            if (loginErrorMap.containsKey(username)) {
                Map<Object, Object> userInfoMap = (Map<Object, Object>) loginErrorMap.get(username);
                loginCount = Integer.parseInt(userInfoMap.get("loginCount").toString());
                long loginTime = Long.parseLong(userInfoMap.get("loginTime").toString());
                //判断是否是5分钟内的登录错误
                if ((System.currentTimeMillis() - loginTime) <= loginIntervalTime) {
                    userInfoMap.put("loginCount", ++loginCount);
                    if (loginCount == 3) {
                        //账号锁定开始时间
                        userInfoMap.put("loginTime", System.currentTimeMillis());
                    }
                } else {
                    //超过5分钟则清除登录错误信息
                    loginErrorMap.remove(username);
                }
            } else {
                Map<String, Object> userInfoMap = new HashMap<>();
                userInfoMap.put("loginTime", System.currentTimeMillis());
                userInfoMap.put("loginCount", loginCount);

                loginErrorMap.put(username, userInfoMap);
            }
            count = loginCount;
        }

        return count;
    }

    /**
     * 判断账号是否处于锁定状态
     * @param username
     * @return
     */
    private boolean isLocked(Object username) {
        boolean locked = false;
        if (username == null) {
            return false;
        }
        if (loginErrorMap.containsKey(username)) {
            Map<?, ?> userInfoMap = (Map<?, ?>) loginErrorMap.get(username);
            if (Integer.parseInt(userInfoMap.get("loginCount").toString()) == 3) {
                if (System.currentTimeMillis() - Long.parseLong(userInfoMap.get("loginTime").toString()) <= lockTime) {
                    locked = true;
                } else {
                    //超过10分钟解锁账号
                    loginErrorMap.remove(username);
                }
            }
        }
        return locked;
    }

}

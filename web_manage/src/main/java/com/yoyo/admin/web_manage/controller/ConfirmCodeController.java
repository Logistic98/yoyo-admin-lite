package com.yoyo.admin.web_manage.controller;

import cn.hutool.core.util.StrUtil;
import com.yoyo.admin.common.domain.ConfirmCodeMessage;
import com.yoyo.admin.common.domain.User;
import com.yoyo.admin.common.service.AliConfirmCodeMessageService;
import com.yoyo.admin.common.service.UserService;
import com.yoyo.admin.common.utils.ResultDataUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@Api(tags = "验证码发送")
@RestController
@RequestMapping("/auth")
public class ConfirmCodeController {

    private AliConfirmCodeMessageService aliConfirmCodeMessageService;

    @Autowired
    public void setAliConfirmCodeMessageService(AliConfirmCodeMessageService aliConfirmCodeMessageService) {
        this.aliConfirmCodeMessageService = aliConfirmCodeMessageService;
    }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @ApiOperation("注册发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phoneNumber", value = "手机号码", dataType = "String", paramType = "query"),
    })
    @PostMapping(value = "/register/confirmCode")
    public ResponseEntity<?> sendConfirmCodeForRegister(@RequestParam String phoneNumber) {
        if (StrUtil.isEmpty(phoneNumber)) {
            return ResultDataUtils.error("请输入手机号码");
        }
        User user = userService.getUserByPhoneNumber(phoneNumber);
        if (user != null) {
            return ResultDataUtils.error("手机号码已注册");
        }
        try {
            ConfirmCodeMessage confirmCodeMessage = aliConfirmCodeMessageService.createForRegister(phoneNumber);
            Map<String, Long> map = new HashMap<>();
            map.put("sendTime", confirmCodeMessage.getCreateTime().getTime());
            return ResultDataUtils.success(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("登录发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phoneNumber", value = "手机号码", dataType = "String", paramType = "query"),
    })
    @PostMapping(value = "/login/confirmCode")
    public ResponseEntity<?> sendConfirmCodeForLogin(@RequestParam String phoneNumber) {
        if (StrUtil.isEmpty(phoneNumber)) {
            return ResultDataUtils.error("请输入手机号码");
        }
        User user = userService.getUserByPhoneNumber(phoneNumber);
        if (user == null) {
            return ResultDataUtils.error("手机号码未注册");
        }
        try {
            ConfirmCodeMessage confirmCodeMessage = aliConfirmCodeMessageService.createForLogin(phoneNumber);
            Map<String, Long> map = new HashMap<>();
            map.put("sendTime", confirmCodeMessage.getCreateTime().getTime());
            return ResultDataUtils.success(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("修改密码发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phoneNumber", value = "手机号码", dataType = "String", paramType = "query"),
    })
    @PostMapping(value = "/changePassword/confirmCode")
    public ResponseEntity<?> sendConfirmCodeForChangePassword(@RequestParam String phoneNumber) {
        if (StrUtil.isEmpty(phoneNumber)) {
            return ResultDataUtils.error("请输入手机号码");
        }
        User user = userService.getUserByPhoneNumber(phoneNumber);
        if (user == null) {
            return ResultDataUtils.error("手机号码未注册");
        }
        try {
            ConfirmCodeMessage confirmCodeMessage = aliConfirmCodeMessageService.createForChangePassword(phoneNumber);
            Map<String, Long> map = new HashMap<>();
            map.put("sendTime", confirmCodeMessage.getCreateTime().getTime());
            return ResultDataUtils.success(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResultDataUtils.error(ex.getMessage());
        }
    }

}

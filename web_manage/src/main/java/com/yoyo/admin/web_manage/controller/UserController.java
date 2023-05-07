package com.yoyo.admin.web_manage.controller;

import com.yoyo.admin.common.config.SessionHolder;
import com.yoyo.admin.common.constant.RegexpConstants;
import com.yoyo.admin.common.domain.Dept;
import com.yoyo.admin.common.domain.Role;
import com.yoyo.admin.common.domain.User;
import com.yoyo.admin.common.service.DeptService;
import com.yoyo.admin.common.service.RoleService;
import com.yoyo.admin.common.service.UserService;
import com.yoyo.admin.common.service.WebPermissionService;
import com.yoyo.admin.common.utils.RequestBodyUtils;
import com.yoyo.admin.common.utils.ResultDataUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Api(tags = "管理用户")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/user")
public class UserController {

    private UserService userService;
    private DeptService deptService;
    private WebPermissionService webPermissionService;
    private RoleService roleService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setDeptService(DeptService deptService) {
        this.deptService = deptService;
    }

    @Autowired
    public void setWebPermissionService(WebPermissionService webPermissionService) {
        this.webPermissionService = webPermissionService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @ApiOperation("分页获取用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户姓名/手机号码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "deptId", value = "责任部门id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "第几页", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", paramType = "query"),
    })
    @GetMapping(value = "/page")
    @PreAuthorize("hasAuthority('user_list')")
    public ResponseEntity<?> page(@RequestParam(defaultValue = "") String name,
                                  @RequestParam(defaultValue = "") Long deptId,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultDataUtils.success(userService.pageUsers(name, deptId, page, pageSize, webPermissionService.getDataScopeDept()));
    }

    @ApiOperation("根据角色id获取用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "Long", paramType = "query"),
    })
    @GetMapping(value = "/list")
    @PreAuthorize("hasAuthority('user_list')")
    public ResponseEntity<?> list(@RequestParam(defaultValue = "") Long roleId) {
        return ResultDataUtils.success(userService.listUsers(roleId));
    }

    @ApiOperation("获取用户详情")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/get/{id:\\d+}")
    @PreAuthorize("hasAuthority('user_detail')")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResultDataUtils.success(userService.getUser(id));
    }

    @ApiOperation("获取当前用户")
    @GetMapping(value = "/current")
    public ResponseEntity<?> current() {
        return ResultDataUtils.success(userService.currentUser());
    }

    @ApiOperation("获取当前用户菜单列表")
    @GetMapping(value = "/current/menus")
    public ResponseEntity<?> currentMenu() {
        try {
            return ResultDataUtils.success(userService.currentMenus());
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation(value = "修改密码", notes = "修改密码（需要对参数使用AES加密再进行传输，整个加密放到body里）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPassword", value = "原密码", required = true, dataType = "String", paramType = "body"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, dataType = "String", paramType = "body")
    })
    @PostMapping(value = "/password/update")
    public ResponseEntity<?> editPassword(HttpServletRequest request) {
        Map<String, Object> body = RequestBodyUtils.getBodyMapFromRequest(request, SessionHolder.getWebAesKey());
        try {
            String oldPassword = "";
            String newPassword = "";
            if (!body.containsKey("oldPassword")) {
                return ResultDataUtils.error("旧密码未填写");
            }
            if (!body.containsKey("newPassword")) {
                return ResultDataUtils.error("新密码未填写");
            }
            oldPassword = body.get("oldPassword").toString();
            newPassword = body.get("newPassword").toString();

            // 该方法接受一个正则表达式作为它的第一个参数。
            Pattern p = Pattern.compile(RegexpConstants.REGEX_PASSWORD);
            // 对输入str进行解释和匹配操作
            Matcher m = p.matcher(newPassword);
            if (!m.matches()) {
                return ResultDataUtils.error("密码过于简单");
            }

            userService.updatePassword(oldPassword, newPassword);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation(value = "增加用户", notes = "增加用户（需要对参数使用AES加密再进行传输）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "姓名", required = true, dataType = "String", paramType = "body"),
            @ApiImplicitParam(name = "gender", value = "性别", required = true, dataType = "Integer", paramType = "body"),
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "body"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "body"),
            @ApiImplicitParam(name = "phoneNumber", value = "手机号码", required = true, dataType = "String", paramType = "body"),
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true, dataType = "Long", paramType = "body"),
            @ApiImplicitParam(name = "deptId", value = "部门id", required = true, dataType = "Long", paramType = "body")
    })
    @PostMapping(value = "/add")
    @PreAuthorize("hasAuthority('user_add')")
    public ResponseEntity<?> add(HttpServletRequest request) {
        Map<String, Object> body = RequestBodyUtils.getBodyMapFromRequest(request, SessionHolder.getWebAesKey());
        if (body.size() == 0) {
            return ResultDataUtils.error("提供的用户信息不正确");
        }
        try {
            User user = new User();
            if (!body.containsKey("name")) {
                return ResultDataUtils.error("姓名未填写");
            }
            if (!body.containsKey("gender")) {
                return ResultDataUtils.error("性别未填写");
            }
            if (!body.containsKey("username")) {
                return ResultDataUtils.error("用户名未填写");
            }
            if (!body.containsKey("password")) {
                return ResultDataUtils.error("密码未填写");
            }
            if (!body.containsKey("phoneNumber")) {
                return ResultDataUtils.error("手机号码未填写");
            }
            if (!body.containsKey("roleId")) {
                return ResultDataUtils.error("角色未填写");
            }
            if (!body.containsKey("deptId")) {
                return ResultDataUtils.error("所属部门未填写");
            }
            user.setName(body.get("name").toString());
            user.setGender(Integer.parseInt(body.get("gender").toString()));
            user.setUsername(body.get("username").toString());
            user.setPassword(body.get("password").toString());
            user.setPhoneNumber(body.get("phoneNumber").toString());
            Role role = roleService.getRole(Long.valueOf(body.get("roleId").toString()));
            user.setRole(role);
            Dept dept = deptService.getDept(Long.valueOf(body.get("deptId").toString()));
            user.setDept(dept);
            userService.addUser(user);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation(value="修改用户", notes= "修改用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long", paramType = "body"),
            @ApiImplicitParam(name = "name", value = "姓名", required = true, dataType = "String", paramType = "body"),
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "body"),
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true, dataType = "Long", paramType = "body"),
            @ApiImplicitParam(name = "deptId", value = "部门id", required = true, dataType = "Long", paramType = "body"),
    })
    @PostMapping(value = "/update")
    @PreAuthorize("hasAuthority('user_update')")
    public ResponseEntity<?> update(@RequestBody @ApiIgnore Map<String, Object> body) {
        try {
            userService.updateUser(body);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("删除用户")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('user_delete')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            User user = userService.getUser(id);
            userService.deleteUser(user);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("重置密码")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long", paramType = "query")
    @PostMapping(value = "/password/reset")
    @PreAuthorize("hasAuthority('user_reset')")
    public ResponseEntity<?> editPassword(@RequestParam Long id) {
        try {
            return ResultDataUtils.success(userService.resetPassword(id));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

}

package com.yoyo.admin.web_manage.controller;

import com.yoyo.admin.common.domain.Role;
import com.yoyo.admin.common.service.MenuService;
import com.yoyo.admin.common.service.RoleService;
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

@Api(tags = "管理角色")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/role")
public class RoleController {

    private RoleService roleService;

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    private MenuService menuService;

    @Autowired
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    @ApiOperation("获取角色分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleName", value = "角色名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "第几页", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", paramType = "query"),
    })
    @GetMapping(value = "/page")
    @PreAuthorize("hasAuthority('role_list')")
    public ResponseEntity<?> page(@RequestParam(defaultValue = "") String roleName,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            return ResultDataUtils.success(roleService.pageRoles(roleName, page, pageSize));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("获取所有未删除的角色列表")
    @GetMapping(value = "/list")
    @PreAuthorize("hasAuthority('role_list')")
    public ResponseEntity<?> list(){
        try {
            return ResultDataUtils.success(roleService.listRoles());
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation(value = "获取角色详情",notes = "获取角色详情（获取指定角色的菜单列表也使用此接口）")
    @ApiImplicitParam(name = "id", value = "角色id", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/get/{id}")
    @PreAuthorize("hasAuthority('role_detail')")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            return ResultDataUtils.success(roleService.getRole(id));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("增加角色")
    @ApiImplicitParam(name = "role", value = "角色对象", required = true, dataType = "Role", paramType = "body")
    @PostMapping(value = "/add")
    @PreAuthorize("hasAuthority('role_add')")
    public ResponseEntity<?> add(@RequestBody Role role) {
        try {
            return ResultDataUtils.success(roleService.addRole(role));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("修改角色")
    @ApiImplicitParam(name = "role", value = "角色对象", required = true, dataType = "Role", paramType = "body")
    @PostMapping(value = "/update")
    @PreAuthorize("hasAuthority('role_update')")
    public ResponseEntity<?> update(@RequestBody Role role) {
        try {
            return ResultDataUtils.success(roleService.updateRole(role));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("删除角色")
    @ApiImplicitParam(name = "id", value = "角色id", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('role_delete')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Role role = roleService.getRole(id);
            roleService.deleteRole(role);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("获取指定角色的所有权限列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true, dataType = "Long", paramType = "path"),
    })
    @GetMapping(value = "/getRoleAuthorities/{roleId}")
    public ResponseEntity<?> getRoleAuthorities(@PathVariable Long roleId) {
        try {
            return ResultDataUtils.success(menuService.getRoleAuthorities(roleId));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

}

package com.yoyo.admin.web_manage.controller;

import com.yoyo.admin.common.domain.Menu;
import com.yoyo.admin.common.enums.MenuTypeEnum;
import com.yoyo.admin.common.enums.VisibleTypeEnum;
import com.yoyo.admin.common.service.MenuService;
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

@Api(tags = "管理菜单")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/menu")
public class MenuController {

    private MenuService menuService;

    @Autowired
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    @ApiOperation("获取菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "菜单名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "菜单类型 0:主菜单 1:子菜单 2:按钮", dataType = "MenuTypeEnum", paramType = "query"),
            @ApiImplicitParam(name = "visible", value = "是否显示", dataType = "VisibleType", paramType = "query"),
    })
    @GetMapping(value = "/list")
    @PreAuthorize("hasAuthority('menu_list')")
    public ResponseEntity<?> list(@RequestParam(defaultValue = "") String name,
                                  @RequestParam(defaultValue = "") MenuTypeEnum type,
                                  @RequestParam(defaultValue = "") VisibleTypeEnum visible) {
        try {
            return ResultDataUtils.success(menuService.listMenus(name, type, visible));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("获取菜单详情")
    @ApiImplicitParam(name = "id", value = "菜单id", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/get/{id}")
    @PreAuthorize("hasAuthority('menu_detail')")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            return ResultDataUtils.success(menuService.getMenu(id));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("增加菜单")
    @ApiImplicitParam(name = "menu", value = "菜单对象", required = true, dataType = "Menu", paramType = "body")
    @PostMapping(value = "/add")
    @PreAuthorize("hasAuthority('menu_add')")
    public ResponseEntity<?> add(@RequestBody Menu menu) {
        try {
            return ResultDataUtils.success(menuService.addMenu(menu));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("修改菜单")
    @ApiImplicitParam(name = "menu", value = "菜单对象", required = true, dataType = "Menu", paramType = "body")
    @PostMapping(value = "/update")
    @PreAuthorize("hasAuthority('menu_update')")
    public ResponseEntity<?> update(@RequestBody Menu menu) {
        try {
            return ResultDataUtils.success(menuService.updateMenu(menu));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("删除菜单")
    @ApiImplicitParam(name = "id", value = "菜单id", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('menu_delete')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            menuService.deleteMenu(id);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

}

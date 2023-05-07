package com.yoyo.admin.web_manage.controller;

import com.yoyo.admin.common.domain.Dept;
import com.yoyo.admin.common.service.DeptService;
import com.yoyo.admin.common.service.WebPermissionService;
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


@Api(tags = "管理部门")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/dept")
public class DeptController {

    private DeptService deptService;
    private WebPermissionService webPermissionService;

    @Autowired
    public void setDeptService(DeptService deptService) {
        this.deptService = deptService;
    }

    @Autowired
    public void setWebPermissionService(WebPermissionService webPermissionService) {
        this.webPermissionService = webPermissionService;
    }

    @ApiOperation("获取部门列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "部门名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "部门状态 0停用，1启用", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "right", value = "权限控制 0不控制, 1控制", dataType = "Integer", paramType = "query"),
    })
    @GetMapping(value = "/list")
    @PreAuthorize("hasAuthority('dept_list')")
    public ResponseEntity<?> listAll(@RequestParam(defaultValue = "") String name,
                                     @RequestParam(defaultValue = "") Integer status,
                                     @RequestParam(defaultValue = "1") Integer right) {
        try {
            if (right == null || right == 1) {
                return ResultDataUtils.success(deptService.listDepts(name, status, webPermissionService.getDataScopeDept()));
            } else {
                return ResultDataUtils.success(deptService.listDepts(name, status, null));
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("获取责任部门详情")
    @ApiImplicitParam(name = "id", value = "责任部门id", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/get/{id}")
    @PreAuthorize("hasAuthority('dept_detail')")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            return ResultDataUtils.success(deptService.getDept(id));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }


    @ApiOperation("增加责任部门")
    @ApiImplicitParam(name = "dept", value = "责任部门对象", required = true, dataType = "Dept", paramType = "body")
    @PostMapping(value = "/add")
    @PreAuthorize("hasAuthority('dept_add')")
    public ResponseEntity<?> add(@RequestBody Dept dept) {
        try {
            return ResultDataUtils.success(deptService.addDept(dept));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("修改责任部门")
    @ApiImplicitParam(name = "dept", value = "责任部门对象", required = true, dataType = "Dept", paramType = "body")
    @PostMapping(value = "/update")
    @PreAuthorize("hasAuthority('dept_update')")
    public ResponseEntity<?> update(@RequestBody Dept dept) {
        try {
            return ResultDataUtils.success(deptService.updateDept(dept));
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("启用责任部门")
    @ApiImplicitParam(name = "id", value = "责任部门id", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/enable/{id}")
    @PreAuthorize("hasAuthority('dept_enable')")
    public ResponseEntity<?> enable(@PathVariable Long id) {
        try {
            deptService.enableDept(id);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("禁用责任部门")
    @ApiImplicitParam(name = "id", value = "责任部门id", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/disable/{id}")
    @PreAuthorize("hasAuthority('dept_disable')")
    public ResponseEntity<?> disable(@PathVariable Long id) {
        try {
            deptService.disableDept(id);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("删除责任部门")
    @ApiImplicitParam(name = "id", value = "责任部门id", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('dept_delete')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            deptService.deleteDept(id);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

}

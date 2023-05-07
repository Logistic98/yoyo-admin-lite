package com.yoyo.admin.web_manage.controller;

import com.yoyo.admin.common.logger.enumeration.OperationType;
import com.yoyo.admin.common.logger.service.OperationLogService;
import com.yoyo.admin.common.utils.DateTimeUtils;
import com.yoyo.admin.common.utils.ResultDataUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "操作日志")
@RestController
@RequestMapping(value = "/api/logs")
public class OperationLogController {

    private OperationLogService operationLogService;
    private static final List<String> _terminalNames = new ArrayList<>();

    static {
        _terminalNames.add("Web");
        _terminalNames.add("小程序");
    }

    @Autowired
    public void setOperationLogService(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @ApiOperation("操作日志详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "日志id", dataType = "Long", paramType = "path"),
    })
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('log_detail')")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResultDataUtils.success(operationLogService.get(id));
    }

    @ApiOperation("获取操作日志所使用的所有操作目标")
    @GetMapping(value = "/targets")
    @PreAuthorize("hasAuthority('log_list')")
    public ResponseEntity<?> getTargets() {
        return ResultDataUtils.success(operationLogService.listTarget(_terminalNames));
    }

    @ApiOperation("操作日志分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "operationTypeId", value = "操作类型id(1.新增/2.更新/3.删除)", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "terminalName", value = "终端名称（Web/小程序/公众号）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "longUserId", value = "用户id(web用户的id)", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "stringUserId", value = "用户id(微信小程序或公众号的用户id)", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ipAddress", value = "IP地址", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "target", value = "操作目标", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "operation", value = "具体操作", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "操作对象", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "第几页", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", paramType = "query"),
    })
    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('log_list')")
    public ResponseEntity<?> list(@RequestParam(required = false) Integer operationTypeId,
                                  @RequestParam(required = false) String terminalName,
                                  @RequestParam(required = false) Long longUserId,
                                  @RequestParam(required = false) String stringUserId,
                                  @RequestParam(required = false) String userName,
                                  @RequestParam(required = false) String ipAddress,
                                  @RequestParam(required = false) String target,
                                  @RequestParam(required = false) String operation,
                                  @RequestParam(required = false) Date beginTime,
                                  @RequestParam(required = false) Date endTime,
                                  @RequestParam(required = false) String remark,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "20") Integer pageSize) {
        OperationType operationType = null;
        if (operationTypeId != null) {
            operationType = OperationType.getById(operationTypeId);
            if (operationType == null) {
                return ResultDataUtils.error("指定的操作类型id不正确");
            }
        }
        if (endTime != null) {
            endTime = DateTimeUtils.dayEnd(endTime);
        }
        if (beginTime != null) {
            beginTime = DateTimeUtils.dayBegin(beginTime);
        }
        if (terminalName != null && !terminalName.isEmpty()) {
            List<String> list = new ArrayList<>();
            list.add(terminalName);
            return ResultDataUtils.success(operationLogService.list(operationType, list, longUserId, stringUserId, userName, ipAddress, target, operation, beginTime, endTime, remark, page, pageSize));
        } else {
            return ResultDataUtils.success(operationLogService.list(operationType, _terminalNames, longUserId, stringUserId, userName, ipAddress, target, operation, beginTime, endTime, remark, page, pageSize));
        }
    }

}

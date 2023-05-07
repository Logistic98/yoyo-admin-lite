package com.yoyo.admin.web_manage.controller;

import com.yoyo.admin.common.domain.Area;
import com.yoyo.admin.common.service.AreaService;
import com.yoyo.admin.common.utils.ResultDataUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "行政区域管理")
@RestController
@RequestMapping(value = "/api/web/area")
public class AreaController {

    private AreaService areaService;

    @Autowired
    public void setAreaService(AreaService areaService) {
        this.areaService = areaService;
    }

    @ApiOperation("获取指定级别以上所有地区的树形列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "maxGrade", value = "行政级别（1:省级，2:地市级，3:区县极，4:街道级，5:社区级）", dataType = "Integer", paramType = "query")
    })
    @GetMapping(value = "/getAreaTree")
    public ResponseEntity<?> getAreaTree(@RequestParam(defaultValue = "3") Integer maxGrade) {
        return ResultDataUtils.success(areaService.tree(maxGrade));
    }

    @ApiOperation("获取指定级别的地区列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "maxGrade", value = "行政级别（1:省级，2:地市级，3:区县极，4:街道级，5:社区级）", dataType = "Integer", paramType = "query")
    })
    @GetMapping(value = "/getAreaList")
    public ResponseEntity<?> getAreaList(@RequestParam(defaultValue = "1") Integer maxGrade) {
        return ResultDataUtils.success(areaService.list(maxGrade));
    }

    @ApiOperation("获取指定地区列表的所有下级地区的列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "areas", value = "地区列表", dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "maxGrade", value = "行政级别（1:省级，2:地市级，3:区县极，4:街道级，5:社区级）", dataType = "Integer", paramType = "query")
    })
    @PostMapping(value = "/getAreasWithChildren")
    public ResponseEntity<?> getAreasWithChildren(@RequestBody List<Area> areas,
                                                  @RequestParam(defaultValue = "1") Integer maxGrade) {
        return ResultDataUtils.success(areaService.getAreasWithChildren(areas, maxGrade));
    }

}

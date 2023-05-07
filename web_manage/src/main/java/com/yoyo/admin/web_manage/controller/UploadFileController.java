package com.yoyo.admin.web_manage.controller;

import com.yoyo.admin.common.domain.UploadFile;
import com.yoyo.admin.common.service.UploadFileService;
import com.yoyo.admin.common.utils.ResultDataUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "通用文件上传")
@RestController
@RequestMapping("/api/uploads")
public class UploadFileController {
    private UploadFileService uploadFileService;

    @Autowired
    public void setUploadFileService(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    @ApiOperation("上传图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件列表", dataType = "List", paramType = "form"),
    })
    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public ResponseEntity<?> uploadImages(@RequestParam List<MultipartFile> file) {
        try {
            List<UploadFile> fileList = uploadFileService.uploadFiles(file, "IMAGE");
            return ResultDataUtils.success(fileList);
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件列表", dataType = "List", paramType = "form"),
    })
    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public ResponseEntity<?> uploadFiles(@RequestParam List<MultipartFile> file) {
        try {
            List<UploadFile> fileList = uploadFileService.uploadFiles(file, "FILE");
            return ResultDataUtils.success(fileList);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("上传音频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件列表", dataType = "List", paramType = "form"),
    })
    @RequestMapping(value = "/audio", method = RequestMethod.POST)
    public ResponseEntity<?> uploadAudios(@RequestParam List<MultipartFile> file) {
        try {
            List<UploadFile> fileList = uploadFileService.uploadFiles(file, "AUDIO");
            return ResultDataUtils.success(fileList);
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("上传视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件列表", dataType = "List", paramType = "form"),
    })
    @RequestMapping(value = "/video", method = RequestMethod.POST)
    public ResponseEntity<?> uploadVideos(@RequestParam List<MultipartFile> file) {
        try {
            List<UploadFile> fileList = uploadFileService.uploadFiles(file, "VIDEO");
            return ResultDataUtils.success(fileList);
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }
}

package com.yoyo.admin.web_manage.controller;

import com.yoyo.admin.common.utils.ResultDataUtils;
import com.yoyo.admin.minio_common.service.MinioService;
import com.yoyo.admin.minio_common.util.FileTypeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "MinIO对象存储")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/web/minio")
public class MinioController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioController.class);

    private final MinioService minioService;

    @Value("${minio.endpoint}")
    private String minioBaseUrl;

    @ApiOperation("上传文件对象到指定存储桶")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件", dataType = "file", paramType = "form"),
    })
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ResponseEntity<?> uploadFile(MultipartFile file, String bucketName) {
        try {
            LOGGER.info("MinioController | uploadFile is called");
            LOGGER.info("MinioController | uploadFile | bucketName : " + bucketName);
            String fileType = FileTypeUtils.getFileType(file);
            LOGGER.info("MinioController | uploadFile | fileType : " + fileType);
            if (fileType != null) {
                minioService.putObject(file, bucketName, fileType);
            }
            return ResultDataUtils.success("Upload successfully");
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("创建存储桶")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bucketName", value = "存储桶名称", dataType = "String", paramType = "path"),
    })
    @RequestMapping(value = "/addBucket/{bucketName}", method = RequestMethod.POST)
    public ResponseEntity<?> addBucket(@PathVariable String bucketName) {
        try {
            LOGGER.info("MinioController | addBucket is called");
            LOGGER.info("MinioController | addBucket | bucketName : " + bucketName);
            minioService.makeBucket(bucketName);
            return ResultDataUtils.success("Bucket name "+ bucketName +" created");
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("列出指定存储桶的所有文件对象")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bucketName", value = "存储桶名称", dataType = "String", paramType = "path"),
    })
    @RequestMapping(value = "/listObjectName/{bucketName}", method = RequestMethod.GET)
    public ResponseEntity<?> listObjectName(@PathVariable String bucketName) {
        LOGGER.info("MinioController | listObjectName is called");
        LOGGER.info("MinioController | listObjectName | bucketName : " + bucketName);
        return ResultDataUtils.success(minioService.listObjectName(bucketName));
    }

    @ApiOperation("列出所有存储桶名称")
    @RequestMapping(value = "/listBucketName", method = RequestMethod.GET)
    public ResponseEntity<?> listBucketName() {
        LOGGER.info("MinioController | listBucketName is called");
        return ResultDataUtils.success(minioService.listBucketName());
    }

    @ApiOperation("根据名称删除存储桶")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bucketName", value = "存储桶名称", dataType = "String", paramType = "path"),
    })
    @RequestMapping(value = "/removeBucket/{bucketName}", method = RequestMethod.GET)
    public ResponseEntity<?> removeBucket(@PathVariable String bucketName) {
        LOGGER.info("MinioController | removeBucket is called");
        LOGGER.info("MinioController | removeBucket | bucketName : " + bucketName);
        boolean state =  minioService.removeBucket(bucketName);
        LOGGER.info("MinioController | removeBucket | state : " + state);
        if(state){
            return ResultDataUtils.success("Delete bucket successfully");
        }else{
            return ResultDataUtils.error("Delete bucket failed");
        }
    }

    @ApiOperation("从指定存储桶删除文件对象")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bucketName", value = "存储桶名称", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "objectName", value = "文件对象名称", dataType = "String", paramType = "path"),
    })
    @RequestMapping(value = "/removeObject/{bucketName}/{objectName}", method = RequestMethod.GET)
    public ResponseEntity<?> removeObject(@PathVariable("bucketName") String bucketName,
                            @PathVariable("objectName") String objectName) {
        LOGGER.info("MinioController | removeObject is called");
        LOGGER.info("MinioController | removeObject | bucketName : " + bucketName);
        LOGGER.info("MinioController | removeObject | objectName : " + objectName);
        boolean state =  minioService.removeObject(bucketName, objectName);
        LOGGER.info("MinioController | removeObject | state : " + state);
        if(state){
            return ResultDataUtils.success("Delete object successfully");
        }else{
            return ResultDataUtils.error("Delete object failed");
        }
    }

    @ApiOperation("从指定存储桶批量删除文件对象")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bucketName", value = "存储桶名称", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "objectNameList", value = "文件对象列表", dataType = "List", paramType = "body"),
    })
    @RequestMapping(value = "/removeListObject/{bucketName}", method = RequestMethod.GET)
    public ResponseEntity<?> removeListObject(@PathVariable("bucketName") String bucketName,
                                   @RequestBody List<String> objectNameList) {
        LOGGER.info("MinioController | removeListObject is called");
        LOGGER.info("MinioController | removeListObject | bucketName : " + bucketName);
        LOGGER.info("MinioController | removeListObject | objectNameList size : " + objectNameList.size());
        boolean state =  minioService.removeListObject(bucketName, objectNameList) ;
        LOGGER.info("MinioController | removeListObject | state : " + state);
        if(state){
            return ResultDataUtils.success("Delete object successfully");
        }else{
            return ResultDataUtils.error("Delete object failed");
        }
    }

    @ApiOperation("查看指定存储桶的文件对象名称列表和及下载URL")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bucketName", value = "存储桶名称", dataType = "String", paramType = "path"),
    })
    @RequestMapping(value = "/showListObjectNameAndDownloadUrl/{bucketName}", method = RequestMethod.GET)
    public ResponseEntity<?> showListObjectNameAndDownloadUrl(@PathVariable String bucketName) {
        try {
            LOGGER.info("MinioController | showListObjectNameAndDownloadUrl is called");
            LOGGER.info("MinioController | showListObjectNameAndDownloadUrl | bucketName : " + bucketName);
            Map<String, String> map = new HashMap<>();
            List<String> listObjectNames = minioService.listObjectName(bucketName);
            LOGGER.info("MinioController | showListObjectNameAndDownloadUrl | listObjectNames size : " + listObjectNames.size());
            String url = minioBaseUrl + "/" + bucketName + "/";
            LOGGER.info("MinioController | showListObjectNameAndDownloadUrl | url : " + url);
            for (int i = 0; i <listObjectNames.size() ; i++) {
                map.put(listObjectNames.get(i),url+listObjectNames.get(i));
            }
            LOGGER.info("MinioController | showListObjectNameAndDownloadUrl | map : " + map);
            return ResultDataUtils.success(map);
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("从指定存储桶下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bucketName", value = "存储桶名称", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "objectName", value = "文件对象名称", dataType = "String", paramType = "path"),
    })
    @RequestMapping(value = "/download/{bucketName}/{objectName}", method = RequestMethod.GET)
    public ResponseEntity<?> download(HttpServletResponse response,
                         @PathVariable("bucketName") String bucketName,
                         @PathVariable("objectName") String objectName) {
        LOGGER.info("MinioController | download is called");
        LOGGER.info("MinioController | download | bucketName : " + bucketName);
        LOGGER.info("MinioController | download | objectName : " + objectName);
        InputStream in = null;
        try {
            in = minioService.downloadObject(bucketName, objectName);
            response.setHeader("Content-Disposition", "attachment;filename="
                    + URLEncoder.encode(objectName, "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            // 从 InputStream 中删除字节复制到 OutputStream
            IOUtils.copy(in, response.getOutputStream());
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("MinioController | download | UnsupportedEncodingException : " + e.getMessage());
        } catch (IOException e) {
            LOGGER.info("MinioController | download | IOException : " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.info("MinioController | download | IOException : " + e.getMessage());
                }
            }
        }
        return ResultDataUtils.success("Download successfully");
    }
}

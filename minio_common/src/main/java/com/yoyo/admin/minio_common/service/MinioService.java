package com.yoyo.admin.minio_common.service;

import com.yoyo.admin.minio_common.config.MinioConfig;
import com.yoyo.admin.minio_common.util.MinioUtils;
import io.minio.messages.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioService.class);

    private final MinioUtils minioUtils;
    private final MinioConfig minioProperties;

    /**
     * 检查存储桶是否已经存在
     * @param bucketName
     * @return
     */
    public boolean bucketExists(String bucketName) {
        LOGGER.info("MinioService | bucketExists is called");
        return minioUtils.bucketExists(bucketName);
    }

    /**
     * 创建存储桶
     * @param bucketName
     */
    public void makeBucket(String bucketName) {
        LOGGER.info("MinioService | makeBucket is called");
        LOGGER.info("MinioService | makeBucket | bucketName : " + bucketName);
        minioUtils.makeBucket(bucketName);
    }

    /**
     * 列出所有存储桶名称
     * @return
     */
    public List<String> listBucketName() {
        LOGGER.info("MinioService | listBucketName is called");
        return minioUtils.listBucketNames();
    }

    /**
     * 列出所有存储桶
     * @return
     */
    public List<Bucket> listBuckets() {
        LOGGER.info("MinioService | listBuckets is called");
        return minioUtils.listBuckets();
    }

    /**
     * 根据名称删除存储桶
     * @param bucketName
     * @return
     */
    public boolean removeBucket(String bucketName) {
        LOGGER.info("MinioService | removeBucket is called");
        LOGGER.info("MinioService | removeBucket | bucketName : " + bucketName);
        return minioUtils.removeBucket(bucketName);
    }

    /**
     * 列出指定存储桶的所有文件对象
     * @param bucketName
     * @return
     */
    public List<String> listObjectName(String bucketName) {
        LOGGER.info("MinioService | listObjectName is called");
        LOGGER.info("MinioService | listObjectName | bucketName : " + bucketName);
        return minioUtils.listObjectName(bucketName);
    }

    /**
     * 上传文件对象到指定存储桶
     * @param multipartFile
     * @param bucketName
     * @param fileType
     */
    @SneakyThrows
    public void putObject(MultipartFile multipartFile, String bucketName, String fileType) {
        LOGGER.info("MinioService | putObject is called");
        try {
            bucketName = StringUtils.isNotBlank(bucketName) ? bucketName : minioProperties.getBucketName();
            LOGGER.info("MinioService | putObject | bucketName : " + bucketName);
            if (!this.bucketExists(bucketName)) {
                this.makeBucket(bucketName);
                LOGGER.info("MinioService | putObject | bucketName : " + bucketName + " created");
            }
            String fileName = multipartFile.getOriginalFilename();
            LOGGER.info("MinioService | getFileType | fileName : " + fileName);
            long fileSize = multipartFile.getSize();
            LOGGER.info("MinioService | getFileType | fileSize : " + fileSize);
            assert fileName != null;
            String objectName = UUID.randomUUID().toString().replaceAll("-", "")
                    + fileName.substring(fileName.lastIndexOf("."));
            LOGGER.info("MinioService | getFileType | objectName : " + objectName);
            LocalDateTime createdTime = LocalDateTime.now();
            LOGGER.info("MinioService | getFileType | createdTime : " + createdTime);
            minioUtils.putObject(bucketName, multipartFile, objectName,fileType);
            LOGGER.info("MinioService | getFileType | url : " + minioProperties.getEndpoint()+"/"+bucketName+"/"+objectName);
        } catch (Exception e) {
            LOGGER.info("MinioService | getFileType | Exception : " + e.getMessage());
        }
    }

    /**
     * 从指定存储桶下载文件
     * @param bucketName
     * @param objectName
     * @return
     */
    public InputStream downloadObject(String bucketName, String objectName) {
        LOGGER.info("MinioService | downloadObject is called");
        LOGGER.info("MinioService | downloadObject | bucketName : " + bucketName);
        LOGGER.info("MinioService | downloadObject | objectName : " + objectName);
        return minioUtils.getObject(bucketName,objectName);
    }

    /**
     * 从指定存储桶删除文件对象
     * @param bucketName
     * @param objectName
     * @return
     */
    public boolean removeObject(String bucketName, String objectName) {
        LOGGER.info("MinioService | removeObject is called");
        LOGGER.info("MinioService | removeObject | bucketName : " + bucketName);
        LOGGER.info("MinioService | removeObject | objectName : " + objectName);
        return minioUtils.removeObject(bucketName, objectName);
    }

    /**
     * 从指定存储桶批量删除文件对象
     * @param bucketName
     * @param objectNameList
     * @return
     */
    public boolean removeListObject(String bucketName, List<String> objectNameList) {
        LOGGER.info("MinioService | removeListObject is called");
        LOGGER.info("MinioService | removeObject | bucketName : " + bucketName);
        LOGGER.info("MinioService | removeObject | objectNameList size : " + objectNameList.size());
        return minioUtils.removeObject(bucketName, objectNameList);
    }

    /**
     * 从存储桶中获取文件路径
     * @param bucketName
     * @param objectName
     * @return
     */
    public String getObjectUrl(String bucketName, String objectName) {
        LOGGER.info("MinioService | getObjectUrl is called");
        LOGGER.info("MinioService | getObjectUrl | bucketName : " + bucketName);
        LOGGER.info("MinioService | getObjectUrl | objectName : " + objectName);
        return minioUtils.getObjectUrl(bucketName, objectName);
    }
}

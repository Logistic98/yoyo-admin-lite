package com.yoyo.admin.minio_common.util;

import com.yoyo.admin.minio_common.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MinioUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioUtils.class);

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 上传文件到指定存储桶
     * @param bucketName
     * @param multipartFile
     * @param filename
     * @param fileType
     */
    @SneakyThrows
    public void putObject(String bucketName, MultipartFile multipartFile, String filename, String fileType) {
        LOGGER.info("MinioUtil | putObject is called");
        LOGGER.info("MinioUtil | putObject | filename : " + filename);
        LOGGER.info("MinioUtil | putObject | fileType : " + fileType);
        InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(filename).stream(
                        inputStream, -1, minioConfig.getFileSize())
                        .contentType(fileType)
                        .build());
    }

    /**
     * 检查存储桶是否存在
     * @param bucketName
     * @return
     */
    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        LOGGER.info("MinioUtil | bucketExists is called");
        boolean found =
                minioClient.bucketExists(
                        BucketExistsArgs.builder().
                                bucket(bucketName).
                                build());
        LOGGER.info("MinioUtil | bucketExists | found : " + found);
        if (found) {
            LOGGER.info("MinioUtil | bucketExists | message : " + bucketName + " exists");
        } else {
            LOGGER.info("MinioUtil | bucketExists | message : " + bucketName + " does not exist");
        }
        return found;
    }

    /**
     * 创建存储桶
     * @param bucketName
     * @return
     */
    @SneakyThrows
    public boolean makeBucket(String bucketName) {
        LOGGER.info("MinioUtil | makeBucket is called");
        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | makeBucket | flag : " + flag);
        if (!flag) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());

            return true;
        } else {
            return false;
        }
    }

    /**
     * 列出存储桶
     * @return
     */
    @SneakyThrows
    public List<Bucket> listBuckets() {
        LOGGER.info("MinioUtil | listBuckets is called");
        return minioClient.listBuckets();
    }

    /**
     * 列出所有存储桶名称
     * @return
     */
    @SneakyThrows
    public List<String> listBucketNames() {
        LOGGER.info("MinioUtil | listBucketNames is called");
        List<Bucket> bucketList = listBuckets();
        LOGGER.info("MinioUtil | listBucketNames | bucketList size : " + bucketList.size());
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        LOGGER.info("MinioUtil | listBucketNames | bucketListName size : " + bucketListName.size());
        return bucketListName;
    }

    /**
     * 列出指定存储桶中的所有对象
     * @param bucketName
     * @return
     */
    @SneakyThrows
    public Iterable<Result<Item>> listObjects(String bucketName) {
        LOGGER.info("MinioUtil | listObjects is called");
        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | listObjects | flag : " + flag);
        if (flag) {
            return minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).build());
        }
        return null;
    }

    /**
     * 按名称删除存储桶
     * @param bucketName
     * @return
     */
    @SneakyThrows
    public boolean removeBucket(String bucketName) {
        LOGGER.info("MinioUtil | removeBucket is called");
        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | removeBucket | flag : " + flag);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                // 当存储桶中有文件对象时删除失败
                LOGGER.info("MinioUtil | removeBucket | item size : " + item.size());
                if (item.size() > 0) {
                    return false;
                }
            }
            // 桶为空时删除桶
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            flag = bucketExists(bucketName);
            LOGGER.info("MinioUtil | removeBucket | flag : " + flag);
            if (!flag) {
                return true;
            }
        }
        return false;
    }

    /**
     * 列出指定存储桶中的所有对象名称
     * @param bucketName
     * @return
     */
    @SneakyThrows
    public List<String> listObjectName(String bucketName) {
        LOGGER.info("MinioUtil | listObjectName is called");
        List<String> listObjectName = new ArrayList<>();
        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | listObjectName | flag : " + flag);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectName.add(item.objectName());
            }
        } else {
            listObjectName.add(" Bucket does not exist ");
        }
        LOGGER.info("MinioUtil | listObjectName | listObjectNames size : " + listObjectName.size());
        return listObjectName;
    }

    /**
     * 从指定存储桶中删除指定对象
     * @param bucketName
     * @param objectName
     * @return
     */
    @SneakyThrows
    public boolean removeObject(String bucketName, String objectName) {
        LOGGER.info("MinioUtil | removeObject is called");
        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | removeObject | flag : " + flag);
        if (flag) {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        }
        return false;
    }

    /**
     * 从指定存储桶获取文件路径
     * @param bucketName
     * @param objectName
     * @return
     */
    @SneakyThrows
    public String getObjectUrl(String bucketName, String objectName) {
        LOGGER.info("MinioUtil | getObjectUrl is called");
        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | getObjectUrl | flag : " + flag);
        String url = "";
        if (flag) {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(2, TimeUnit.MINUTES)
                            .build());
            LOGGER.info("MinioUtil | getObjectUrl | url : " + url);
        }
        return url;
    }

    /**
     * 从指定的存储桶中获取对象的元数据
     * @param bucketName
     * @param objectName
     * @return
     */
    @SneakyThrows
    public StatObjectResponse statObject(String bucketName, String objectName) {
        LOGGER.info("MinioUtil | statObject is called");
        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | statObject | flag : " + flag);
        if (flag) {
            StatObjectResponse stat =
                    minioClient.statObject(
                            StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            LOGGER.info("MinioUtil | statObject | stat : " + stat.toString());
            return stat;
        }
        return null;
    }

    /**
     * 从指定的存储桶中获取文件对象作为流
     * @param bucketName
     * @param objectName
     * @return
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        LOGGER.info("MinioUtil | getObject is called");
        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | getObject | flag : " + flag);
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                InputStream stream =
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .build());
                LOGGER.info("MinioUtil | getObject | stream : " + stream.toString());
                return stream;
            }
        }
        return null;
    }

    /**
     * 从指定的存储桶中获取一个文件对象作为流（断点下载）
     * @param bucketName
     * @param objectName
     * @param offset
     * @param length
     * @return
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName, long offset, Long length) {
        LOGGER.info("MinioUtil | getObject is called");
        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | getObject | flag : " + flag);
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                InputStream stream =
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .offset(offset)
                                        .length(length)
                                        .build());
                LOGGER.info("MinioUtil | getObject | stream : " + stream.toString());
                return stream;
            }
        }
        return null;
    }

    /**
     * 从指定存储桶中删除多个文件对象
     * @param bucketName
     * @param objectNames
     * @return
     */
    @SneakyThrows
    public boolean removeObject(String bucketName, List<String> objectNames) {
        LOGGER.info("MinioUtil | removeObject is called");
        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | removeObject | flag : " + flag);
        if (flag) {
            List<DeleteObject> objects = new LinkedList<>();
            for (int i = 0; i < objectNames.size(); i++) {
                objects.add(new DeleteObject(objectNames.get(i)));
            }
            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                LOGGER.info("MinioUtil | removeObject | error : " + error.objectName() + " " + error.message());
                return false;
            }
        }
        return true;
    }

    /**
     * 将 InputStream 对象上传到指定存储桶
     * @param bucketName
     * @param objectName
     * @param inputStream
     * @param contentType
     * @return
     */
    @SneakyThrows
    public boolean putObject(String bucketName, String objectName, InputStream inputStream, String contentType) {
        LOGGER.info("MinioUtil | putObject is called");
        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | putObject | flag : " + flag);
        if (flag) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                            inputStream, -1, minioConfig.getFileSize())
                            .contentType(contentType)
                            .build());
            StatObjectResponse statObject = statObject(bucketName, objectName);
            LOGGER.info("MinioUtil | putObject | statObject != null : " + (statObject != null));
            LOGGER.info("MinioUtil | putObject | statObject.size() : " + statObject.size());
            if (statObject.size() > 0) {
                return true;
            }
        }
        return false;
    }
}

package com.yoyo.admin.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传、下载根路径
 */
@Component
@ConfigurationProperties(prefix = "settings.files")
public class FileConfig {

    private String uploadRootPath;
    private String downloadRootPath;

    public String getUploadRootPath() {
        return uploadRootPath;
    }

    public void setUploadRootPath(String uploadRootPath) {
        this.uploadRootPath = uploadRootPath;
    }

    public String getDownloadRootPath() {
        return downloadRootPath;
    }

    public void setDownloadRootPath(String downloadRootPath) {
        this.downloadRootPath = downloadRootPath;
    }

}

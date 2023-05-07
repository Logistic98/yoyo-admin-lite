package com.yoyo.admin.common.domain.repository;

import com.yoyo.admin.common.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 上传文件
 */
public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    /**
     * 根据id获取上传文件
     * @param id 上传文件id
     * @return
     */
    UploadFile findFirstById(Long id);

}

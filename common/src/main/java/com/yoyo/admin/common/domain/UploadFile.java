package com.yoyo.admin.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 上传文件
 */
@Data
@Entity
@ApiModel(value = "upload_file", description = "上传文件")
@Table(name = "upload_file")
@org.hibernate.annotations.Table(appliesTo = "upload_file", comment = "上传文件表")
public class UploadFile {

    /**
     * 上传文件id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "上传文件id")
    @Column(name = "id", columnDefinition = "bigint COMMENT '上传文件id'")
    private Long id;

    /**
     * URL地址
     */
    @ApiModelProperty(value = "URL地址")
    @Column(name = "url", columnDefinition = "varchar(255) COMMENT 'URL地址'")
    private String url;

    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    @Column(name = "filename", columnDefinition = "varchar(255) COMMENT '文件名'")
    private String filename;

    /**
     * 文件类型
     */
    @ApiModelProperty(value = "文件类型：IMAGE,FILE,AUDIO,VIDEO")
    @Column(name = "file_type", columnDefinition = "varchar(255) COMMENT '文件类型：IMAGE,FILE,AUDIO,VIDEO'")
    private String fileType;

    /**
     * 扩展名
     */
    @ApiModelProperty(value = "扩展名")
    @Column(name = "extension_name", columnDefinition = "varchar(255) COMMENT '扩展名'")
    private String extensionName;

    /**
     * 完整路径
     */
    @ApiModelProperty(value = "完整路径")
    @Column(name = "complete_path", columnDefinition = "varchar(255) COMMENT '完整路径'")
    @JsonIgnore
    private String completePath;

    /**
     * 文件大小
     */
    @ApiModelProperty(value = "文件大小")
    @Column(name = "size", columnDefinition = "bigint COMMENT '文件大小'")
    private Long size;

    /**
     * 上传时间
     */
    @ApiModelProperty(value = "上传时间")
    @Column(name = "upload_time", columnDefinition = "datetime COMMENT '上传时间'")
    private Date uploadTime;

}

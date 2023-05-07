package com.yoyo.admin.common.logger.domain;

import com.yoyo.admin.common.logger.enumeration.OperationType;
import com.yoyo.admin.common.logger.enumeration.OperationTypeConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 操作日志
 */
@Data
@Entity
@ApiModel(value = "OperationLog", description = "操作日志")
@Table(name = "operation_log")
@org.hibernate.annotations.Table(appliesTo = "operation_log", comment = "操作日志表")
public class OperationLog {

    /**
     * 操作日志id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "操作日志id")
    @Column(name = "id", columnDefinition = "bigint COMMENT '操作日志id'")
    private Long id;

    /**
     * 操作类型id
     */
    @ApiModelProperty(value = "操作类型id")
    @Column(name = "operation_type_id", columnDefinition = "bigint COMMENT '操作类型id'")
    @Convert(converter = OperationTypeConverter.class)
    private OperationType operationType;

    /**
     * 终端名称
     */
    @ApiModelProperty(value = "终端名称")
    @Column(name = "terminal_name", length = 20, columnDefinition = "varchar(255) COMMENT '终端名称'")
    private String terminalName;

    /**
     * web用户Id
     */
    @ApiModelProperty(value = "web用户Id")
    @Column(name = "web_user_id", columnDefinition = "bigint COMMENT 'web用户Id'")
    private Long webUserId;

    /**
     * 小程序/公众号用户Id
     */
    @ApiModelProperty(value = "小程序/公众号用户Id")
    @Column(name = "wechat_user_id", length = 50, columnDefinition = "varchar(50) COMMENT '小程序/公众号用户Id'")
    private String wechatUserId;

    /**
     * 用户姓名
     */
    @ApiModelProperty(value = "用户姓名")
    @Column(name = "user_name", length = 50, columnDefinition = "varchar(50) COMMENT '用户姓名'")
    private String userName;

    /**
     * ip地址
     */
    @ApiModelProperty(value = "ip地址")
    @Column(name = "ip_address", length = 50, columnDefinition = "varchar(50) COMMENT 'ip地址'")
    private String ipAddress;

    /**
     * 具体操作，由注解传来
     */
    @ApiModelProperty(value = "具体操作，由注解传来")
    @Column(name = "operation", length = 50, columnDefinition = "varchar(50) COMMENT '具体操作，由注解传来'")
    private String operation;

    /**
     * 操作内容
     */
    @ApiModelProperty(value = "操作内容")
    @Column(name = "content", columnDefinition = "text COMMENT '操作内容'")
    private String content;

    /**
     * 操作对象
     */
    @ApiModelProperty(value = "操作对象")
    @Column(name = "target", length = 50, columnDefinition = "varchar(50) COMMENT '操作对象'")
    private String target;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Column(name = "remark", length = 1024, columnDefinition = "varchar(1024) COMMENT '备注'")
    private String remark;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time", updatable = false, columnDefinition = "datetime COMMENT '创建时间'")
    private Date createTime;

}

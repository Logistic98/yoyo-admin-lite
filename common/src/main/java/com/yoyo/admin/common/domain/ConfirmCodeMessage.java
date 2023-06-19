package com.yoyo.admin.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@ApiModel(value = "confirm_code_message", description = "阿里大于（大鱼）短信验证码表")
@Table(name = "confirm_code_message", indexes = {@Index(columnList = "phone_number")})
@org.hibernate.annotations.Table(appliesTo = "confirm_code_message", comment = "阿里大于（大鱼）短信验证码表")
public class ConfirmCodeMessage {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键")
    @Column(name = "id", columnDefinition = "bigint COMMENT '主键'")
    private Long id;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @Column(name = "phone_number", columnDefinition = "varchar(255) COMMENT '手机号码'")
    private String phoneNumber;

    /**
     * 短信验证码
     */
    @ApiModelProperty(value = "短信验证码")
    @Column(name = "confirm_code", columnDefinition = "varchar(10) COMMENT '短信验证码'")
    private String confirmCode;

    /**
     * 是否使用
     */
    @ApiModelProperty(value = "是否使用 0:未使用 1:已使用")
    @Column(name = "is_used", columnDefinition = "int COMMENT '是否使用 0:未使用 1:已使用'")
    private Integer isUsed;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time", updatable = false, columnDefinition = "datetime COMMENT '创建时间'")
    private Date createTime;

    /**
     * 验证码类型
     */
    @ApiModelProperty(value = "验证码类型 1：注册 2：登录 3：修改密码")
    @Column(name = "type", columnDefinition = "int COMMENT '验证码类型 1：注册 2：登录 3：修改密码'")
    private Integer type;

}

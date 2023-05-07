package com.yoyo.admin.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

/**
 * 用户表
 */
@Data
@Entity
@ApiModel(value = "user", description = "用户")
@Table(name = "user")
@org.hibernate.annotations.Table(appliesTo = "user", comment = "用户表")
public class User implements UserDetails {

    /**
     * 用户id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "用户id")
    @Column(name = "id", columnDefinition = "bigint COMMENT '用户id'")
    private Long id;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @Column(name = "name", columnDefinition = "varchar(255) COMMENT '姓名'")
    private String name;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别 1:男，2:女")
    @Column(name = "gender", columnDefinition = "int COMMENT '性别 1:男，2:女'")
    private Integer gender;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    @Column(name = "username", columnDefinition = "varchar(255) COMMENT '用户名'")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @JsonIgnore
    @Column(name = "password", columnDefinition = "varchar(255) COMMENT '密码'")
    private String password;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @Column(name = "phone_number", columnDefinition = "varchar(255) COMMENT '手机号码'")
    private String phoneNumber;

    /**
     * 用户角色
     */
    @JsonIgnoreProperties({"depts"})
    @ManyToOne
    @ApiModelProperty(value = "用户角色")
    @JoinColumn(name = "role_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT), columnDefinition = "bigint COMMENT '用户角色ID'")
    private Role role;

    /**
     * 所属责任部门
     */
    @JsonIgnoreProperties({"parentDept"})
    @ManyToOne
    @ApiModelProperty(value = "所属责任部门")
    @JoinColumn(name = "dept_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT), columnDefinition = "bigint COMMENT '所属责任部门ID'")
    private Dept dept;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    @Column(name = "head_image", columnDefinition = "varchar(255) COMMENT '头像'")
    private String headImage;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time", updatable = false, columnDefinition = "datetime COMMENT '创建时间'")
    private Date createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @ApiModelProperty(value = "更新时间")
    @Column(name = "update_time", columnDefinition = "datetime COMMENT '更新时间'")
    private Date updateTime;

    /**
     * 最新登录时间
     */
    @ApiModelProperty(value = "最新登录时间")
    @Column(name = "login_time", columnDefinition = "datetime COMMENT '最新登录时间'")
    private Date loginTime;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除 0:未删除,1:已删除")
    @Column(name = "is_deleted", columnDefinition = "int COMMENT '是否删除 0:未删除,1:已删除'")
    @JsonIgnore
    private Integer isDeleted = 0;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}

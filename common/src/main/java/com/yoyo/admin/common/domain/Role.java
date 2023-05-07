package com.yoyo.admin.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 角色表
 */
@Data
@Entity
@ApiModel(value = "role", description = "角色")
@Table(name = "role")
@org.hibernate.annotations.Table(appliesTo = "role", comment = "角色表")
public class Role implements Serializable {

    /**
     * 角色id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "角色id")
    @Column(name = "id", columnDefinition = "bigint COMMENT '角色id'")
    private Long id;

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    @Column(name = "code", length = 30, columnDefinition = "varchar(30) COMMENT '编码'")
    private String code;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @Column(name = "name", columnDefinition = "varchar(255) COMMENT '名称'")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Column(name = "description", columnDefinition = "varchar(255) COMMENT '描述'")
    private String description;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time", columnDefinition = "datetime COMMENT '创建时间'")
    private Date createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @ApiModelProperty(value = "更新时间")
    @Column(name = "update_time", columnDefinition = "datetime COMMENT '更新时间'")
    private Date updateTime;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除 0:未删除 1:已删除")
    @Column(name = "is_deleted", columnDefinition = "int COMMENT '是否删除 0:未删除 1:已删除'")
    @JsonIgnore
    private Integer isDeleted = 0;

    /**
     * 功能权限-菜单
     */
    @ApiModelProperty(value = "功能权限-菜单")
    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "role_menu_list", joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "menu_id", referencedColumnName = "id")})
    private List<Menu> menus;

}

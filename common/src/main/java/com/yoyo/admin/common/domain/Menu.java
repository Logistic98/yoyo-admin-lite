package com.yoyo.admin.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yoyo.admin.common.enums.MenuTypeEnum;
import com.yoyo.admin.common.enums.VisibleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 菜单表
 */
@Entity
@Data
@ApiModel(value = "menu", description = "菜单")
@Table(name = "menu")
@org.hibernate.annotations.Table(appliesTo = "menu", comment = "菜单表")
public class Menu implements Serializable {

    /**
     * 菜单id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "菜单id")
    @Column(name = "id", columnDefinition = "bigint COMMENT '菜单id'")
    private Long id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @Column(name = "name", columnDefinition = "varchar(255) COMMENT '名称'")
    private String name;

    /**
     * url
     */
    @ApiModelProperty(value = "url")
    @Column(name = "url", columnDefinition = "varchar(255) COMMENT 'url'")
    private String url;

    /**
     * uri
     */
    @ApiModelProperty(value = "uri")
    @Column(name = "uri", columnDefinition = "varchar(255) COMMENT '后端接口uri'")
    private String uri;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    @Column(name = "icon", columnDefinition = "varchar(255) COMMENT '图标'")
    private String icon;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @Column(name = "`sort`", columnDefinition = "int COMMENT '排序'")
    private Integer sort = 1;

    /**
     * 菜单状态
     */
    @ApiModelProperty(value = "菜单状态 0显示,1隐藏")
    @Column(name = "visible", columnDefinition = "int COMMENT '菜单状态 0显示,1隐藏'")
    private VisibleTypeEnum visible = VisibleTypeEnum.VISIBLE;

    /**
     * 菜单类型
     */
    @ApiModelProperty(value = "菜单类型 0:主菜单 1:子菜单 2:按钮")
    @Column(name = "type", columnDefinition = "int COMMENT '菜单类型 0:主菜单 1:子菜单 2:按钮'")
    private MenuTypeEnum type;

    /**
     * 权限编码
     */
    @ApiModelProperty(value = "权限编码")
    @Column(name = "permission", columnDefinition = "varchar(255) COMMENT '权限编码'")
    private String permission;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Column(name = "description", columnDefinition = "varchar(255) COMMENT '描述'")
    private String description;

    /**
     * 父菜单
     */
    @ManyToOne
    @ApiModelProperty(value = "父菜单")
    @JoinColumn(name = "parent_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT), columnDefinition = "bigint COMMENT '父菜单ID'")
    private Menu parentMenu;

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

    @Transient
    private List<Menu> children;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Menu)) {
            return false;
        }
        Menu menu = (Menu) o;
        return id.equals(menu.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}

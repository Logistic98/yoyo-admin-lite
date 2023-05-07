package com.yoyo.admin.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 部门表
 */
@Entity
@Data
@ApiModel(value = "Dept", description = "部门")
@Table(name = "dept")
@NamedEntityGraph(name = "dept.all", attributeNodes = {@NamedAttributeNode(value = "parentDept", subgraph = "parent.parent")},
        subgraphs = {
                @NamedSubgraph(name = "parent.parent", attributeNodes = {@NamedAttributeNode(value = "parentDept")})
        })
@org.hibernate.annotations.Table(appliesTo = "dept", comment = "部门表")
public class Dept implements Serializable {

    /**
     * 部门id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "部门id")
    @Column(name = "id", columnDefinition = "bigint COMMENT '部门id'")
    private Long id;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time", updatable = false, columnDefinition = "datetime COMMENT '创建时间'")
    @CreationTimestamp
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @Column(name = "update_time", columnDefinition = "datetime COMMENT '更新时间'")
    @UpdateTimestamp
    private Date updateTime;

    /**
     * 是否已删除
     */
    @ApiModelProperty(value = "是否已删除 0:未删除 1:已删除")
    @Column(name = "is_deleted", columnDefinition = "int COMMENT '是否已删除 0:未删除 1:已删除'")
    private Integer isDeleted = 0;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Column(name = "remark", columnDefinition = "varchar(255) COMMENT '备注'")
    private String remark;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    @Column(name = "name", columnDefinition = "varchar(255) COMMENT '部门名称'")
    private String name;

    /**
     * 父部门
     */
    @ManyToOne
    @ApiModelProperty(value = "父部门")
    @JoinColumn(name = "parent_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT), columnDefinition = "bigint COMMENT '父部门ID'")
    private Dept parentDept;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    @Column(name = "order_num", columnDefinition = "int COMMENT '显示顺序'")
    private Integer orderNum;

    /**
     * 是否拥有全部数据权限
     */
    @ApiModelProperty(value = "是否拥有全部数据权限 0:否 1:是")
    @Column(name = "has_all_scope", columnDefinition = "int COMMENT '是否拥有全部数据权限 0:否 1:是'")
    @JsonIgnore
    private Integer hasAllScope = 0;

    /**
     * 部门状态
     */
    @ApiModelProperty(value = "部门状态 0:停用 1:启用")
    @Column(name = "status", columnDefinition = "int COMMENT '部门状态 0:停用 1:启用'")
    private Integer status;

}

package com.yoyo.admin.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 行政区划表
 */
@Data
@Entity
@ApiModel(value = "area", description = "行政区划")
@Table(name = "area")
@org.hibernate.annotations.Table(appliesTo = "area", comment = "行政区划表")
public class Area implements Serializable {

    /**
     * 地区id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "地区id")
    @Column(name = "id", columnDefinition = "bigint COMMENT '地区id'")
    private Long id;

    /**
     * 代码编号
     */
    @ApiModelProperty(value = "代码编号")
    @Column(name = "code", columnDefinition = "varchar(16) COMMENT '代码编号'")
    private String code;

    /**
     * 地区名称
     */
    @ApiModelProperty(value = "地区名称")
    @Column(name = "name", columnDefinition = "varchar(255) COMMENT '地区名称'")
    private String name;

    /**
     * 行政级别
     */
    @ApiModelProperty(value = "行政级别 1:省级，2:地市级，3:区县极，4:街道级，5:社区级")
    @Column(name = "grade", columnDefinition = "int(10) COMMENT '行政级别 1:省级，2:地市级，3:区县极，4:街道级，5:社区级'")
    private Integer grade;

    /**
     * 父级行政区域
     */
    @ManyToOne
    @JoinColumn(name = "parent_code", referencedColumnName = "code", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Area parent;

}

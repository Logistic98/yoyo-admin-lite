package com.yoyo.admin.common.vo;

import com.yoyo.admin.common.domain.Dept;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 部门VO
 */
@Data
public class DeptVO {

    private Long id;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;
    private String remark;
    private String name;
    private Dept parentDept;
    private Integer orderNum;
    private Integer status;
    private List<DeptVO> children;
    private String title;
    private Long key;
    private Boolean isLeaf;

    public DeptVO(Dept dept) {
        this.id = dept.getId();
        this.createTime = dept.getCreateTime();
        this.updateTime = dept.getUpdateTime();
        this.isDeleted = dept.getIsDeleted();
        this.remark = dept.getRemark();
        this.name = dept.getName();
        this.parentDept = dept.getParentDept();
        this.orderNum = dept.getOrderNum();
        this.status = dept.getStatus();
        this.key = dept.getId();
        this.title = dept.getName();
    }

    public void setChildren(List<DeptVO> children) {
        this.children = children;
        this.isLeaf = children == null || children.size() == 0;
    }

}

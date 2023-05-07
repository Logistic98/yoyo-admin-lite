package com.yoyo.admin.common.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;

import com.yoyo.admin.common.constant.Result;
import com.yoyo.admin.common.constant.SysConstants;
import com.yoyo.admin.common.domain.Dept;
import com.yoyo.admin.common.domain.repository.DeptRepository;
import com.yoyo.admin.common.logger.annotation.CreateLog;
import com.yoyo.admin.common.logger.annotation.DeleteLog;
import com.yoyo.admin.common.logger.annotation.UpdateLog;
import com.yoyo.admin.common.vo.DeptVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 部门相关接口
 */
@Service
public class DeptService {

    private DeptRepository deptRepository;

    @Autowired
    public void setDeptRepository(DeptRepository deptRepository) {
        this.deptRepository = deptRepository;
    }

    /**
     * 获取责任部门列表
     *
     * @param name  责任部门名称
     * @param status 责任部门状态
     * @param deptIds 责任部门id列表
     * @return 责任部门对象列表结果
     */
    public List<DeptVO> listDepts(String name, Integer status, Set<Long> deptIds) {
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order);
        Sort sort = Sort.by(orders);
        Specification<Dept> specification = (root, query, builder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(builder.equal(root.get("isDeleted"), 0));
            if (StrUtil.isNotEmpty(name)) {
                predicateList.add(builder.isNotNull(root.get("name")));
                predicateList.add(builder.like(root.get("name"), "%" + name + "%"));
            }
            if (status != null) {
                predicateList.add(builder.equal(root.get("status"), status));
            }
            if (deptIds != null && deptIds.size() > 0) {
                CriteriaBuilder.In<Long> in = builder.in(root.get("id"));
                deptIds.forEach(in::value);
                predicateList.add(in);
            }
            Predicate[] arrayType = new Predicate[predicateList.size()];
            return builder.and(predicateList.toArray(arrayType));
        };
        List<Dept> list = deptRepository.findAll(specification, sort);
        return deptTree(list);
    }

    /**
     * 获取责任部门详细信息
     *
     * @param id 责任部门id
     * @return 责任部门对象详情结果
     */
    public Dept getDept(Long id) {
        return deptRepository.findFirstById(id);
    }

    /**
     * 获取责任部门
     *
     * @param name 责任部门名称
     * @return 责任部门对象详情结果
     */
    public Dept getDeptByName(String name) {
        return deptRepository.findByNameAndIsDeleted(name, 0);
    }

    /**
     * 增加责任部门
     *
     * @param dept 责任部门对象
     * @return 增加结果
     */
    @CreateLog(target = "责任部门", operation = "新增责任部门")
    public Dept addDept(Dept dept) throws Exception {
        if (isNameExist(dept)) {
            throw new Exception(Result.DEPT_EXIST);
        }
        // 默认没有全部数据权限
        dept.setHasAllScope(0);
        dept.setStatus(1);
        deptRepository.save(dept);
        return dept;
    }

    /**
     * 修改责任部门
     *
     * @param dept 责任部门对象
     * @return 修改结果
     */
    @UpdateLog(target = "责任部门", operation = "修改责任部门")
    public Dept updateDept(Dept dept) throws Exception {
        if (isNameExist(dept)) {
            throw new Exception(Result.DEPT_EXIST);
        }
        if (dept.getParentDept() != null) {
            if (isChild(dept, dept.getParentDept())) {
                throw new Exception(Result.PARENT_IS_CHILD);
            }
        }
        Dept oldDept = deptRepository.findFirstById(dept.getId());
        BeanUtil.copyProperties(dept, oldDept, CopyOptions.create().setIgnoreNullValue(true));
        if (dept.getParentDept() == null) {
            oldDept.setParentDept(null);
        }
        return deptRepository.save(oldDept);
    }

    /**
     * 删除责任部门
     *
     * @param id 责任部门对象id
     */
    @DeleteLog(target = "责任部门", operation = "删除责任部门")
    public void deleteDept(Long id) throws Exception {
        Dept dept = deptRepository.findFirstById(id);
        if (id.equals(SysConstants.DEFAULT_ID)) {
            throw new Exception(Result.DEPT_IS_DEFAULT);
        }
        if (isChildNotDelete(dept)) {
            throw new Exception(Result.CHILD_NOT_DELETE);
        }
        dept.setIsDeleted(1);
        deptRepository.save(dept);
    }

    /**
     * 启用责任部门
     *
     * @param id 责任部门对象id
     */
    @UpdateLog(target = "责任部门", operation = "启用责任部门")
    public void enableDept(Long id) {
        Dept dept = deptRepository.findFirstById(id);
        dept.setStatus(1);
        deptRepository.save(dept);
    }

    /**
     * 禁用责任部门
     *
     * @param id 责任部门对象id
     */
    @UpdateLog(target = "责任部门", operation = "禁用责任部门")
    public void disableDept(Long id) {
        Dept dept = deptRepository.findFirstById(id);
        dept.setStatus(0);
        deptRepository.save(dept);
    }

    /**
     * 同一父级下不能有同名责任部门
     *
     * @param dept 责任部门对象
     * @return 结果 true 是 false 否
     */
    private boolean isNameExist(Dept dept) {
        Dept existDept = deptRepository.findFirstByNameAndParentDeptAndIsDeleted(dept.getName(), dept.getParentDept(), 0);
        if (existDept == null) {
            return false;
        } else {
            boolean isSame = (dept.getId() != null && dept.getId().equals(existDept.getId()));
            return !isSame;
        }
    }

    /**
     * 修改责任部门时不能挂载到子责任部门下
     *
     * @param dept       责任部门对象
     * @param parentDept 父责任部门对象
     * @return 结果 true 是 false 否
     */
    private boolean isChild(Dept dept, Dept parentDept) {
        Dept curParentDept = deptRepository.findFirstById(parentDept.getId());
        if (dept.getId().equals(parentDept.getId())) {
            return true;
        }
        if (curParentDept.getParentDept() == null) {
            return false;
        }
        return isChild(dept, curParentDept.getParentDept());
    }

    /**
     * 是否存在未删除的子责任部门
     *
     * @param dept 责任部门对象
     * @return 结果 true 是 false 否
     */
    private boolean isChildNotDelete(Dept dept) {
        List<Dept> list = deptRepository.findAllByIsDeletedAndParentDeptOrderByOrderNum(0, dept);
        return !list.isEmpty();
    }

    /**
     * 初始化时插入部门
     *
     * @param name         名称
     * @param parentDeptId 父部门id
     * @param hasAllScope 是否拥有全部数据权限
     * @return 部门对象
     */
    public Dept insertDept(String name, Long parentDeptId, Integer hasAllScope) {
        Dept parentDept = deptRepository.findFirstById(parentDeptId);
        Dept dept = new Dept();
        dept.setName(name);
        dept.setParentDept(parentDept);
        dept.setHasAllScope(hasAllScope);
        return deptRepository.save(dept);
    }

    /**
     * 将部门列表拼接成树
     */
    public List<DeptVO> deptTree(List<Dept> depts) {
        List<Dept> topList = new ArrayList<>();
        List<Dept> othersList = new ArrayList<>();
        for (Dept dept : depts) {
            if (dept.getParentDept() == null) {
                topList.add(dept);
            } else {
                boolean isTop = true;
                Long parentId = dept.getParentDept().getId();
                for (Dept dept1 : depts) {
                    if (parentId.equals(dept1.getId())) {
                        isTop = false;
                        break;
                    }
                }
                if (isTop) {
                    dept.setParentDept(null);
                    topList.add(dept);
                } else {
                    othersList.add(dept);
                }
            }
        }
        List<DeptVO> list = new ArrayList<>();
        for (Dept dept : topList) {
            DeptVO deptVO = new DeptVO(dept);
            deptVO.setChildren(getDeptChildren(othersList, dept.getId()));
            list.add(deptVO);
        }
        return list;
    }

    /**
     * 从指定列表中查询构建父部门的所有子部门树
     * @param deptList
     * @param parentId
     * @return
     */
    private List<DeptVO> getDeptChildren(List<Dept> deptList, Long parentId) {
        List<DeptVO> children = new ArrayList<>();
        for (Dept dept : deptList) {
            if (dept.getParentDept() != null && dept.getParentDept().getId().equals(parentId)) {
                DeptVO deptVO = new DeptVO(dept);
                deptVO.setChildren(getDeptChildren(deptList, dept.getId()));
                children.add(deptVO);
            }
        }
        return children;
    }

}

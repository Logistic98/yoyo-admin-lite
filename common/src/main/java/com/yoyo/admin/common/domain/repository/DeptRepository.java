package com.yoyo.admin.common.domain.repository;

import com.yoyo.admin.common.domain.Dept;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 部门管理
 */
public interface DeptRepository extends JpaRepository<Dept, Long> {

    /**
     * 获取相应id的部门
     *
     * @param id
     * @return 部门
     */
    Dept findFirstById(Long id);

    /**
     * 获取相应条件的部门
     *
     * @param name
     * @param dept
     * @param isDeleted
     * @return 部门
     */
    Dept findFirstByNameAndParentDeptAndIsDeleted(String name, Dept dept, Integer isDeleted);

    /**
     * 获取相应条件的部门列表
     *
     * @param specification
     * @param sort
     * @return 部门列表
     */
    List<Dept> findAll(Specification specification, Sort sort);

    /**
     * 获取相应条件的部门列表
     *
     * @param isDeleted
     * @param parentDept
     * @return 部门列表
     */
    List<Dept> findAllByIsDeletedAndParentDeptOrderByOrderNum(Integer isDeleted, Dept parentDept);

    /**
     * 获取相应条件的部门列表
     *
     * @param isDeleted
     * @return 部门列表
     */
    List<Dept> findAllByIsDeletedOrderByOrderNum(Integer isDeleted);

    /**
     * 根据名称获取部门
     */
    Dept findByNameAndIsDeleted(String name, Integer isDeleted);

    /**
     * 根据Id获取部门
     */
    Dept findFirstByIdAndIsDeleted(Long id, Integer isDeleted);

}

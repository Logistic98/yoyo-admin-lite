package com.yoyo.admin.common.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.yoyo.admin.common.constant.Result;
import com.yoyo.admin.common.constant.SysConstants;
import com.yoyo.admin.common.domain.Role;
import com.yoyo.admin.common.domain.repository.RoleRepository;
import com.yoyo.admin.common.domain.repository.UserRepository;
import com.yoyo.admin.common.logger.annotation.CreateLog;
import com.yoyo.admin.common.logger.annotation.DeleteLog;
import com.yoyo.admin.common.logger.annotation.UpdateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务相关接口
 */
@Service
public class RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 获取角色分页列表
     *
     * @param name     名称
     * @param page     页码
     * @param pageSize 页数
     * @return 角色对象分页列表结果
     */
    public Page<Role> pageRoles(String name, Integer page, Integer pageSize) {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order);
        Sort sort = Sort.by(orders);
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, sort);
        Specification<Role> specification = (root, query, builder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(builder.equal(root.get("isDeleted"), 0));
            if (StrUtil.isNotEmpty(name)) {
                predicateList.add(builder.like(root.get("name"), "%" + name + "%"));
            }
            Predicate[] arrayType = new Predicate[predicateList.size()];
            return builder.and(predicateList.toArray(arrayType));
        };

        return roleRepository.findAll(specification, pageRequest);
    }

    /**
     * 获取所有未删除的角色列表
     *
     * @return 角色对象列表结果
     */
    public List<Role> listRoles() {
        return roleRepository.findAllByIsDeleted(0);
    }

    /**
     * 获取角色详细信息
     *
     * @param id 角色对象id
     * @return 角色对象详情结果
     */
    public Role getRole(Long id) {
        return roleRepository.findFirstById(id);
    }

    /**
     * 获取指定用户的角色详细信息
     *
     * @param userId 用户对象id
     * @return 角色对象详情结果
     */
    public Role getRoleByUser(Long userId) {
        return roleRepository.findByUser(userId);
    }

    /**
     * 新增角色
     *
     * @param role 角色对象
     * @return 新增结果
     */
    @CreateLog(target = "角色", operation = "新增角色")
    public Role addRole(Role role) throws Exception {
        if (isExist(role)) {
            throw new Exception(Result.ROLE_EXIST);
        }
        roleRepository.save(role);
        return role;
    }

    /**
     * 修改角色
     *
     * @param role 角色对象
     * @return 修改结果
     */
    @UpdateLog(target = "角色", operation = "修改角色")
    public Role updateRole(Role role) throws Exception {
        if (isExist(role)) {
            throw new Exception(Result.ROLE_EXIST);
        }
        Role oldRole = roleRepository.findFirstById(role.getId());
        BeanUtil.copyProperties(role, oldRole, CopyOptions.create().setIgnoreNullValue(true));
        return roleRepository.save(oldRole);
    }

    /**
     * 删除角色
     *
     * @param role 角色对象
     * @return 删除结果
     */
    @DeleteLog(target = "角色", operation = "删除角色")
    public void deleteRole(Role role) throws Exception {
        if (role == null) {
            throw new Exception(Result.ROLE_NOT_FOUND);
        }
        if (role.getId().equals(SysConstants.DEFAULT_ID)) {
            throw new Exception(Result.ROLE_IS_ADMIN);
        }
        role.setIsDeleted(1);
        roleRepository.save(role);
    }

    /**
     * 判断角色名是否已存在
     *
     * @param role 角色对象
     * @return 结果 true 是 false 否
     */
    private boolean isExist(Role role) {
        Role existRole = roleRepository.findFirstByNameAndIsDeleted(role.getName(), 0);
        if (existRole == null) {
            return false;
        } else {
            return role.getId() == null || !role.getId().equals(existRole.getId());
        }
    }

}

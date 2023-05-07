package com.yoyo.admin.common.service;

import com.yoyo.admin.common.constant.SysConstants;
import com.yoyo.admin.common.domain.Dept;
import com.yoyo.admin.common.domain.User;
import com.yoyo.admin.common.domain.repository.DeptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限管理相关接口
 */
@Service
public class WebPermissionService {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private DeptRepository deptRepository;

    @Autowired
    public void setDeptRepository(DeptRepository deptRepository) {
        this.deptRepository = deptRepository;
    }

    /**
     * 获取用户可以操作的责任部门权限
     * @return 部门id集合
     */
    public Set<Long> getDataScopeDept() {
        User current = userService.current();
        if (current == null) {
            throw new AccessDeniedException("没有数据权限");
        }
        User user = userService.getUser(current.getId());
        if (user == null) {
            throw new AccessDeniedException("没有数据权限");
        }
        // 如果是超级管理员，拥有所有数据权限 / 如果角色数据权限为查看全部数据，拥有所有数据权限
        if (SysConstants.DEFAULT_ADMIN_ROLE.equals(user.getRole().getCode())) {
            return new HashSet<>();
        }
        Dept dept = user.getDept();
        // 部门是否能够查看全部数据权限
        if (dept != null && dept.getHasAllScope() == 1) {
            return new HashSet<>();
        }
        Set<Long> deptIds = new HashSet<>();
        deptIds.add(dept.getId());
        List<Dept> deptList = new ArrayList<>();
        deptList = getChildList(dept, deptList);
        deptList.forEach(item -> deptIds.add(item.getId()));
        return deptIds;
    }

    /**
     * 获取该责任部门所有数据权限
     * @param dept
     * @return
     */
    public List<Long> getAllDept(Dept dept) {
        List<Long> deptIds = new ArrayList<>();
        List<Dept> deptList = new ArrayList<>();
        deptList = getChildList(dept, deptList);
        deptList.forEach(item -> deptIds.add(item.getId()));
        return deptIds;
    }

    public List<Dept> getChildList(Dept dept, List<Dept> deptList) {
        deptList.add(dept);
        List<Dept> childList = deptRepository.findAllByIsDeletedAndParentDeptOrderByOrderNum(0, dept);
        if (childList.size() > 0) {
            for (Dept child : childList) {
                getChildList(child, deptList);
            }
        }
        return deptList;
    }

}

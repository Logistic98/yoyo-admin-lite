package com.yoyo.admin.common.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.yoyo.admin.common.config.SessionHolder;
import com.yoyo.admin.common.constant.Result;
import com.yoyo.admin.common.constant.SysConstants;
import com.yoyo.admin.common.domain.Dept;
import com.yoyo.admin.common.domain.Menu;
import com.yoyo.admin.common.domain.Role;
import com.yoyo.admin.common.domain.User;
import com.yoyo.admin.common.domain.repository.DeptRepository;
import com.yoyo.admin.common.domain.repository.UserRepository;
import com.yoyo.admin.common.logger.annotation.CreateLog;
import com.yoyo.admin.common.logger.annotation.DeleteLog;
import com.yoyo.admin.common.logger.annotation.UpdateLog;
import com.yoyo.admin.common.utils.ObjectConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * 系统用户相关接口
 */
@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private RoleService roleService;

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findFirstByUsernameAndIsDeleted(username, 0);
    }

    private MenuService menuService;

    @Autowired
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    private DeptRepository deptRepository;

    @Autowired
    public void setDeptRepository(DeptRepository deptRepository) {
        this.deptRepository = deptRepository;
    }

    /**
     * 获取当前用户
     *
     * @return 用户对象
     */
    public User current() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return null;
        }
        if (user instanceof User) {
            return (User) user;
        } else {
            return null;
        }
    }

    /**
     * 获取当前用户详情
     *
     * @return 用户对象
     */
    public User currentUser() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return null;
        }
        if (user instanceof User) {
            return userRepository.findFirstById(((User) user).getId());
        } else {
            return null;
        }
    }

    /**
     * 获取当前用户菜单列表
     *
     * @return 用户对象
     */
    public List<Menu> currentMenus() throws Exception {
        User user = this.current();
        if (user == null) {
            throw new Exception(Result.USER_NOT_LOGIN);
        }
        if (user.getRole() == null) {
            throw new Exception("该用户未分配角色");
        }
        List<Menu> menus = null;
        // 如果是超级管理员角色，返回所有菜单
        if (SysConstants.DEFAULT_ADMIN_ROLE.equals(user.getRole().getCode())) {
            menus = menuService.listUserMenus(null);
        } else {
            menus = menuService.listUserMenus(user.getRole().getId());
        }
        if (menus.size() == 0) {
            throw new Exception("该用户角色未分配菜单");
        }
        return menus;
    }

    /**
     * 修改当前用户密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @UpdateLog(target = "用户", operation = "修改密码")
    public void updatePassword(String oldPassword, String newPassword) throws Exception {
        User user = this.current();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new Exception("原密码错误");
        }
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

    }

    /**
     * 获取用户分页列表
     *
     * @param keyword  姓名/手机
     * @param page     页码
     * @param pageSize 页数
     * @return 用户对象列表结果
     */
    public Page<User> pageUsers(String keyword, Long deptId, Integer page, Integer pageSize, Set<Long> deptIds) {
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order);
        Sort sort = Sort.by(orders);
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, sort);
        Specification<User> specification = (root, query, builder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(builder.equal(root.get("isDeleted"), 0));
            if (StrUtil.isNotBlank(keyword)) {
                predicateList.add(builder.or(builder.like(root.get("name"), "%" + keyword + "%"), builder.like(root.get("phoneNumber"), "%" + keyword + "%")));
            }
            if (deptId != null) {
                predicateList.add(builder.isNotNull(root.get("dept")));
                predicateList.add(builder.equal(root.get("dept").get("id"), deptId));
            }
            if (deptIds != null && deptIds.size() > 0) {
                CriteriaBuilder.In<Long> in = builder.in(root.get("dept").get("id"));
                deptIds.forEach(in::value);
                predicateList.add(in);
            }
            Predicate[] arrayType = new Predicate[predicateList.size()];
            return builder.and(predicateList.toArray(arrayType));
        };
        Page<User> pages = userRepository.findAll(specification, pageRequest);
        return pages;
    }

    /**
     * 获取用户列表
     *
     * @param roleId 角色id
     * @return 用户对象列表结果
     */
    public List<User> listUsers(Long roleId) {
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order);
        Sort sort = Sort.by(orders);
        Specification<User> specification = (root, query, builder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(builder.equal(root.get("isDeleted"), 0));
            predicateList.add(builder.notEqual(root.get("username"), SysConstants.DEFAULT_ADMIN_USER));
            if (roleId != null) {
                predicateList.add(builder.isNotNull(root.get("role")));
                predicateList.add(builder.equal(root.get("role").get("id"), roleId));
            }
            Predicate[] arrayType = new Predicate[predicateList.size()];
            return builder.and(predicateList.toArray(arrayType));
        };
        List<User> users = userRepository.findAll(specification, sort);
        return users;
    }

    /**
     * 根据用户id获取用户详情
     *
     * @param id 用户id
     * @return 用户对象详情结果
     */
    public User getUser(Long id) {
        return userRepository.findFirstById(id);
    }

    /**
     * 根据用户名获取用户详情
     *
     * @param username
     * @return 用户对象详情结果
     */
    public User getUserByUsername(String username) {
        return userRepository.findFirstByUsernameAndIsDeleted(username, 0);
    }

    /**
     * 根据手机号获取用户详情
     *
     * @param phoneNumber
     * @return 用户对象详情结果
     */
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findFirstByPhoneNumberAndIsDeleted(phoneNumber, 0);
    }

    /**
     * 增加用户
     *
     * @param user 用户对象
     * @return 增加结果
     */
    @CreateLog(target = "用户", operation = "新增用户")
    public void addUser(User user) throws Exception {
        if (isExist(user.getUsername())) {
            throw new Exception(Result.USER_EXIST);
        }
        if (user.getRole() != null) {
            Role role = roleService.getRole(user.getRole().getId());
            if (role.getMenus() == null || role.getMenus().size() <= 0) {
                throw new Exception(Result.ROLE_NO_MENU);
            }
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * 修改用户
     *
     * @param body
     * @return 修改结果
     */
    @UpdateLog(target = "用户", operation = "修改用户")
    public void updateUser(Map<String, Object> body) throws Exception {
        Long id = ObjectConvertUtils.parseLong(body.get("id"));
        if (id == null) {
            throw new Exception("用户id不可为空");
        }
        String name = ObjectConvertUtils.parseString(body.get("name"));
        if (name == null || StrUtil.isEmpty(name)) {
            throw new Exception("姓名不可为空");
        }
        String username = ObjectConvertUtils.parseString(body.get("username"));
        if (username == null || StrUtil.isEmpty(username)) {
            throw new Exception("用户名不可为空");
        }
        if (isExist(username, id)) {
            throw new Exception("该用户已存在");
        }
        Long roleId = ObjectConvertUtils.parseLong(body.get("roleId"));
        if (roleId == null) {
            throw new Exception("角色不可为空");
        }
        Role role = roleService.getRole(roleId);
        if (role.getMenus() == null || role.getMenus().size() <= 0) {
            throw new Exception(Result.ROLE_NO_MENU);
        }
        Long deptId = ObjectConvertUtils.parseLong(body.get("deptId"));
        if (deptId == null) {
            throw new Exception("部门不可为空");
        }
        Dept dept = deptRepository.findFirstById(deptId);
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setUsername(username);
        user.setRole(role);
        user.setDept(dept);
        User oldUser = userRepository.findFirstById(id);
        user.setPassword(oldUser.getPassword());
        BeanUtil.copyProperties(user, oldUser, CopyOptions.create().setIgnoreNullValue(true));
        userRepository.save(oldUser);
    }

    /**
     * 删除用户
     *
     * @param user 用户
     * @return 删除结果
     */
    @DeleteLog(target = "用户", operation = "删除用户")
    public void deleteUser(User user) throws Exception {
        User current = this.current();
        if (user.getId().equals(SysConstants.DEFAULT_ID)) {
            throw new Exception(Result.ADMIN_USER_CAN_NOT_DELETE);
        }
        if (user.getId().equals(current.getId())) {
            throw new Exception(Result.CAN_NOT_DEL_SELF);
        }
        user.setIsDeleted(1);
        //被删除的用户强制退出登录
        SessionHolder.removeWebSession(user.getId());
        userRepository.save(user);
    }

    /**
     * 重置密码
     *
     * @param id 用户id
     * @return 删除结果
     */
    @UpdateLog(target = "用户", operation = "重置密码")
    public String resetPassword(Long id) throws Exception {
        User current = this.current();
        if (!SysConstants.DEFAULT_ADMIN_ROLE.equals(current.getRole().getCode())) {
            throw new Exception("只有超级管理员账号可以重置密码");
        }
        User user = userRepository.findFirstById(id);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String resetPassword = "88888888";
        user.setPassword(encoder.encode(resetPassword));
        userRepository.save(user);
        return resetPassword;
    }

    /**
     * 用户是否已存在
     *
     * @param username 用户名
     * @return 结果 true 是 false 否
     */
    private boolean isExist(String username) {
        User existUser = userRepository.findFirstByUsernameAndIsDeleted(username, 0);
        if (existUser == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 用户是否已存在（排除指定用户）
     *
     * @param username 用户名
     * @param id 排除的用户id
     * @return 结果 true 是 false 否
     */
    private boolean isExist(String username, Long id) {
        User existUser = userRepository.findFirstByUsernameAndIsDeletedAndIdNot(username, 0, id);
        if (existUser == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取指定用户的权限列表
     * @param user
     * @return
     */
    public Collection<? extends GrantedAuthority> getAuthorities(User user) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (user == null) {
            return grantedAuthorities;
        }
        Role role = user.getRole();
        if (role == null) {
            return grantedAuthorities;
        }
        Set<String> authorities = menuService.getRoleAuthorities(role.getId());
        if (authorities.size() > 0) {
            authorities.forEach(v -> {
                grantedAuthorities.add(new SimpleGrantedAuthority(v));
            });
        }
        return grantedAuthorities;
    }

}

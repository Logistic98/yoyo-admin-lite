package com.yoyo.admin.common.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import com.yoyo.admin.common.constant.Result;
import com.yoyo.admin.common.constant.SysConstants;
import com.yoyo.admin.common.domain.Menu;
import com.yoyo.admin.common.domain.Role;
import com.yoyo.admin.common.domain.repository.MenuRepository;
import com.yoyo.admin.common.domain.repository.RoleRepository;
import com.yoyo.admin.common.enums.MenuTypeEnum;
import com.yoyo.admin.common.enums.VisibleTypeEnum;
import com.yoyo.admin.common.utils.ResultDataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 菜单服务相关接口
 */
@Service
public class MenuService {

    private MenuRepository menuRepository;

    @Autowired
    public void setMenuRepository(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * 获取菜单列表
     *
     * @param name    名称
     * @param type    类型
     * @param visible 是否可见
     * @return 菜单对象列表
     */
    public List<Menu> listMenus(String name, MenuTypeEnum type, VisibleTypeEnum visible) {
        Sort.Order order1 = new Sort.Order(Sort.Direction.ASC, "sort");
        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "createTime");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        Sort sort = Sort.by(orders);
        Specification<Menu> specification = (root, query, builder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(builder.equal(root.get("isDeleted"), 0));
            if (StrUtil.isNotEmpty(name)) {
                predicateList.add(builder.isNotNull(root.get("name")));
                predicateList.add(builder.like(root.get("name"), "%" + name + "%"));
            }
            if (visible != null) {
                predicateList.add(builder.isNotNull(root.get("visible")));
                predicateList.add(builder.equal(root.get("visible"), visible));
            }
            if (type != null) {
                predicateList.add(builder.isNotNull(root.get("type")));
                predicateList.add(builder.equal(root.get("type"), type));
            }
            Predicate[] arrayType = new Predicate[predicateList.size()];
            return builder.and(predicateList.toArray(arrayType));
        };
        return menuRepository.findAll(specification, sort);
    }

    /**
     * 获取菜单详细信息
     *
     * @param id 菜单对象id
     * @return 菜单对象详情结果
     */
    public Menu getMenu(Long id) {
        return menuRepository.findFirstById(id);
    }

    /**
     * 查询指定角色拥有的菜单
     *
     * @param roleId
     * @return 菜单对象列表
     */
    public List<Menu> listUserMenus(Long roleId) {
        if (roleId != null) {
            return menuRepository.findAllByRole(roleId);
        } else {
            return menuRepository.findAllByIsDeleted(0);
        }
    }

    /**
     * 增加菜单
     *
     * @param menu 菜单对象
     * @return 增加结果
     */
    public Menu addMenu(Menu menu) throws Exception {
        if (isUrlExist(menu)) {
            throw new Exception(Result.MENU_URL_EXIST);
        }
        if (menu.getParentMenu() != null && VisibleTypeEnum.VISIBLE.equals(menu.getVisible())) {
            if (isParentHide(menu)) {
                throw new Exception(Result.MENU_PARENT_HIDE);
            }
        }
        menuRepository.save(menu);
        // 增加的菜单默认给超级管理员角色
        Role superAdmin = roleRepository.findFirstByCode(SysConstants.DEFAULT_ADMIN_ROLE);
        superAdmin.getMenus().add(menu);
        roleRepository.save(superAdmin);
        return menu;
    }

    /**
     * 修改菜单
     *
     * @param menu 菜单对象
     * @return 修改结果
     */
    public Menu updateMenu(Menu menu) throws Exception {
        if (isUrlExist(menu)) {
            throw new Exception(Result.MENU_URL_EXIST);
        }
        if (menu.getParentMenu() != null) {
            if (isChild(menu, menu.getParentMenu())) {
                throw new Exception(Result.PARENT_IS_CHILD);
            }
        }
        if (menu.getVisible() != null && VisibleTypeEnum.HIDE.equals(menu.getVisible())) {
            if (isUsed(menu)) {
                throw new Exception(Result.MENU_ROLE_USE);
            }
            if (isChildVisible(menu)) {
                throw new Exception(Result.MENU_CHILD_VISIBLE);
            }
        }
        if (menu.getParentMenu() != null && VisibleTypeEnum.VISIBLE.equals(menu.getVisible())) {
            if (isParentHide(menu)) {
                throw new Exception(Result.MENU_PARENT_HIDE);
            }
        }
        Menu oldMenu = menuRepository.findFirstById(menu.getId());
        BeanUtil.copyProperties(menu, oldMenu, CopyOptions.create().setIgnoreNullValue(true));
        if (menu.getParentMenu() == null) {
            oldMenu.setParentMenu(null);
        }
        return menuRepository.save(oldMenu);
    }

    /**
     * 删除菜单
     *
     * @param id 菜单对象id
     * @return 删除结果
     */
    public void deleteMenu(Long id) throws Exception {
        Menu menu = menuRepository.findFirstById(id);
        if (menu == null) {
            throw new Exception(Result.MENU_NOT_FOUND);
        }
        if (isChildNotDelete(menu)) {
            throw new Exception(Result.CHILD_NOT_DELETE);
        }
        if (isUsed(menu)) {
            throw new Exception(Result.MENU_ROLE_USE);
        }
        menu.setIsDeleted(1);
        menuRepository.save(menu);
    }

    /**
     * 获取指定角色的所有权限列表
     *
     * @param roleId
     * @return
     */
    public Set<String> getRoleAuthorities(Long roleId) {
        Set<String> authorities = new HashSet<>();
        List<Menu> list = menuRepository.findAllByRole(roleId);
        if (list.size() > 0) {
            list.forEach(v -> {
                if (StrUtil.isNotBlank(v.getPermission())) {
                    authorities.add(v.getPermission());
                }
            });
        }
        return authorities;
    }

    /**
     * 同一父级下是否有同名菜单
     *
     * @param menu 菜单对象
     * @return 结果 true 是 false 否
     */
    private boolean isNameExist(Menu menu) {
        Menu existMenu = menuRepository.findTopByNameAndParentMenuAndIsDeletedAndVisible(menu.getName().trim(), menu.getParentMenu(), 0, VisibleTypeEnum.VISIBLE);
        if (existMenu == null) {
            return false;
        } else {
            return menu.getId() == null || !menu.getId().equals(existMenu.getId());
        }
    }

    /**
     * 菜单Url不为#时是否相同
     *
     * @param menu 菜单对象
     * @return 结果 true 是 false 否
     */
    private boolean isUrlExist(Menu menu) {
        if (StrUtil.isEmpty(menu.getUrl()) || SysConstants.EMPTY_URL.equals(menu.getUrl())) {
            return false;
        }
        Menu existMenu = menuRepository.findTopByUrlAndIsDeletedAndVisible(menu.getUrl(), 0, VisibleTypeEnum.VISIBLE);
        if (existMenu == null) {
            return false;
        } else {
            return menu.getId() == null || !menu.getId().equals(existMenu.getId());
        }
    }


    /**
     * 修改菜单时不能挂载到子菜单下
     *
     * @param menu 菜单对象
     * @return 结果 true 是 false 否
     */
    private boolean isChild(Menu menu, Menu parentMenu) {
        Menu curParentMenu = menuRepository.findFirstById(parentMenu.getId());
        if (menu.getId().equals(parentMenu.getId())) {
            return true;
        }
        if (curParentMenu.getParentMenu() == null) {
            return false;
        }
        return isChild(menu, curParentMenu.getParentMenu());
    }

    /**
     * 菜单挂载判断，规则如下：
     * 目录（主菜单）只能挂载在目录下，或为根目录
     * 菜单（侧栏菜单）只能挂载在目录或菜单下
     * 按钮只能挂载在菜单下
     *
     * @param menu 菜单对象
     * @return 结果 true 是 false 否
     */
    private ResponseEntity<?> isRightParent(Menu menu) {
        Menu parentMenu = null;
        if (menu.getParentMenu() != null) {
            parentMenu = menuRepository.findFirstById(menu.getParentMenu().getId());
        }
        if (MenuTypeEnum.MAIN.equals(menu.getType())) {
            if (parentMenu != null && !MenuTypeEnum.MAIN.equals(parentMenu.getType())) {
                return ResultDataUtils.error(Result.MENU_MAIN_ROOT_ERROR);
            }
        } else if (MenuTypeEnum.CHILD.equals(menu.getType())) {
            if (parentMenu == null) {
                return ResultDataUtils.error(Result.MENU_CHILD_ROOT_ERROR);
            } else if (MenuTypeEnum.BUTTON.equals(parentMenu.getType())) {
                return ResultDataUtils.error(Result.MENU_CHILD_BIND_ERROR);
            }
        } else {
            if (parentMenu == null || !MenuTypeEnum.CHILD.equals(parentMenu.getType())) {
                return ResultDataUtils.error(Result.MENU_BUTTON_BIND_ERROR);
            }
        }
        return ResultDataUtils.success();
    }

    /**
     * 判断菜单是否已在使用
     *
     * @param menu 菜单对象
     * @return 结果 true 是 false 否
     */
    private boolean isUsed(Menu menu) {
        List<Role> roles = roleRepository.findAllByMenu(menu.getId());
        return !roles.isEmpty();
    }

    /**
     * 是否存在未删除的子菜单
     *
     * @param menu 菜单对象
     * @return 结果 true 是 false 否
     */
    private boolean isChildNotDelete(Menu menu) {
        List<Menu> list = menuRepository.findAllByIsDeletedAndParentMenu(0, menu);
        return !list.isEmpty();
    }

    /**
     * 是否存在未删除且显示的子菜单
     *
     * @param menu 菜单对象
     * @return 结果 true 是 false 否
     */
    private boolean isChildVisible(Menu menu) {
        List<Menu> list = menuRepository.findAllByIsDeletedAndVisibleAndParentMenu(0, VisibleTypeEnum.VISIBLE, menu);
        return !list.isEmpty();
    }

    /**
     * 父菜单是否隐藏
     *
     * @param menu 菜单对象
     * @return 结果 true 是 false 否
     */
    private boolean isParentHide(Menu menu) {
        Menu parentMenu = menuRepository.findFirstById(menu.getParentMenu().getId());
        return parentMenu.getVisible() == VisibleTypeEnum.HIDE;
    }

    /**
     * 初始化时插入菜单
     *
     * @param name        名称
     * @param type        类型
     * @param url         url
     * @param sort        排序
     * @param parentMenu  父菜单
     * @param description 描述
     * @return 菜单对象
     */
    public Menu insertMenu(String name, String type, String url, String permission, Integer sort, Menu parentMenu, String description) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setType((EnumUtil.fromString(MenuTypeEnum.class, type)));
        menu.setUrl(url);
        menu.setPermission(permission);
        menu.setSort(sort);
        menu.setParentMenu(parentMenu);
        menu.setVisible(VisibleTypeEnum.VISIBLE);
        menu.setDescription(description);
        return menuRepository.save(menu);
    }

}

package com.yoyo.admin.common.domain.repository;

import com.yoyo.admin.common.domain.Menu;
import com.yoyo.admin.common.enums.VisibleTypeEnum;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 菜单管理
 */
public interface MenuRepository extends JpaRepository<Menu, Long> {

    /**
     * 获取相应id的菜单
     *
     * @param id 菜单id
     * @return 菜单
     */
    Menu findFirstById(Long id);

    /**
     * 获取相应条件的菜单
     *
     * @param name       菜单名称
     * @param parentMenu 父菜单
     * @param isDeleted   是否删除
     * @param visible    是否可见
     * @return 菜单
     */
    Menu findTopByNameAndParentMenuAndIsDeletedAndVisible(String name, Menu parentMenu, Integer isDeleted, VisibleTypeEnum visible);

    /**
     * 获取相应条件的菜单
     *
     * @param url      url
     * @param isDeleted 是否删除
     * @param visible  是否可见
     * @return 菜单
     */
    Menu findTopByUrlAndIsDeletedAndVisible(String url, Integer isDeleted, VisibleTypeEnum visible);

    /**
     * 获取相应条件的菜单列表
     *
     * @param specification 条件
     * @param sort          排序
     * @return 菜单列表
     */
    List<Menu> findAll(Specification specification, Sort sort);

    /**
     * 获取相应条件的菜单列表
     *
     * @param isDeleted   是否删除
     * @param parentMenu 菜单名称
     * @return 菜单列表
     */
    List<Menu> findAllByIsDeletedAndParentMenu(Integer isDeleted, Menu parentMenu);

    /**
     * 获取相应条件的菜单列表
     *
     * @param isDeleted   是否删除
     * @param visible    是否可见
     * @param parentMenu 菜单名称
     * @return 菜单列表
     */
    List<Menu> findAllByIsDeletedAndVisibleAndParentMenu(Integer isDeleted, VisibleTypeEnum visible, Menu parentMenu);

    /**
     * 获取相应角色的菜单列表
     *
     * @param roleId   角色id
     * @return 菜单列表
     */
    @Query(nativeQuery = true, value = "select m.* from menu m, role_menu_list l where m.id = l.menu_id and l.role_id = ?1 and m.is_deleted = 0")
    List<Menu> findAllByRole(Long roleId);

    /**
     * 获取全部未删除菜单列表
     *
     * @param isDeleted 是否删除
     * @return 菜单列表
     */
    List<Menu> findAllByIsDeleted(Integer isDeleted);

}

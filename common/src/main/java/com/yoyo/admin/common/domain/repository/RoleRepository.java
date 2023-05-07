package com.yoyo.admin.common.domain.repository;

import com.yoyo.admin.common.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 角色管理
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 获取相应id的角色
     *
     * @param id 角色id
     * @return 角色对象
     */
    Role findFirstById(Long id);

    /**
     * 获取相应code的角色
     *
     * @param code 角色code
     * @return 角色对象
     */
    Role findFirstByCode(String code);

    /**
     * 获取相应name的角色
     *
     * @param name     角色名称
     * @param isDeleted 是否删除
     * @return 角色对象
     */
    Role findFirstByNameAndIsDeleted(String name, Integer isDeleted);

    /**
     * 获取用户的角色
     */
    @Query(nativeQuery = true, value = "SELECT r.* FROM role r, dept o, user u where r.id = o.role_id and u.dept_id = o.id and u.id=?1 ")
    Role findByUser(Long userId);

    /**
     * 获取相应条件的角色列表
     *
     * @param specification 条件
     * @param sort          排序
     * @return 角色对象列表
     */
    List<Role> findAll(Specification specification, Sort sort);

    /**
     * 获取相应条件的角色列表
     *
     * @param isDeleted  是否删除
     * @return 角色对象列表
     */
    List<Role> findAllByIsDeleted(Integer isDeleted);


    /**
     * 获取相应条件的角色分页列表
     *
     * @param specification 条件
     * @param pageable      分页
     * @return 角色对象分页列表
     */
    Page<Role> findAll(Specification specification, Pageable pageable);

    /**
     * 获取指定菜单的角色列表
     *
     * @param menuId  菜单Id
     * @return 角色对象列表
     */
    @Query(nativeQuery = true, value = "select r.* from role r, role_menu_list l where r.id = l.role_id and l.menu_id = ?1 and r.is_deleted = 0")
    List<Role> findAllByMenu(Long menuId);

}

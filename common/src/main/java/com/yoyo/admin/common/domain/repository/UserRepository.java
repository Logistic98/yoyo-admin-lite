package com.yoyo.admin.common.domain.repository;

import com.yoyo.admin.common.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 用户管理
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据id获取用户
     *
     * @param id
     * @return 用户
     */
    User findFirstById(Long id);

    /**
     * 根据用户名获取用户
     *
     * @param username
     * @param isDeleted
     * @return 用户
     */
    User findFirstByUsernameAndIsDeleted(String username, Integer isDeleted);

    /**
     * 根据用户名获取用户(排除指定用户）
     * @param username
     * @param isDeleted
     * @param id
     * @return
     */
    User findFirstByUsernameAndIsDeletedAndIdNot(String username, Integer isDeleted, Long id);

    /**
     * 根据手机号获取用户
     *
     * @param phoneNumber
     * @param isDeleted
     * @return 用户
     */
    User findFirstByPhoneNumberAndIsDeleted(String phoneNumber, Integer isDeleted);

    /**
     * 获取相应条件的用户分页列表
     *
     * @param specification 条件
     * @param pageable      分页
     * @return 用户分页列表
     */
    Page<User> findAll(Specification specification, Pageable pageable);

    /**
     * 获取相应条件的用户列表
     *
     * @param specification 条件
     * @param sort          排序
     * @return 用户列表
     */
    List<User> findAll(Specification specification, Sort sort);

    /**
     * 获取相应id的用户列表
     *
     * @param ids id列表
     * @return 用户列表
     */
    List<User> findAllByIdIn(List<Long> ids);

}

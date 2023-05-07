package com.yoyo.admin.common.domain.repository;

import com.yoyo.admin.common.domain.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * 行政区划管理
 */
public interface AreaRepository extends JpaRepository<Area, String> {

    /**
     * 按code获取地区
     *
     * @param code
     * @return 地区
     */
    Area findFirstByCode(String code);

    /**
     * 获取地区列表
     *
     * @param maxGrade
     * @return 地区列表
     */
    List<Area> findAllByGradeLessThanEqual(Integer maxGrade);

    /**
     * 查询所有地区编号
     *
     * @param maxGrade
     * @param codes
     * @return 地区列表
     */
    List<Area> findAllByGradeLessThanEqualAndCodeIn(Integer maxGrade, List<String> codes);

    /**
     * 获取指定地区列表的所有下级地区的列表
     *
     * @param maxGrade
     * @param parentCodes
     * @return 地区列表
     */
    List<Area> findAllByGradeLessThanEqualAndParent_CodeIn(Integer maxGrade, Collection<String> parentCodes);

}

package com.yoyo.admin.common.logger.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 操作日志
 */
public interface OperationLogRepository extends JpaRepository<OperationLog, Long>, JpaSpecificationExecutor<OperationLog> {

    /**
     * 根据id获取操作日志
     * @param id
     * @return
     */
    OperationLog findFirstById(Long id);

    /**
     * 获取系统全部操作日志
     * @param terminalNames
     * @return
     */
    @Query("select o.target from OperationLog o where o.terminalName in ?1 group by o.target")
    List<String> findAllTargets(List<String> terminalNames);
}

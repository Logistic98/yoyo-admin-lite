package com.yoyo.admin.common.domain.repository;

import com.yoyo.admin.common.domain.ConfirmCodeMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface ConfirmCodeMessagingRepository extends JpaRepository<ConfirmCodeMessage, Long> {

    /**
     * 根据手机号、创建时间、是否使用查询符合条件的记录
     * @param phoneNumber
     * @param maxDate
     * @param isUsed
     * @return
     */
    ConfirmCodeMessage findFirstByPhoneNumberAndCreateTimeAfterAndIsUsedOrderByIdDesc(String phoneNumber, Date maxDate, Integer isUsed);

}

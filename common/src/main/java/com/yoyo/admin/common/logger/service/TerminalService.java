package com.yoyo.admin.common.logger.service;

import org.springframework.stereotype.Component;

/**
 * 获取终端名
 */
@Component
public interface TerminalService {
    String getTerminalName();
}

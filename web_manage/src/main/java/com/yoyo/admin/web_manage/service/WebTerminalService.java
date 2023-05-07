package com.yoyo.admin.web_manage.service;

import com.yoyo.admin.common.logger.service.TerminalService;
import org.springframework.stereotype.Service;

/**
 * Web终端服务
 */
@Service
public class WebTerminalService implements TerminalService {
    @Override
    public String getTerminalName() {
        return "Web";
    }
}

package com.yoyo.admin.web_manage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "settings.hosts")
public class HostConfig {

    private Boolean useSingleUserLimit;

    public Boolean getUseSingleUserLimit() {
        return useSingleUserLimit;
    }

    public void setUseSingleUserLimit(Boolean useSingleUserLimit) {
        this.useSingleUserLimit = useSingleUserLimit;
    }

}

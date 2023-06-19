package com.yoyo.admin.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.sms")
public class AliMessageConfig {

    private String accessKeyId;
    private String accessKeySecret;
    private String domain;
    private String signName;
    private String loginTemplate;
    private String registerTemplate;
    private String changePasswordTemplate;
    private String breedTemplate;
    private String apiVersion;
    private Integer resendInterval;
    private Integer usableTimeout;
    private String url;

}

package com.yoyo.admin.web_manage;

import cn.hutool.core.util.StrUtil;
import com.yoyo.admin.common.constant.SysConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.*;
import java.util.Properties;

@SpringBootApplication(scanBasePackages = "com.yoyo.admin.*")
@EnableJpaRepositories(basePackages = "com.yoyo.admin.*")
@EntityScan(basePackages = "com.yoyo.admin.*")
@EnableScheduling
public class WebManageApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebManageApplication.class);

    public static void main(String[] args) throws IOException {

        // 启动SpringApplication服务
        SpringApplication.run(WebManageApplication.class, args);

        // 读取properties文件获取端口号配置
        Properties mainProperties = new Properties();
        BufferedReader mainBufferedReader = new BufferedReader(new InputStreamReader(new ClassPathResource("application.properties").getInputStream()));
        mainProperties.load(mainBufferedReader);
        String configChoose = mainProperties.getProperty("spring.profiles.active");
        Properties properties = new Properties();
        if(SysConstants.CONFIG_CHOOSE.PROD.equals(configChoose)){
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ClassPathResource("application-prod.properties").getInputStream()));
            properties.load(bufferedReader);
        }else if(SysConstants.CONFIG_CHOOSE.DEV.equals(configChoose)){
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ClassPathResource("application-dev.properties").getInputStream()));
            properties.load(bufferedReader);
        }else{
            LOGGER.error("请检查application.properties文件的spring.profiles.active配置，只允许取值prod或dev。");
        }
        String port = properties.getProperty("server.port");

        // 打印启动成功的信息
        LOGGER.info("======项目启动成功！======");
        LOGGER.info(StrUtil.format("Druid数据库连接池地址：http://localhost:{}/druid/index.html",port));
        LOGGER.info(StrUtil.format("Swagger在线接口文档地址：http://localhost:{}/doc.html",port));
        LOGGER.info(StrUtil.format("Swagger导入Postman地址：http://localhost:{}/v2/api-docs",port));

    }

}

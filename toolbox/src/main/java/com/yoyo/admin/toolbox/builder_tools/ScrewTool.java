package com.yoyo.admin.toolbox.builder_tools;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.yoyo.admin.common.constant.SysConstants;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * 数据库设计文档生成
 */
public class ScrewTool {

    static void documentGeneration() throws Exception {
        //读取properties文件获取配置信息
        Properties mainProperties = new Properties();
        BufferedReader mainBufferedReader = new BufferedReader(new FileReader("web_manage/src/main/resources/application.properties"));
        mainProperties.load(mainBufferedReader);
        String configChoose = mainProperties.getProperty("spring.profiles.active");
        Properties properties = new Properties();
        if(SysConstants.CONFIG_CHOOSE.PROD.equals(configChoose)){
            BufferedReader bufferedReader = new BufferedReader(new FileReader("web_manage/src/main/resources/application-prod.properties"));
            properties.load(bufferedReader);
        }else if(SysConstants.CONFIG_CHOOSE.DEV.equals(configChoose)){
            BufferedReader bufferedReader = new BufferedReader(new FileReader("web_manage/src/main/resources/application-dev.properties"));
            properties.load(bufferedReader);
        }else{
            throw new Exception("请检查application.properties文件的spring.profiles.active配置，只允许取值prod或dev。");
        }
        String datasourceDriver = properties.getProperty("spring.datasource.druid.driver-class-name");
        String datasourceUrl = properties.getProperty("spring.datasource.druid.url");
        String datasourceUsername = properties.getProperty("spring.datasource.druid.username");
        String datasourcePassword = properties.getProperty("spring.datasource.druid.password");
        //数据源
        HikariConfig hikariConfig = new HikariConfig();
        //数据库driver
        hikariConfig.setDriverClassName(datasourceDriver);
        //数据库URL
        hikariConfig.setJdbcUrl(datasourceUrl);
        //数据库用户名
        hikariConfig.setUsername(datasourceUsername);
        //数据库用户密码
        hikariConfig.setPassword(datasourcePassword);
        //设置可以获取tables remarks信息
        hikariConfig.addDataSourceProperty("useInformationSchema", "true");
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaximumPoolSize(5);
        DataSource dataSource = new HikariDataSource(hikariConfig);
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date date = new Date();
        //生成配置
        EngineConfig engineConfig = EngineConfig.builder()
                //生成文件路径
                .fileOutputDir("./project_doc/db_structure")
                //打开目录
                .openOutputDir(true)
                //文件类型 HTML、MD、WORD
                .fileType(EngineFileType.HTML)
                //生成模板实现
                .produceType(EngineTemplateType.freemarker)
                //自定义文件名称
                .fileName("yoyo-admin数据库设计文档--"+dateFormat.format(date)).build();
        //忽略表
        ArrayList<String> ignoreTableName = new ArrayList<>();
        //忽略表前缀
        ArrayList<String> ignorePrefix = new ArrayList<>();
        ignorePrefix.add("test_");
        ignorePrefix.add("bak_");
        //忽略表后缀
        ArrayList<String> ignoreSuffix = new ArrayList<>();
        ignoreSuffix.add("_test");
        ignoreSuffix.add("_bak");
        ProcessConfig processConfig = ProcessConfig.builder()
                //指定生成逻辑、当存在指定表、指定表前缀、指定表后缀时，将生成指定表，其余表不生成、并跳过忽略表配置
                //根据名称指定表生成
                .designatedTableName(new ArrayList<>())
                //根据表前缀生成
                .designatedTablePrefix(new ArrayList<>())
                //根据表后缀生成
                .designatedTableSuffix(new ArrayList<>())
                //忽略表名
                .ignoreTableName(ignoreTableName)
                //忽略表前缀
                .ignoreTablePrefix(ignorePrefix)
                //忽略表后缀
                .ignoreTableSuffix(ignoreSuffix).build();
        //配置
        Configuration config = Configuration.builder()
                //版本
                .version("v1.0")
                //数据源
                .dataSource(dataSource)
                //生成配置
                .engineConfig(engineConfig)
                //生成配置
                .produceConfig(processConfig)
                .build();
        //执行生成
        new DocumentationExecute(config).execute();
    }

    public static void main(String[] args) throws Exception {
        documentGeneration();
    }
}

package com.yoyo.admin.web_manage.config;

import com.fasterxml.classmate.TypeResolver;
import org.apache.commons.compress.utils.Sets;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.Operation;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * 由于 Spring Security 的登录、登出接口是通过Filter实现，导致 Swagger 无法获取其信息。
 * 这里手动将登录、登出接口注册到Swagger中，在Swagger-UI才能展示，方便调用。
 */
@Component
public class SpringSecurityApis implements ApiListingScannerPlugin {

    /**
     * Implement this method to manually add ApiDescriptions
     * 实现此方法可手动添加ApiDescriptions
     *
     * @param context - Documentation context that can be used infer documentation context
     * @return List of {@link ApiDescription}
     * @see ApiDescription
     */
    @Override
    public List<ApiDescription> apply(DocumentationContext context) {

        // 额外添加登录接口的文档
        Operation loginOperation = new OperationBuilder(new CachingOperationNameGenerator())
                .method(HttpMethod.POST)
                .summary("系统登录")
                // 接收参数格式
                .consumes(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
                // 返回参数格式
                .produces(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
                .tags(Sets.newHashSet("系统管理"))
                .uniqueId("login")
                .parameters(Collections.singletonList(
                        new ParameterBuilder()
                                .description("用户名与密码使用AES加密后得到的字符串")
                                .type(new TypeResolver().resolve(String.class))
                                .name("aes_str")
                                .parameterType("body")
                                .parameterAccess("access")
                                .required(true)
                                .modelRef(new ModelRef("text"))
                                .build()
                ))
                .responseMessages(Collections.singleton(
                        new ResponseMessageBuilder().code(200).message("请求成功")
                                .responseModel(new ModelRef(
                                        "xyz.gits.boot.common.core.response.RestResponse")
                                ).build()))
                .build();

        ApiDescription loginApiDescription = new ApiDescription("auth", "/auth/login", "登录接口",
                Collections.singletonList(loginOperation), false);

        // 额外添加退出登录接口的文档
        Operation logoutOperation = new OperationBuilder(new CachingOperationNameGenerator())
                .method(HttpMethod.POST)
                .summary("系统退出登录")
                // 接收参数格式
                .consumes(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
                // 返回参数格式
                .produces(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
                .parameters(Collections.emptyList())
                .uniqueId("logout")
                .tags(Sets.newHashSet("系统管理"))
                .responseMessages(Collections.singleton(
                        new ResponseMessageBuilder().code(200).message("请求成功")
                                .responseModel(new ModelRef(
                                        "xyz.gits.boot.common.core.response.RestResponse")
                                ).build()))
                .build();

        ApiDescription logoutApiDescription = new ApiDescription("auth", "/auth/logout", "退出登录接口",
                Collections.singletonList(logoutOperation), false);

        return Arrays.asList(loginApiDescription, logoutApiDescription);

    }

    /**
     * 是否使用此插件
     *
     * @param documentationType swagger文档类型
     * @return true 启用
     */
    @Override
    public boolean supports(DocumentationType documentationType) {
        return DocumentationType.SWAGGER_2.equals(documentationType);
    }

}

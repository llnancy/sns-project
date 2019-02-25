package org.lilu.sns.configuration;

import org.lilu.sns.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther: lilu
 * @Date: 2019/1/27
 * @Description: 拦截器配置
 */
@Component
public class InterceptorConfiguration implements WebMvcConfigurer {
    @Autowired
    private PassportInterceptor passportInterceptor;

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
    }
}
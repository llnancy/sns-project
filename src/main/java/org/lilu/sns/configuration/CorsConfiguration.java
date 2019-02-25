package org.lilu.sns.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther: lilu
 * @Date: 2019/2/8
 * @Description: 跨域请求配置
 */
@Component
public class CorsConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                /* 允许客户端写入cookie */
                .allowCredentials(true)
                /* http请求方式：get post put delete head options trace */
                .allowedMethods("GET","POST","PUT","DELETE");
    }
}
package org.indoles.memberserviceserver.global.config;

import org.indoles.memberserviceserver.global.util.LoginArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginArgumentResolver loginArgumentResolver;
    private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;


    public WebConfig(LoginArgumentResolver loginArgumentResolver, JwtAuthenticationInterceptor jwtAuthenticationInterceptor) {
        this.loginArgumentResolver = loginArgumentResolver;
        this.jwtAuthenticationInterceptor = jwtAuthenticationInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/**");
    }
}



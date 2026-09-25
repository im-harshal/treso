package com.harshal.treso.config;

import com.harshal.treso.interceptor.IdempotencyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private IdempotencyInterceptor idempotencyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Apply idempotency check only to the expenses endpoint
        registry.addInterceptor(idempotencyInterceptor).addPathPatterns("/api/expenses");
    }
}

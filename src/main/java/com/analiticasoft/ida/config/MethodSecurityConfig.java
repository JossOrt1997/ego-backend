package com.analiticasoft.ida.config;

import com.analiticasoft.ida.security.CustomPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class MethodSecurityConfig {

    private final CustomPermissionEvaluator customPermissionEvaluator;

    @Bean
    public MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        // Aquí le pasamos nuestro evaluador personalizado
        expressionHandler.setPermissionEvaluator(customPermissionEvaluator);
        return expressionHandler;
    }
}
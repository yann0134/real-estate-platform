package com.realestate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowed-methods}")
    private String[] allowedMethods;

    @Value("${app.cors.allowed-headers}")
    private String[] allowedHeaders;

    @Value("${app.cors.allow-credentials}")
    private boolean allowCredentials;

    /*@Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Configuration des origines autorisées
        for (String origin : allowedOrigins) {
            config.addAllowedOrigin(origin);
        }
        
        // Configuration des méthodes HTTP autorisées
        for (String method : allowedMethods) {
            config.addAllowedMethod(method.trim());
        }
        
        // Configuration des en-têtes autorisés
        for (String header : allowedHeaders) {
            config.addAllowedHeader(header.trim());
        }
        
        // Autoriser les credentials
        config.setAllowCredentials(allowCredentials);
        
        // Configuration pour toutes les routes
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }*/


    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Set allowed origins from properties
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));

        // Set allowed methods from properties
        config.setAllowedMethods(Arrays.asList(allowedMethods));

        // Set allowed headers from properties
        config.setAllowedHeaders(Arrays.asList(allowedHeaders));

        // Allow credentials
        config.setAllowCredentials(allowCredentials);

        // Set exposed headers
        config.setExposedHeaders(Arrays.asList("Authorization", "XSRF-TOKEN"));

        // Configuration for all routes
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

}

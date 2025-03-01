package com.trails_art.trails.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CrossOriginConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Apply to all endpoints
                        .allowedOrigins("http://localhost:5432", "http://localhost:3000", "http://localhost:8080", "http://localhost:5500")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow these methods
                        .allowedHeaders("*") // Allow all headers
                        .allowCredentials(true); // Allow authentication (cookies, tokens)
            }
        };
    }
}

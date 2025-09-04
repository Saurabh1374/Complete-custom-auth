package com.kitchome.auth.authentication;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // apply CORS to all endpoints
                .allowedOrigins("http://localhost:5173", "https://your-frontend.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .exposedHeaders("Authorization")   // 👈 if you want frontend to read JWT from headers
                .allowCredentials(true)
                .maxAge(3600); // cache preflight response for 1 hour
    }
}

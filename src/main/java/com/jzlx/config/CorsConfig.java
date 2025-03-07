package com.jzlx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry resistry){
        resistry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .maxAge(36000)
                .allowedHeaders("*");
    }
}

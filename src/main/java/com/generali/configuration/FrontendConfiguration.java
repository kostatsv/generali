package com.generali.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FrontendConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/js/*").addResourceLocations("classpath:/public/static/js/");
        registry.addResourceHandler("/static/css/*").addResourceLocations("classpath:/public/static/css/");
        registry.addResourceHandler("/static/img/*").addResourceLocations("classpath:/public/static/img/");

        registry.addResourceHandler("/static/*").addResourceLocations("classpath:/public/static/");

        registry.addResourceHandler("index.html").addResourceLocations("classpath:/public/index.html");

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/public/static/fonts/");
    }

}

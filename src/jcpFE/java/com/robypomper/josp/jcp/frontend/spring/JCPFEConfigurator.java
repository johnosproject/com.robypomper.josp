package com.robypomper.josp.jcp.frontend.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


@Configuration
public class JCPFEConfigurator extends HandlerInterceptorAdapter implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/frontend").setViewName("forward:/frontend/index.html");
        registry.addViewController("/frontend/").setViewName("forward:/frontend/index.html");
        registry.addViewController("/frontend/{spring:\\w+}").setViewName("forward:/frontend/index.html");
        registry.addViewController("/frontend/{spring:\\w+}/**{spring:?!(\\.js|\\.css|\\.png)$}").setViewName("forward:/frontend/index.html");
        registry.addViewController("/frontend/**/{spring:\\w+}").setViewName("forward:/frontend/index.html");
    }

}

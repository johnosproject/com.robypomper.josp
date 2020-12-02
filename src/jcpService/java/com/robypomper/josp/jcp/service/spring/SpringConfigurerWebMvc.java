package com.robypomper.josp.jcp.service.spring;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
@Profile({"auth"})
public class SpringConfigurerWebMvc implements WebMvcConfigurer {

    @Bean
    public MappingJackson2HttpMessageConverter jacksonConverter() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new Hibernate5Module());
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Bean
    public StringHttpMessageConverter stringMessageConverter() {
        return new StringHttpMessageConverter();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jacksonConverter());
        converters.add(stringMessageConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        assert registry != null;
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        String[] CLASSPATH_RESOURCE_LOCATIONS = {
                "classpath:/META-INF/resources/", "classpath:/resources/",
                "classpath:/static/", "classpath:/public/"};
        registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }

}

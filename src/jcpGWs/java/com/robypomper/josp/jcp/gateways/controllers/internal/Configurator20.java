package com.robypomper.josp.jcp.gateways.controllers.internal;

import com.robypomper.josp.jcp.defs.gateways.internal.Versions;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.plugins.Docket;


/**
 * JCP Gateways (Internal)
 */
@Configuration(Versions.API_NAME)
public class Configurator20 {

    @Autowired
    private SwaggerConfigurer swagger;

    // Docs configs

    @Bean("swaggerConfig" + Versions.API_NAME)
    public Docket swaggerConfig() {
        return swagger.createAPIsGroup(Versions.API_NAME, Versions.API_PATH_BASE, Versions.VER_JCP_APIs_2_0, Versions.API_NAME);
    }

}
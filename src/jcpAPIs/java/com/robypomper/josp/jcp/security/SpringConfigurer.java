package com.robypomper.josp.jcp.security;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
@KeycloakConfiguration
public class SpringConfigurer extends KeycloakWebSecurityConfigurerAdapter {

    /** List of allowed public paths. */
    @Value("${oauth2.resource.public-paths}")
    private String[] mPublicPaths;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // Enable HTTPs
                .requiresChannel()
                .anyRequest()
                .requiresSecure()

                // Enable CSRF token as cookie
//                .and()
//                .csrf()
//                    .disable()
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

                // Enable Auth checks
                .and()
                .authorizeRequests()
                .antMatchers(mPublicPaths).permitAll()
                .anyRequest().authenticated()

                // Setup OAuth2 ResServer
                .and()
                .oauth2ResourceServer()
                .jwt();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        SimpleAuthorityMapper grantedAuthorityMapper = new SimpleAuthorityMapper();
        grantedAuthorityMapper.setPrefix("ROLE_");

        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(grantedAuthorityMapper);
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

}
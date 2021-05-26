package com.robypomper.josp.jcp.base.spring;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("sessions")
public class SpringSessionsConfigurer {

    // Internal vars

    @Value("${sessions.sameSiteNone:false}")
    private boolean isSameSiteNone;


    @Bean
    public TomcatContextCustomizer sameSiteCookiesConfig() {
        if (isSameSiteNone)
            return context -> {
                final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
                cookieProcessor.setSameSiteCookies(SameSiteCookies.NONE.getValue());
                context.setCookieProcessor(cookieProcessor);
            };
        return context -> {};
    }

}

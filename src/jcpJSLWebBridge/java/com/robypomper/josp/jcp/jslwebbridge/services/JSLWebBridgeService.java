package com.robypomper.josp.jcp.jslwebbridge.services;

import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLAlreadyInitForSessionException;
import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLErrorOnInitException;
import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLNotInitForSessionException;
import com.robypomper.josp.jcp.jslwebbridge.webbridge.JSLParams;
import com.robypomper.josp.jcp.jslwebbridge.webbridge.JSLWebBridge;
import com.robypomper.josp.jsl.JSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Service
public class JSLWebBridgeService {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLWebBridgeService.class);
    private final JSLWebBridge webBridge;


    // Constructors

    @Autowired
    protected JSLWebBridgeService(JSLParams jslParams,
                                  @Value("${jcp.jsl.remove.delay:900}") int jslRemoveScheduledDelaySeconds,
                                  @Value("${jcp.jsl.heartbeat.delay:60}") int heartbeatTimerDelaySeconds) {
        webBridge = new JSLWebBridge(jslParams, jslRemoveScheduledDelaySeconds, heartbeatTimerDelaySeconds);
    }


    @PreDestroy
    public void destroy() {
        webBridge.destroyAll();
        log.trace("JSL webBridge service destroyed");
    }


    // Getters

    public JSL getJSL(String sessionId) throws JSLNotInitForSessionException {
        return webBridge.getJSL(sessionId);
    }

    public SseEmitter getJSLEmitter(String sessionId) throws JSLNotInitForSessionException {
        return webBridge.getJSLEmitter(sessionId);
    }

    // Creators

    public JSL initJSL(String sessionId, String clientId, String clientSecret) throws JSLAlreadyInitForSessionException, JSLErrorOnInitException {
        return webBridge.initJSL(sessionId, clientId, clientSecret);
    }


    // Sessions

    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> sessionListenerWithMetrics() {
        ServletListenerRegistrationBean<HttpSessionListener> listenerRegBean = new ServletListenerRegistrationBean<>();

        listenerRegBean.setListener(new HttpSessionListener() {
            public void sessionCreated(HttpSessionEvent se) {
            }

            public void sessionDestroyed(HttpSessionEvent se) {
                String sessionId = se.getSession().getId();
                log.info(String.format("Terminated session '%s'", sessionId));
                webBridge.destroyJSL(sessionId);
            }

        });

        return listenerRegBean;
    }

}

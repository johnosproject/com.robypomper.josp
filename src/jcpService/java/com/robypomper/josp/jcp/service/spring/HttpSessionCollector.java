package com.robypomper.josp.jcp.service.spring;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("auth-none")
public class HttpSessionCollector implements HttpSessionListener {

    private static final Map<String, HttpSession> sessions = new HashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        sessions.put(session.getId(), session);
    }


    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        sessions.remove(event.getSession().getId());
    }

    public static HttpSession find(String sessionId) {
        return sessions.get(sessionId);
    }

    public static Map<String,HttpSession> getAllSessions() {
        return sessions;
    }

}
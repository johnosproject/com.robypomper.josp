package com.robypomper.josp.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RequestsMaker {

    public static void execAndPrintReq(OAuth20Service service, OAuth2AccessToken accessToken, String url)
            throws InterruptedException, ExecutionException, IOException {
        execAndPrintReq(service, accessToken, Verb.GET, url);
    }

    public static void execAndPrintReq(OAuth20Service service, OAuth2AccessToken accessToken, String url, String body)
            throws InterruptedException, ExecutionException, IOException {
        execAndPrintReq(service, accessToken, Verb.GET, url, body);
    }

    public static void execAndPrintReq(OAuth20Service service, OAuth2AccessToken accessToken, String url, Map<String, String> headers)
            throws InterruptedException, ExecutionException, IOException {
        execAndPrintReq(service, accessToken, Verb.GET, url, headers);
    }

    public static void execAndPrintReq(OAuth20Service service, OAuth2AccessToken accessToken, String url, String body, Map<String, String> headers)
            throws InterruptedException, ExecutionException, IOException {
        execAndPrintReq(service, accessToken, Verb.GET, url, body, headers);
    }

    public static void execAndPrintReq(OAuth20Service service, OAuth2AccessToken accessToken, Verb method, String url)
            throws InterruptedException, ExecutionException, IOException {
        execAndPrintReq(service, accessToken, method, url, null, new HashMap<>());
    }

    public static void execAndPrintReq(OAuth20Service service, OAuth2AccessToken accessToken, Verb method, String url, String body)
            throws InterruptedException, ExecutionException, IOException {
        execAndPrintReq(service, accessToken, method, url, body, new HashMap<>());
    }

    public static void execAndPrintReq(OAuth20Service service, OAuth2AccessToken accessToken, Verb method, String url, Map<String, String> headers)
            throws InterruptedException, ExecutionException, IOException {
        execAndPrintReq(service, accessToken, method, url, null, headers);
    }

    public static void execAndPrintReq(OAuth20Service service, OAuth2AccessToken accessToken, Verb method, String url, String body, Map<String, String> headers)
            throws InterruptedException, ExecutionException, IOException {
        final OAuthRequest req = new OAuthRequest(method, url);
        if (body != null)
            req.setPayload(body);
        for (Map.Entry<String, String> header : headers.entrySet())
            req.addHeader(header.getKey(), header.getValue());
        service.signRequest(accessToken, req);
        try (Response response = service.execute(req)) {
            System.out.println(String.format("GET %-65s => [%s] %s", url, response.getCode(), response.getBody()));
        }
    }


    public static <T> T execAndGetReq(OAuth20Service service, OAuth2AccessToken accessToken, String url, Class<T> reqObj)
            throws InterruptedException, ExecutionException, IOException {
        return execAndGetReq(service, accessToken, Verb.GET, url, reqObj);
    }

    public static <T> T execAndGetReq(OAuth20Service service, OAuth2AccessToken accessToken, String url, String body, Class<T> reqObj)
            throws InterruptedException, ExecutionException, IOException {
        return execAndGetReq(service, accessToken, Verb.GET, url, body, reqObj);
    }

    public static <T> T execAndGetReq(OAuth20Service service, OAuth2AccessToken accessToken, String url, Map<String, String> headers, Class<T> reqObj)
            throws InterruptedException, ExecutionException, IOException {
        return execAndGetReq(service, accessToken, Verb.GET, url, headers, reqObj);
    }

    public static <T> T execAndGetReq(OAuth20Service service, OAuth2AccessToken accessToken, String url, String body, Map<String, String> headers, Class<T> reqObj)
            throws InterruptedException, ExecutionException, IOException {
        return execAndGetReq(service, accessToken, Verb.GET, url, body, headers, reqObj);
    }

    public static <T> T execAndGetReq(OAuth20Service service, OAuth2AccessToken accessToken, Verb method, String url, Class<T> reqObj)
            throws InterruptedException, ExecutionException, IOException {
        return execAndGetReq(service, accessToken, method, url, new HashMap<>(), reqObj);
    }

    public static <T> T execAndGetReq(OAuth20Service service, OAuth2AccessToken accessToken, Verb method, String url, String body, Class<T> reqObj)
            throws InterruptedException, ExecutionException, IOException {
        return execAndGetReq(service, accessToken, method, url, body, new HashMap<>(), reqObj);
    }

    public static <T> T execAndGetReq(OAuth20Service service, OAuth2AccessToken accessToken, Verb method, String url, Map<String, String> headers, Class<T> reqObj)
            throws InterruptedException, ExecutionException, IOException {
        return execAndGetReq(service, accessToken, method, url, null, headers, reqObj);
    }

    public static <T> T execAndGetReq(OAuth20Service service, OAuth2AccessToken accessToken, Verb method, String url, String body, Map<String, String> headers, Class<T> reqObj)
            throws InterruptedException, ExecutionException, IOException {
        final OAuthRequest req = new OAuthRequest(method, url);
        if (body != null)
            req.setPayload(body);
        for (Map.Entry<String, String> header : headers.entrySet())
            req.addHeader(header.getKey(), header.getValue());
        injectSession(req);
        service.signRequest(accessToken, req);
        try (Response response = service.execute(req)) {
            storeSession(response);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), reqObj);
        }
    }

    private static String sessionId = null;

    private static void injectSession(OAuthRequest request) {
        if (sessionId != null)
            request.addHeader("Cookie", sessionId);
    }

    private static void storeSession(Response response) {
        String setCookie = response.getHeader("Set-Cookie");
        if (setCookie == null)
            return;

        if (setCookie.contains("JSESSIONID"))
            sessionId = setCookie;
    }

}

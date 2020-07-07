package com.robypomper.josp.core.jcpclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.KeycloakApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuth2AccessTokenErrorResponse;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.robypomper.java.JavaSSLIgnoreChecks;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


public class DefaultJCPClient2 implements JCPClient2 {

    public static final String TH_CONNECTION_NAME = "_JCP_CONNECTION_";
    public static final String HEAD_COOKIE = "Cookie";
    public static final String HEAD_SET_COOKIE = "Set-Cookie";
    public static final String SESSION_KEY = "JSESSIONID";


    private final OAuth20Service service;
    public final String baseUrlAuth;
    public final String baseUrlAPIs;
    private OAuth2AccessToken accessToken = null;

    private boolean cliCred_isConnected = false;
    private String cliCred_refreshToken = null;

    private boolean authCode_isConnected = false;
    private String authCode_refreshToken = null;
    private String authCode_loginCode = null;


    private final List<ConnectListener> connectListeners = new ArrayList<>();
    private final List<DisconnectListener> disconnectListeners = new ArrayList<>();


    private Timer connectionTimer = null;
    public final int connectionTimerDelaySeconds;


    private final List<LoginListener> loginListeners = new ArrayList<>();


    private final Map<String, String> defaultHeaders = new HashMap<>();
    private String sessionId = null;


    // Constructor

    public DefaultJCPClient2(String clientId, String clientSecret, String apisBaseUrl, String authBaseUrl, String authScopes, String authCallBack, String authRealm) {
        this(clientId, clientSecret, apisBaseUrl, authBaseUrl, authScopes, authCallBack, authRealm, null, 30);
    }

    public DefaultJCPClient2(String clientId, String clientSecret, String apisBaseUrl, String authBaseUrl, String authScopes, String authCallBack, String authRealm, int connectionRetrySeconds) {
        this(clientId, clientSecret, apisBaseUrl, authBaseUrl, authScopes, authCallBack, authRealm, null, connectionRetrySeconds);
    }

    public DefaultJCPClient2(String clientId, String clientSecret, String apisBaseUrl, String authBaseUrl, String authScopes, String authCallBack, String authRealm, String authCodeRefreshToken) {
        this(clientId, clientSecret, apisBaseUrl, authBaseUrl, authScopes, authCallBack, authRealm, authCodeRefreshToken, 30);
    }

    public DefaultJCPClient2(String clientId, String clientSecret, String apisBaseUrl, String authBaseUrl, String authScopes, String authCallBack, String authRealm, String authCodeRefreshToken, int connectionRetrySeconds) {
        service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .defaultScope(authScopes)
                .callback(authCallBack)
                .build(KeycloakApi.instance("https://" + authBaseUrl, authRealm));
        baseUrlAuth = authBaseUrl;
        baseUrlAPIs = apisBaseUrl;
        if (authCodeRefreshToken != null && !authCodeRefreshToken.isEmpty())
            authCode_refreshToken = authCodeRefreshToken;
        connectionTimerDelaySeconds = connectionRetrySeconds;
    }


    // Connection

    @Override
    public boolean isConnected() {
        return cliCred_isConnected || authCode_isConnected;
    }

    @Override
    public void connect() throws JCPNotReachableException, ConnectionException, AuthenticationException {
        if (isConnected())
            return;

        if (!checkServerReachability(baseUrlAuth))
            throw new JCPNotReachableException(String.format("Error connecting to JCP because '%s' (Auth's url) not reachable", baseUrlAuth));
        if (!checkServerReachability(baseUrlAPIs))
            throw new JCPNotReachableException(String.format("Error connecting to JCP because '%s' (Auth's url) not reachable", baseUrlAPIs));

        if (isClientCredentialFlowEnabled()) {
            accessToken = getAccessTokenCliCredFlow(service);
            cliCred_refreshToken = accessToken.getRefreshToken();
            cliCred_isConnected = true;

            authCode_isConnected = false;
            authCode_refreshToken = null;
            authCode_loginCode = null;

        } else if (isAuthCodeFlowEnabled()) {
            try {
                accessToken = getAccessTokenAuthCodeFlow(service, authCode_refreshToken, authCode_loginCode);
                authCode_refreshToken = accessToken.getRefreshToken();
                authCode_isConnected = true;
                authCode_loginCode = null;
                emitLoggedIn();

                cliCred_isConnected = false;
                cliCred_refreshToken = null;

            } catch (AuthenticationException e) {
                authCode_refreshToken = null;
                authCode_loginCode = null;
                throw e;
            }
        }

        emitConnected();
    }

    @Override
    public void disconnect() {
        if (!isConnected() && !isConnecting())
            return;

        if (isConnecting()) {
            stopConnectionTimer();
            return;
        }

        cliCred_isConnected = false;
        authCode_isConnected = false;
        accessToken = null;

        emitDisconnected();
    }

    private boolean checkServerReachability(String urlBase) {
        try {
            String host = urlBase.substring(0, urlBase.indexOf(':'));
            int port = Integer.parseInt(urlBase.substring(urlBase.indexOf(':') + 1));
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 30);
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    private static OAuth2AccessToken getAccessTokenCliCredFlow(OAuth20Service service) throws ConnectionException {
        try {
            try {
                return service.getAccessTokenClientCredentialsGrant();

            } catch (SSLHandshakeException e) {

                try {
                    JavaSSLIgnoreChecks.disableSSLChecks(JavaSSLIgnoreChecks.LOCALHOST);
                    return service.getAccessTokenClientCredentialsGrant();

                } catch (SSLHandshakeException e1) {
                    throw new ConnectionException("Error connecting to JCP because SSL handshaking failed", e1);
                }
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new ConnectionException("Error connecting to JCP because can't get the access token for Client Credentials flow", e);

        } catch (JavaSSLIgnoreChecks.JavaSSLIgnoreChecksException e) {
            throw new ConnectionException("Error connecting to JCP because can't ignore LOCALHOST's certificate checks", e);
        }
    }

    private static OAuth2AccessToken getAccessTokenAuthCodeFlow(OAuth20Service service, String refreshToken, String loginCode) throws ConnectionException, AuthenticationException {
        if (loginCode == null && refreshToken == null)
            throw new AuthenticationException("Error connecting to JCP because Login Code nor Refresh Token were set for Auth Code flow");

        try {
            try {
                if (loginCode != null)
                    return service.getAccessToken(loginCode);
                else
                    return service.refreshAccessToken(refreshToken);

            } catch (SSLHandshakeException e) {

                try {
                    JavaSSLIgnoreChecks.disableSSLChecks(JavaSSLIgnoreChecks.LOCALHOST);
                    if (loginCode != null)
                        return service.getAccessToken(loginCode);
                    else
                        return service.refreshAccessToken(refreshToken);

                } catch (SSLHandshakeException e1) {
                    throw new ConnectionException("Error connecting to JCP because SSL handshaking failed", e1);
                }
            }

        } catch (OAuth2AccessTokenErrorResponse e) {
            throw new AuthenticationException(String.format("Error connecting to JCP because %s", e.getMessage()), e);

        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new ConnectionException("Error connecting to JCP because can't get the access token for Auth Code flow", e);

        } catch (JavaSSLIgnoreChecks.JavaSSLIgnoreChecksException e) {
            throw new ConnectionException("Error connecting to JCP because can't ignore LOCALHOST's certificate checks", e);
        }
    }

    protected String getAuthCodeRefreshToken() {
        return authCode_refreshToken;
    }


    // Connection listeners

    @Override
    public void addConnectListener(ConnectListener listener) {
        connectListeners.add(listener);
    }

    @Override
    public void removeConnectListener(ConnectListener listener) {
        connectListeners.remove(listener);
    }

    @Override
    public void addDisconnectListener(DisconnectListener listener) {
        disconnectListeners.add(listener);
    }

    @Override
    public void removeDisconnectListener(DisconnectListener listener) {
        disconnectListeners.remove(listener);
    }

    private void emitConnected() {
        List<ConnectListener> tmpList = new ArrayList<>(connectListeners);
        for (ConnectListener l : tmpList)
            l.onConnected(this);
    }

    private void emitConnectionFailed(Throwable t) {
        List<ConnectListener> tmpList = new ArrayList<>(connectListeners);
        for (ConnectListener l : tmpList)
            l.onConnectionFailed(this, t);
    }

    private void emitDisconnected() {
        List<DisconnectListener> tmpList = new ArrayList<>(disconnectListeners);
        for (DisconnectListener l : tmpList)
            l.onDisconnected(this);
    }


    // Connection timer

    @Override
    public boolean isConnecting() {
        return connectionTimer != null;
    }

    @Override
    public void startConnectionTimer() {
        if (isConnected() || isConnecting())
            return;

        connectionTimer = new Timer(true);
        connectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setName(TH_CONNECTION_NAME);

                try {
                    DefaultJCPClient2.this.connect();
                    if (isConnected())
                        stopConnectionTimer();

                } catch (JCPNotReachableException ignore) {

                } catch (ConnectionException | AuthenticationException e) {
                    //log.warn(Mrk_JOD.JOD_COMM_JCPCL, String.format("Error on authenticate JCPClient to JCP because %s", e.getMessage()), e);
                    stopConnectionTimer();
                    emitConnectionFailed(e);
                }

            }
        }, 0, connectionTimerDelaySeconds * 1000);
    }

    @Override
    public void stopConnectionTimer() {
        connectionTimer.cancel();
        connectionTimer = null;
    }


    // Authentication flows

    @Override
    public boolean isClientCredentialFlowEnabled() {
        return !isAuthCodeFlowEnabled();
    }

    @Override
    public boolean isAuthCodeFlowEnabled() {
        return authCode_refreshToken != null || authCode_loginCode != null;
    }


    // Login

    @Override
    public boolean isLoggedIn() {
        return isAuthCodeFlowEnabled() && isConnected();
    }

    @Override
    public String getLoginUrl() {
        return service.getAuthorizationUrl();
    }

    @Override
    public void setLoginCode(String loginCode) {
        authCode_loginCode = loginCode;
    }

    @Override
    public void userLogout() {
        if (!isLoggedIn())
            return;

        disconnect();
        authCode_refreshToken = null;
        emitLoggedOut();

        try {
            connect();
        } catch (JCPNotReachableException | ConnectionException | AuthenticationException ignore) { }
    }


    // Login listeners

    @Override
    public void addLoginListener(LoginListener listener) {
        loginListeners.add(listener);
    }

    @Override
    public void removeLoginListener(LoginListener listener) {
        loginListeners.remove(listener);
    }

    private void emitLoggedIn() {
        List<LoginListener> tmpList = new ArrayList<>(loginListeners);
        for (LoginListener l : tmpList)
            l.onLogin(this);
    }

    private void emitLoggedOut() {
        List<LoginListener> tmpList = new ArrayList<>(loginListeners);
        for (LoginListener l : tmpList)
            l.onLogout(this);
    }


    // Headers and sessions

    @Override
    public void addDefaultHeader(String headerName, String headerValue) {
        defaultHeaders.put(headerName, headerValue);
    }

    @Override
    public void removeDefaultHeader(String headerName) {
        defaultHeaders.remove(headerName);
    }

    private void injectDefaultHeaders(OAuthRequest request) {
        for (Map.Entry<String, String> h : defaultHeaders.entrySet())
            request.addHeader(h.getKey(), h.getValue());
    }

    @Override
    public boolean isSessionSet() {
        return sessionId != null;
    }

    private void injectSession(OAuthRequest request) {
        if (sessionId != null)
            request.addHeader(HEAD_COOKIE, sessionId);
    }

    private void storeSession(Response response) {
        String setCookie = response.getHeader(HEAD_SET_COOKIE);
        if (setCookie == null)
            return;

        if (setCookie.contains(SESSION_KEY))
            sessionId = setCookie;
    }

    private void cleanSession() {
        sessionId = null;
    }


    // Exec requests

    @Override
    public void execReq(Verb reqType, String path) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(false, reqType, path, null, new HashMap<>(), false);
    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(toAuth, reqType, path, null, new HashMap<>(), false);
    }

    @Override
    public void execReq(Verb reqType, String path, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(false, reqType, path, null, new HashMap<>(), secure);
    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(toAuth, reqType, path, null, new HashMap<>(), secure);
    }

    @Override
    public void execReq(Verb reqType, String path, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(false, reqType, path, null, params, secure);
    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(toAuth, reqType, path, null, params, secure);
    }

    @Override
    public void execReq(Verb reqType, String path, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(false, reqType, path, null, objParam, secure);
    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(toAuth, reqType, path, null, objParam, secure);
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        return execReq(false, reqType, path, reqObject, new HashMap<>(), secure);
    }

    @Override
    public <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        return execReq(toAuth, reqType, path, reqObject, new HashMap<>(), secure);
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        return execReq(false, reqType, path, reqObject, (Object) params, secure);
    }

    @Override
    public <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        return execReq(toAuth, reqType, path, reqObject, (Object) params, secure);
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        return execReq(false, reqType, path, reqObject, (Object) objParam, secure);
    }

    public <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        if (!isConnected())
            throw new ConnectionException(String.format("Error on exec request '[%s] %s' because not connected to JCP", reqType, path));

        if (reqType == Verb.GET) {
            if (objParam instanceof Map)
                path = prepareGetPath(path, (Map<String, String>) objParam);
            else
                throw new RequestException(String.format("Error on exec request '[%s] %s' because GET request must give a Map<String,String> as parameter (get %s)", reqType, path, objParam.getClass().getSimpleName()));
        }

        OAuthRequest request;
        Response response;
        try {
            if (reqType == Verb.GET || objParam == null)
                request = prepareRequest(toAuth, reqType, path, secure);
            else
                request = prepareRequest(reqType, path, objParam, secure);
            injectDefaultHeaders(request);
            injectSession(request);
            response = service.execute(request);

            if (response.getCode() == 401) {
                try {
                    if (isClientCredentialFlowEnabled())
                        accessToken = service.refreshAccessToken(cliCred_refreshToken);
                    else if (isAuthCodeFlowEnabled())
                        accessToken = service.refreshAccessToken(authCode_refreshToken);

                } catch (OAuth2AccessTokenErrorResponse e) {
                    e.printStackTrace();

                    if (isClientCredentialFlowEnabled())
                        accessToken = getAccessTokenCliCredFlow(service);
                    if (isAuthCodeFlowEnabled()) {
                        emitLoggedOut();
                        try {
                            accessToken = getAccessTokenAuthCodeFlow(service, authCode_refreshToken, authCode_loginCode);
                            emitLoggedIn();

                        } catch (AuthenticationException e1) {
                            throw e1;
                        }
                    }
                }

                request = prepareRequest(toAuth, reqType, path, secure);
                injectDefaultHeaders(request);
                injectSession(request);
                response = service.execute(request);
            }

            storeSession(response);
            if (response.getCode() != 200)
                throwErrorCodes(response, path, secure);

        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RequestException(String.format("Error on exec request '[%s] %s' because %s", reqType, path, e.getMessage()), e);
        }

        if (reqObject == null)
            return null;
        String body = extractBody(response, path, secure);
        if (reqObject.equals(String.class))
            return (T) body;

        return parseJSON(body, reqObject, path, secure);
    }

    private String prepareGetPath(String path, Map<String, String> params) {
        if (params.size() == 0)
            return path;

        StringBuilder fullUrl = new StringBuilder(path + "?");
        for (Map.Entry<String, String> pair : params.entrySet())
            fullUrl.append(pair.getKey()).append("=").append(pair.getValue()).append("&");

        return fullUrl.toString();
    }

    private OAuthRequest prepareRequest(boolean toAuth, Verb reqType, String path, boolean secure) {
        String fullUrl = secure ? "https://" : "http://";
        fullUrl += toAuth ? baseUrlAuth : baseUrlAPIs;
        fullUrl += path;
        OAuthRequest req = new OAuthRequest(reqType, fullUrl);
        service.signRequest(accessToken, req);
        return req;
    }

    private OAuthRequest prepareRequest(Verb reqType, String path, Object param, boolean secure) throws RequestException {
        String fullUrl = secure ? "https://" : "http://";
        fullUrl += baseUrlAPIs + path;
        OAuthRequest req = new OAuthRequest(reqType, fullUrl);
        req.addHeader("Content-Type", "application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        try {
            req.setPayload(mapper.writeValueAsString(param));

        } catch (JsonProcessingException e) {
            throw new RequestException(String.format("Error on prepare request '[%s] %s' because can't serialize param to json (%s)", reqType, path, e.getMessage()), e);
        }

        service.signRequest(accessToken, req);
        return req;
    }

    private void throwErrorCodes(Response response, String path, boolean secure) throws ResponseException {
        switch (response.getCode()) {
            case 200:
                break;
            case 403:
                throw new NotAuthorized_403(path, secure);
            case 404:
                throw new NotFound_404(path, secure);
            case 409:
                throw new Conflict_409(path, secure);
            default:
                throw new Error_Code(path, secure, response.getCode());
        }
    }

    private String extractBody(Response response, String path, boolean secure) throws ResponseParsingException {
        try {
            String body = response.getBody();
            if (body.startsWith("\""))
                body = body.substring(1);
            if (body.endsWith("\""))
                body = body.substring(0, body.length() - 1);
            return body;

        } catch (IOException e) {
            throw new ResponseParsingException(path, secure, e);
        }
    }

    private <T> T parseJSON(String body, Class<T> reqObject, String path, boolean secure) throws ResponseParsingException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(body, reqObject);

        } catch (IOException e) {
            throw new ResponseParsingException(path, secure, e);
        }
    }

}

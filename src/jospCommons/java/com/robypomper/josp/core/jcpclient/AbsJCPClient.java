package com.robypomper.josp.core.jcpclient;

import com.fasterxml.jackson.core.JsonParseException;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * First implementation of {@link JCPClient} interface.
 * <p>
 * This class implements generic JCPClient methods and provide an authentication
 * flow injection via a sub class ({@link JCPClient_AuthFlow} and
 * {@link JCPClient_CliCredFlow}).
 * <p>
 * In addition this class provide the static protected method
 * {@link #disableSSLChecks()}, that can be used by sub classes to disable SSL
 * hostname checks and certificate validations only on localhost.
 */
public abstract class AbsJCPClient implements JCPClient {

    // Class constants

    public static final String HEAD_COOKIE = "Cookie";
    public static final String HEAD_SET_COOKIE = "Set-Cookie";
    public static final String SESSION_KEY = "JSESSIONID";


    // Private vars

    private final JCPConfigs configs;
    private boolean connected = false;
    private OAuth20Service service = null;
    private OAuth2AccessToken accessToken = null;
    private String sessionId = null;


    // Constructor

    /**
     * Default constructor.
     *
     * @param configs     configs to use for JCPClient creation.
     * @param autoConnect if <code>true</code>, then the client will connect to
     *                    JCP immediately after clienti initialization.
     */
    protected AbsJCPClient(JCPConfigs configs, boolean autoConnect) {
        this.configs = configs;

        if (autoConnect) {
            tryConnect();
        }
    }


    // Mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws ConnectionException {
        if (isConnected())
            return;

        System.out.println("DEB: JCP Client connecting...");
        // Setup OAuth2 layer
        try {
            service = new ServiceBuilder(configs.getClientId())
                    .apiSecret(configs.getClientSecrets())
                    .defaultScope(configs.getScopes())
                    .callback(configs.getCallback())
                    .build(KeycloakApi.instance(configs.getBaseUrl(), configs.getRealm()));
        } catch (IllegalArgumentException e) {
            throw new ConnectionException("Wrong JCP configurations.", e);
        }

        // Start Auth flow
        try {
            accessToken = getAccessToken(service);
        } catch (SSLHandshakeException e) {
            throw new ConnectionException("Error on SSL JCP host checks.", e);
        } catch (JsonParseException e) {
            throw new ConnectionException("Token response from JCP not valid, check JCP url.", e);
        } catch (OAuth2AccessTokenErrorResponse e) {
            throw new ConnectionException("Client not authorized to access to JCP resources.", e);
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new ConnectionException(String.format("Error on getting access token from JCP: '%s'", e.getMessage()), e);
        }

        connected = true;
        System.out.println("DEB: JCP Client connected");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        if (!isConnected())
            return;

        service = null;
        accessToken = null;

        connected = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tryConnect() {
        try {
            connect();
        } catch (ConnectionException e) {
            System.out.println(String.format("WAR: Can't connect to the JCP because: '%s'", e.getMessage()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tryDisconnect() {
        disconnect();
    }


    // Get requests

    /**
     * {@inheritDoc}
     */
    @Override
    public void execGetReq(String url, boolean secure) throws RequestException, ConnectionException {
        execGetReq(url, null, new HashMap<String, String>(), secure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execGetReq(String url, Map<String, String> params, boolean secure) throws RequestException, ConnectionException {
        execGetReq(url, null, params, secure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T execGetReq(String url, Class<T> reqObject, boolean secure) throws RequestException, ConnectionException {
        return execGetReq(url, reqObject, new HashMap<String, String>(), secure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T execGetReq(String url, Class<T> reqObject, Map<String, String> params, boolean secure) throws RequestException, ConnectionException {
        if (!isConnected())
            throw new ConnectionException("Client not connected");

        // Send request
        String reqUrl = prepareGetUrl(url, params);
        OAuthRequest request = prepareGetRequest(reqUrl);
        injectSession(request);
        Response response = null;
        try {
            response = service.execute(request);
            storeSession(response);
            checkErrorCodes(response, reqUrl, secure);
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RequestException(String.format("Error on execute request '%s' to JCP.", url), e);
        }

        // Parsing response
        String body = extractBody(response, reqUrl, secure);
        if (reqObject == null)
            return null;
        if (reqObject.equals(String.class))
            return (T) body;
        return parseJSON(body, reqObject, reqUrl, secure);
    }


    // Post requests

    /**
     * {@inheritDoc}
     */
    @Override
    public void execPostReq(String url, boolean secure) throws RequestException, ConnectionException {
        execPostReq(url, null, new HashMap<>(), secure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execPostReq(String url, Object param, boolean secure) throws RequestException, ConnectionException {
        execPostReq(url, null, param, secure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T execPostReq(String url, Class<T> reqObject, boolean secure) throws RequestException, ConnectionException {
        return execPostReq(url, reqObject, new HashMap<>(), secure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T execPostReq(String url, Class<T> reqObject, Object param, boolean secure) throws RequestException, ConnectionException {
        if (!isConnected())
            throw new ConnectionException("Client not connected");

        // Send request
        String reqUrl = url;
        OAuthRequest request = preparePostRequest(reqUrl, param);
        Response response = null;
        try {
            response = service.execute(request);
            checkErrorCodes(response, reqUrl, secure);
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RequestException(String.format("Error on request '%s' to JCP.", url), e);
        }

        // Parsing response
        String body = extractBody(response, reqUrl, secure);
        if (reqObject == null)
            return null;
        if (reqObject.equals(String.class))
            return (T) body;
        return parseJSON(body, reqObject, reqUrl, secure);
    }


    // Abstract authentication function

    /**
     * Depending on authentication flow implemented, this method must return an
     * access token (in OAuth2 context).
     *
     * @param service OAuth2 service representation.
     * @return the OAuth2 access token.
     */
    protected abstract OAuth2AccessToken getAccessToken(OAuth20Service service) throws SSLHandshakeException, InterruptedException, ExecutionException, IOException;


    // Sub classes utils

    /**
     * Init java SSLContext with fake {@link javax.net.ssl.TrustManager} and
     * replace the SSL HostnameVerifier that accept only <code>localhost</code>.
     */
    protected void disableSSLChecks() {
        try {
            JavaSSLIgnoreChecks.disableSSLChecks(JavaSSLIgnoreChecks.LOCALHOST);
        } catch (JavaSSLIgnoreChecks.JavaSSLIgnoreChecksException e) {
            System.out.println(String.format("WAR: Can't disable SSL checks on localhost: '%s'", e.getMessage()));
        }
    }


    // Request mgnm

    /**
     * Compose the url's query from <code>params</code> and return the full url.
     *
     * @param url    resource url's without the <code>?{query}</code>.
     * @param params map key-value of query params.
     * @return the full resource + query url.
     */
    private String prepareGetUrl(String url, Map<String, String> params) {
        StringBuilder fullUrl = new StringBuilder(url + "?");
        for (Map.Entry<String, String> pair : params.entrySet())
            fullUrl.append(pair.getKey()).append("=").append(pair.getValue()).append("&");

        return fullUrl.toString();
    }

    /**
     * Create and sign OAuth2 GET request.
     *
     * @param url request's full url.
     * @return an executable OAuth2 request.
     */
    private OAuthRequest prepareGetRequest(String url) {
        OAuthRequest req = new OAuthRequest(Verb.GET, url);
        service.signRequest(accessToken, req);
        return req;
    }

    /**
     * Create and sign OAuth2 POST request.
     *
     * @param url request's full url.
     * @return an executable OAuth2 request.
     */
    private OAuthRequest preparePostRequest(String url, Object param) throws RequestException {
        OAuthRequest req = new OAuthRequest(Verb.POST, url);
        req.addHeader("Content-Type", "application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        try {
            req.setPayload(mapper.writeValueAsString(param));
        } catch (JsonProcessingException e) {
            throw new RequestException(String.format("Error on formatting POST request '%s' to JCP", url), e);
        }

        service.signRequest(accessToken, req);
        return req;
    }

    /**
     * Depending on <code>response.getCode()</code> this method don't do nothing
     * or thrown the {@link ResponseException}
     * corresponding to the response code.
     *
     * @param response response received for a request execution.
     * @param url      request full url.
     * @param secure   true if the request must be executed on secure channel.
     */
    private void checkErrorCodes(Response response, String url, boolean secure) throws ResponseException {
        switch (response.getCode()) {
            case 200:
                break;
            case 403:
                throw new NotAuthorized_403(url, secure);
            case 404:
                throw new NotFound_404(url, secure);
            case 409:
                throw new Conflict_409(url, secure);
            default:
                throw new Error_Code(url, secure, response.getCode());
        }
    }

    /**
     * Extract the body from given <code>response</code>.
     *
     * @param response response received for a request execution.
     * @param url      request full url.
     * @param secure   true if the request must be executed on secure channel.
     * @return a String containing the response body content.
     */
    private String extractBody(Response response, String url, boolean secure) throws ResponseParsingException {
        try {
            return response.getBody();
        } catch (IOException e) {
            throw new ResponseParsingException(url, secure, e);
        }
    }

    /**
     * JSON responses parser.
     * <p>
     * Parse JSON response body and cast them into required <code>reqObject</code>
     * object type.
     *
     * @param body      JSON String from response body.
     * @param reqObject class of the returned object (for array don't use
     *                  <code>Collection</code>, instead use pure arrays like
     *                  <code>String[]</code>).
     * @param reqUrl    request full url.
     * @param secure    true if the request must be executed on secure channel.
     * @param <T>       cast destination type.
     * @return the object created parsing given JSON string.
     */
    private <T> T parseJSON(String body, Class<T> reqObject, String reqUrl, boolean secure) throws ResponseParsingException {
        try {
            String jsonInString = body;
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonInString, reqObject);
        } catch (IOException e) {
            throw new ResponseParsingException(reqUrl, secure, e);
        }
    }

    /**
     * If current client has a valid <code>sessionId</code> then it will be
     * added to given request.
     *
     * @param request the request instance to add the <code>sessionId</code> to
     *                {@value #HEAD_COOKIE} header.
     */
    private void injectSession(OAuthRequest request) {
        if (sessionId != null)
            request.addHeader(HEAD_COOKIE, sessionId);
    }

    /**
     * If given response has a valid <code>sessionId</code> {@value #HEAD_SET_COOKIE}
     * header then it will store the session id for use it on next request.
     *
     * @param response the response instance to check for {@value #HEAD_SET_COOKIE}
     *                 header.
     */
    private void storeSession(Response response) {
        String setCookie = response.getHeader(HEAD_SET_COOKIE);
        if (setCookie == null)
            return;

        if (setCookie.contains(SESSION_KEY))
            sessionId = setCookie;
    }

}

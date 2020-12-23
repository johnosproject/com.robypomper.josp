/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.Verb;
import com.robypomper.java.JavaEnum;
import com.robypomper.java.JavaSSLIgnoreChecks;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.states.HTTPClientState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


@SuppressWarnings({"UnnecessaryReturnStatement", "unused"})
public class DefaultHTTPClient implements HTTPClient {

    // Class constants

    public static final String TH_CONNECTION_NAME = "_CONN_%s_";
    public static final String TH_CONNECTION_CHECK_NAME = "_CONN_%s_CHECK_";
    public static final String HEAD_COOKIE = "Cookie";
    public static final String HEAD_SET_COOKIE = "Set-Cookie";
    public static final String SESSION_KEY = "JSESSIONID";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JavaEnum.SynchronizableState<HTTPClientState> state = new JavaEnum.SynchronizableState<>(HTTPClientState.NOT_AVAILABLE, log);
    // Configs
    private final String httpBaseUrl;
    private final String httpServerName;
    private final int requestTimeOutSeconds;
    private final int availabilityTimerDelaySeconds;
    private boolean ignoreSSLHostnames;
    // Listeners
    private final List<AvailabilityListener> availabilityListeners = new ArrayList<>();
    private final List<AvailabilityListener> loginListeners = new ArrayList<>();
    // Connection timers
    private Timer availabilityTimer = null;
    // Headers and session
    private final Map<String, String> defaultHeaders = new HashMap<>();
    private String sessionId = null;
    // Other
    private boolean isAvailable;
    private Date lastAvailable;
    private Date lastRequestSuccess;
    private Date lastRequestError;


    // Constructor

    public DefaultHTTPClient(String httpBaseUrl,
                             String httpServerName,
                             int requestTimeOutSeconds,
                             int connectionRetrySeconds,
                             boolean ignoreSSLHostnames) {
        this.httpServerName = httpServerName;
        this.httpBaseUrl = httpBaseUrl;
        this.requestTimeOutSeconds = requestTimeOutSeconds;
        this.availabilityTimerDelaySeconds = connectionRetrySeconds;
        this.ignoreSSLHostnames = ignoreSSLHostnames;
        try {
            checkServerReachability();
        } catch (HTTPClientNotReachableException ignore) {
        }
    }


    // Getter state

    /**
     * {@inheritDoc}
     */
    @Override
    public HTTPClientState getState() {
        return state.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAvailableChecking() {
        return availabilityTimer != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastAvailable() {
        return lastAvailable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastRequest() {
        return lastRequestSuccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastRequestError() {
        return lastRequestError;
    }


    // Availability

    @Override
    public void startCheckAvailability() {
        doStartAvailabilityTimer();
    }

    @Override
    public void stopCheckAvailability() {
        doStopAvailabilityTimer();
    }


    // HTTP server availability manager

    private void checkServerReachability() throws HTTPClientNotReachableException {
        int code;
        try {
            URL url = new URL(httpBaseUrl);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            if (ignoreSSLHostnames && httpUrlConn instanceof HttpsURLConnection)
                ((HttpsURLConnection) httpUrlConn).setHostnameVerifier(JavaSSLIgnoreChecks.ALLHOSTS);
            httpUrlConn.setRequestMethod("GET");
            code = httpUrlConn.getResponseCode();

            if (code != 200) {
                state.set(HTTPClientState.NOT_AVAILABLE);
                isAvailable = false;
                HTTPClientNotReachableException e1 = new HTTPClientNotReachableException(new ResponseException(String.format("Received response code '%d' on check server reachability.", code), url.toString()));
                emitNotAvailable(e1);
                throw e1;
            }

        } catch (IOException e) {
            state.set(HTTPClientState.NOT_AVAILABLE);
            isAvailable = false;
            HTTPClientNotReachableException e1 = new HTTPClientNotReachableException(e);
            emitNotAvailable(e1);
            throw e1;
        }

        state.set(HTTPClientState.AVAILABLE);
        isAvailable = true;
        lastAvailable = JOSPProtocol.getNowDate();
        emitAvailable();
    }


    // HTTP server availability check timer

    private void doStartAvailabilityTimer() {
        if (availabilityTimer != null) return;

        availabilityTimer = new Timer(true);
        availabilityTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setName(TH_CONNECTION_CHECK_NAME);
                try {
                    checkServerReachability();
                } catch (HTTPClientNotReachableException ignore) {
                }

            }
        }, 0, availabilityTimerDelaySeconds * 1000);
    }

    private void doStopAvailabilityTimer() {
        if (availabilityTimer == null) return;

        availabilityTimer.cancel();
        availabilityTimer = null;
    }


    // Connection listeners

    @Override
    public void addAvailabilityListener(AvailabilityListener listener) {
        availabilityListeners.add(listener);
    }

    @Override
    public void removeAvailabilityListener(AvailabilityListener listener) {
        availabilityListeners.remove(listener);
    }

    private void emitAvailable() {
        for (AvailabilityListener l : availabilityListeners)
            l.onAvailable(this);
    }

    private void emitNotAvailable(Throwable t) {
        for (AvailabilityListener l : availabilityListeners)
            l.onNotAvailable(this, t);
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

    private void injectDefaultHeaders(HttpURLConnection httpUrlConn) {
        for (Map.Entry<String, String> h : defaultHeaders.entrySet())
            httpUrlConn.setRequestProperty(h.getKey(), h.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSessionSet() {
        return sessionId != null;
    }

    private void injectSession(HttpURLConnection httpUrlConn) {
        if (sessionId != null)
            httpUrlConn.setRequestProperty(HEAD_COOKIE, sessionId);
    }

    private void storeSession(HttpURLConnection httpUrlConn) {
        String setCookie = httpUrlConn.getHeaderField(HEAD_SET_COOKIE);
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
    public void execReq(Verb reqType, String path) throws RequestException, ResponseException {
        execReq(reqType, path, null, (Object) new HashMap<>());
    }

    @Override
    public void execReq(Verb reqType, String path, Map<String, String> params) throws RequestException, ResponseException {
        execReq(reqType, path, null, (Object) params);
    }

    @Override
    public void execReq(Verb reqType, String path, Object objParam) throws RequestException, ResponseException {
        execReq(reqType, path, null, objParam);
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject) throws RequestException, ResponseException {
        return execReq(reqType, path, reqObject, (Object) new HashMap<>());
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, Map<String, String> params) throws RequestException, ResponseException {
        return execReq(reqType, path, reqObject, (Object) params);
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, Object objParam) throws RequestException, ResponseException {
        String fullUrl = prepareUrl(path);

        if (reqType == Verb.GET) {
            if (objParam instanceof Map)
                //noinspection unchecked
                path = prepareGetPath(path, (Map<String, String>) objParam);
            else
                throw new RequestException(String.format("Error on exec request '[%s] %s' because GET request must give a Map<String,String> as parameter (get %s)", reqType, fullUrl, objParam.getClass().getSimpleName()));
        }

        String fullUrlRequest = prepareUrl(path);

        String responseBody;
        try {
            URL url = new URL(fullUrlRequest);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            if (ignoreSSLHostnames && httpUrlConn instanceof HttpsURLConnection)
                ((HttpsURLConnection) httpUrlConn).setHostnameVerifier(JavaSSLIgnoreChecks.ALLHOSTS);
            httpUrlConn.setRequestMethod(reqType.name());
            httpUrlConn.setUseCaches(false);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setConnectTimeout(requestTimeOutSeconds * 1000);

            injectDefaultHeaders(httpUrlConn);
            injectSession(httpUrlConn);

            SSLSocketFactory sslFactory = null;
            if (ignoreSSLHostnames && httpUrlConn instanceof HttpsURLConnection) {
                sslFactory = ((HttpsURLConnection) httpUrlConn).getSSLSocketFactory();
                ((HttpsURLConnection) httpUrlConn).setSSLSocketFactory(JavaSSLIgnoreChecks.newInstanceSSLDisableSSLChecks().getSocketFactory());
            }
            httpUrlConn.connect();

            if (reqType != Verb.GET && objParam != null) {
                OutputStreamWriter out = new OutputStreamWriter(httpUrlConn.getOutputStream());
                out.write(objParam.toString());
                out.close();
            }

            if (ignoreSSLHostnames && httpUrlConn instanceof HttpsURLConnection)
                ((HttpsURLConnection) httpUrlConn).setSSLSocketFactory(sslFactory);

            storeSession(httpUrlConn);
            responseBody = extractBody(httpUrlConn.getInputStream());
            int responseCode = httpUrlConn.getResponseCode();
            if (responseCode != 200)
                throwErrorCodes(responseCode, responseBody, fullUrl);

        } catch (IOException | JavaSSLIgnoreChecks.JavaSSLIgnoreChecksException e) {
            throw new RequestException(String.format("Error on exec [%s] request @ '%s' because %s", reqType, fullUrl, e.getMessage()), e);
        }

        if (reqObject == null)
            return null;
        String body = trimBody(responseBody);
        if (reqObject.equals(String.class))
            //noinspection unchecked
            return (T) body;

        return parseJSON(body, reqObject, fullUrl);
    }

    private String prepareGetPath(String path, Map<String, String> params) {
        if (params.size() == 0)
            return path;

        StringBuilder fullUrl = new StringBuilder(path + "?");
        for (Map.Entry<String, String> pair : params.entrySet())
            fullUrl.append(pair.getKey()).append("=").append(pair.getValue()).append("&");

        return fullUrl.toString();
    }

    private String prepareUrl(String path) {
        //String fullUrl = !httpBaseUrl.startsWith("http://") ? "http://" : "";
        String fullUrl = !httpBaseUrl.endsWith("/") ? httpBaseUrl : httpBaseUrl.substring(0, httpBaseUrl.length() - 1);
        fullUrl += !path.startsWith("/") ? "/" + path : path;
        return fullUrl;
    }

    private void throwErrorCodes(int code, String body, String fullUrl) throws ResponseException {
        switch (code) {
            case 200:
                break;
            case 400:
                throw new BadRequest_400(fullUrl);
            case 403:
                throw new NotAuthorized_403(fullUrl);
            case 404:
                throw new NotFound_404(fullUrl);
            case 409:
                throw new Conflict_409(fullUrl);
            default:
                throw new Error_Code(fullUrl, code, body);
        }
    }

    private String extractBody(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        return sb.toString();
    }

    private String trimBody(String body) {
        if (body.startsWith("\""))
            body = body.substring(1);
        if (body.endsWith("\""))
            body = body.substring(0, body.length() - 1);
        return body.trim();
    }

    private <T> T parseJSON(String body, Class<T> reqObject, String fullUrl) throws ResponseParsingException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(body, reqObject);

        } catch (IOException e) {
            throw new ResponseParsingException(fullUrl, e);
        }
    }

}

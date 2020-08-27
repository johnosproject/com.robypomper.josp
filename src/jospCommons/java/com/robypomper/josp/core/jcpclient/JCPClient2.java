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

package com.robypomper.josp.core.jcpclient;

import com.github.scribejava.core.model.Verb;

import java.util.Map;


public interface JCPClient2 {

    // Connection

    boolean isConnected();

    void connect() throws JCPNotReachableException, ConnectionException, AuthenticationException;

    void disconnect();


    // Connection listeners

    void addConnectListener(ConnectListener listener);

    void removeConnectListener(ConnectListener listener);

    void addDisconnectListener(DisconnectListener listener);

    void removeDisconnectListener(DisconnectListener listener);


    // Connection listeners interfaces

    interface ConnectListener {

        void onConnected(JCPClient2 jcpClient);

        void onConnectionFailed(JCPClient2 jcpClient, Throwable t);

    }

    interface DisconnectListener {

        void onDisconnected(JCPClient2 jcpClient);

    }


    // Connection timer

    boolean isConnecting();

    void startConnectionTimer();

    void stopConnectionTimer();


    // Authentication flows

    boolean isClientCredentialFlowEnabled();

    boolean isAuthCodeFlowEnabled();


    // Auth urls

    String getLoginUrl();

    String getLogoutUrl();

    String getLogoutUrl(String redirectUrl);


    // Login

    boolean isLoggedIn();

    void setLoginCode(String loginCode);

    void userLogout();


    // Login listeners

    void addLoginListener(LoginListener listener);

    void removeLoginListener(LoginListener listener);


    // Login listeners interface

    interface LoginListener {

        void onLogin(JCPClient2 jcpClient);

        void onLogout(JCPClient2 jcpClient);

    }


    // Headers and sessions

    void addDefaultHeader(String headerName, String headerValue);

    void removeDefaultHeader(String headerName);

    boolean isSessionSet();


    // Exec requests

    void execReq(Verb reqType, String path) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(boolean toAuth, Verb reqType, String path) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(Verb reqType, String path, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(boolean toAuth, Verb reqType, String path, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(Verb reqType, String path, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(boolean toAuth, Verb reqType, String path, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(Verb reqType, String path, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(boolean toAuth, Verb reqType, String path, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    <T> T execReq(Verb reqType, String path, Class<T> reqObject, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    <T> T execReq(Verb reqType, String path, Class<T> reqObject, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    <T> T execReq(Verb reqType, String path, Class<T> reqObject, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;


    // Connection exceptions

    class ConnectionException extends Throwable {

        public ConnectionException(String msg) {
            super(msg);
        }

        public ConnectionException(String msg, Throwable e) {
            super(msg, e);
        }

    }

    class JCPNotReachableException extends Throwable {

        public JCPNotReachableException(String msg) {
            super(msg);
        }

    }

    class AuthenticationException extends Throwable {

        public AuthenticationException(String msg) {
            super(msg);
        }

        public AuthenticationException(String msg, Exception e) {
            super(msg, e);
        }

    }


    // Request exceptions

    class RequestException extends Throwable {

        public RequestException(String msg) {
            super(msg);
        }

        public RequestException(String msg, Exception e) {
            super(msg, e);
        }

    }

    class ResponseException extends Throwable {
        protected final String url;
        protected final boolean secure;

        public ResponseException(String msg, String url, boolean secure) {
            this(msg, url, secure, null);
        }

        public ResponseException(String msg, String url, boolean secure, Exception e) {
            super(msg, e);
            this.url = url;
            this.secure = secure;
        }
    }

    class ResponseParsingException extends ResponseException {

        private static final String MSG = "Error on '%s' url response parsing ('%s').";

        public ResponseParsingException(String url, boolean secure, Exception e) {
            super(String.format(MSG, url, e.getMessage()), url, secure, e);
        }

    }

    class BadRequest_400 extends ResponseException {

        private static final String MSG = "Server received Bad request for '%s' resource.";

        public BadRequest_400(String url, boolean secure) {
            super(String.format(MSG, url), url, secure);
        }

    }

    class NotAuthorized_403 extends ResponseException {

        private static final String MSG = "Client NOT authorized to access to '%s' resource.";

        public NotAuthorized_403(String url, boolean secure) {
            super(String.format(MSG, url), url, secure);
        }

    }

    class NotFound_404 extends ResponseException {

        private static final String MSG = "Resource '%s' NOT found.";

        public NotFound_404(String url, boolean secure) {
            super(String.format(MSG, url), url, secure);
        }

    }

    class Conflict_409 extends ResponseException {

        private static final String MSG = "Conflict on elaborating request on '%s' resource.";

        public Conflict_409(String url, boolean secure) {
            super(String.format(MSG, url), url, secure);
        }

    }

    class Error_Code extends ResponseException {

        private static final String MSG = "Response error '%s' code on '%s' resource.";

        public Error_Code(String url, boolean secure, int code) {
            super(String.format(MSG, code, url), url, secure);
        }

    }

}

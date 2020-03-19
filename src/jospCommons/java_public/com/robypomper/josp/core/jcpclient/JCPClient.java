package com.robypomper.josp.core.jcpclient;

import java.util.Map;


/**
 * Main JCP client interface.
 * <p>
 * It represent a JCP client for both types of allowed clients: Objects and
 * Services.
 * <p>
 * It provide all common management methods: connect, disconnect, isConnected...
 * and a large set of requests execution methods parted by HTTP method (GET,
 * POST...).
 * <p>
 * Finally it provide a large set of Exception classes to identify errors on
 * client connection ({@link ConnectionException}), request execution
 * ({@link RequestException} and {@link ResponseException}) or response error
 * codes (all sub classes of {@link ResponseException}).
 */
public interface JCPClient {

    // Mngm

    /**
     * @return <code>true</code> if current client is connected to the JCP cloud.
     */
    boolean isConnected();

    /**
     * Authenticate and prepare current client to exec request to the JCP.
     */
    void connect() throws ConnectionException;

    /**
     * Reset the JCPClient to initial state.
     */
    void disconnect();

    /**
     * Like {@link #connect()} but this one print log messages instead thrown
     * exceptions.
     */
    void tryConnect();

    /**
     * Like {@link #disconnect()} but this one print log messages instead thrown
     * exceptions.
     */
    void tryDisconnect();


    // Get requests

    /**
     * Exec the GET request specified from given params.
     * <p>
     * This method implementation must send the request, check for errors HTTP
     * status codes and cast response to desired object type.
     *
     * @param url    request's full url.
     * @param secure true if the request must be executed on secure channel.
     */
    void execGetReq(String url, boolean secure) throws RequestException, ConnectionException;

    /**
     * Exec the GET request specified from given params.
     * <p>
     * This method implementation must send the request, check for errors HTTP
     * status codes and cast response to desired object type.
     *
     * @param url    request's full url.
     * @param params map key-value of query params.
     * @param secure true if the request must be executed on secure channel.
     */
    void execGetReq(String url, Map<String, String> params, boolean secure) throws RequestException, ConnectionException;

    /**
     * Exec the GET request specified from given params.
     * <p>
     * This method implementation must send the request, check for errors HTTP
     * status codes and cast response to desired object type.
     *
     * @param url       request's full url.
     * @param reqObject class of the returned object.
     * @param secure    true if the request must be executed on secure channel.
     * @param <T>       cast destination type.
     * @return the object created parsing the obtained response.
     */
    <T> T execGetReq(String url, Class<T> reqObject, boolean secure) throws RequestException, ConnectionException;

    /**
     * Exec the GET request specified from given params.
     * <p>
     * This method implementation must send the request, check for errors HTTP
     * status codes and cast response to desired object type.
     *
     * @param url       request's full url.
     * @param reqObject class of the returned object.
     * @param params    map key-value of query params.
     * @param secure    true if the request must be executed on secure channel.
     * @param <T>       cast destination type.
     * @return the object created parsing the obtained response.
     */
    <T> T execGetReq(String url, Class<T> reqObject, Map<String, String> params, boolean secure) throws RequestException, ConnectionException;


    // Post requests

    /**
     * Exec the POST request specified from given params.
     * <p>
     * This method implementation must send the request, check for errors HTTP
     * status codes and cast response to desired object type.
     *
     * @param url    request's full url.
     * @param secure true if the request must be executed on secure channel.
     */
    void execPostReq(String url, boolean secure) throws RequestException, ConnectionException;

    /**
     * Exec the POST request specified from given params.
     * <p>
     * This method implementation must send the request, check for errors HTTP
     * status codes and cast response to desired object type.
     *
     * @param url    request's full url.
     * @param param  object to send as request body, JSON formatted.
     * @param secure true if the request must be executed on secure channel.
     */
    void execPostReq(String url, Object param, boolean secure) throws RequestException, ConnectionException;

    /**
     * Exec the POST request specified from given params.
     * <p>
     * This method implementation must send the request, check for errors HTTP
     * status codes and cast response to desired object type.
     *
     * @param url       request's full url.
     * @param reqObject class of the returned object.
     * @param secure    true if the request must be executed on secure channel.
     * @param <T>       cast destination type.
     * @return the object created parsing the obtained response.
     */
    <T> T execPostReq(String url, Class<T> reqObject, boolean secure) throws RequestException, ConnectionException;

    /**
     * Exec the POST request specified from given params.
     * <p>
     * This method implementation must send the request, check for errors HTTP
     * status codes and cast response to desired object type.
     *
     * @param url       request's full url.
     * @param reqObject class of the returned object.
     * @param param     object to send as request body, JSON formatted.
     * @param secure    true if the request must be executed on secure channel.
     * @param <T>       cast destination type.
     * @return the object created parsing the obtained response.
     */
    <T> T execPostReq(String url, Class<T> reqObject, Object param, boolean secure) throws RequestException, ConnectionException;


    // Exceptions

    /**
     * Exceptions for JCPClient connection errors (include security related errors).
     */
    class ConnectionException extends Throwable {
        public ConnectionException(String msg) {
            super(msg);
        }

        public ConnectionException(String msg, Exception e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions for errors during request setup or execution.
     */
    class RequestException extends Throwable {
        public RequestException(String msg) {
            super(msg);
        }

        public RequestException(String msg, Exception e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions for errors during response parsing.
     */
    class ResponseException extends RequestException {
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

    /**
     * Exceptions for errors during response parsing.
     */
    class ResponseParsingException extends ResponseException {
        private static final String MSG = "Error on '%s' url response parsing ('%s').";

        public ResponseParsingException(String url, boolean secure, Exception e) {
            super(String.format(MSG, url, e.getMessage()), url, secure, e);
        }
    }

    /**
     * Exceptions for responses with status code <code>400</code>.
     */
    class BadRequest_400 extends ResponseException {
        private static final String MSG = "Server received Bad request for '%s' resource.";

        public BadRequest_400(String url, boolean secure) {
            super(String.format(MSG, url), url, secure);
        }
    }

    /**
     * Exceptions for responses with status code <code>403</code>.
     */
    class NotAuthorized_403 extends ResponseException {
        private static final String MSG = "Client NOT authorized to access to '%s' resource.";

        public NotAuthorized_403(String url, boolean secure) {
            super(String.format(MSG, url), url, secure);
        }
    }

    /**
     * Exceptions for responses with status code <code>404</code>.
     */
    class NotFound_404 extends ResponseException {
        private static final String MSG = "Resource '%s' NOT found.";

        public NotFound_404(String url, boolean secure) {
            super(String.format(MSG, url), url, secure);
        }
    }

    /**
     * Exceptions for responses with status code <code>409</code>.
     */
    class Conflict_409 extends ResponseException {
        private static final String MSG = "Conflict on elaborating request on '%s' resource.";

        public Conflict_409(String url, boolean secure) {
            super(String.format(MSG, url), url, secure);
        }
    }

    /**
     * Exceptions for responses with generic status code.
     */
    class Error_Code extends ResponseException {
        private static final String MSG = "Response error '%s' code on '%s' resource.";

        public Error_Code(String url, boolean secure, int code) {
            super(String.format(MSG, code, url), url, secure);
        }
    }

}

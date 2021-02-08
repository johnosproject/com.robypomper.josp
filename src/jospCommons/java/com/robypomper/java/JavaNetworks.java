package com.robypomper.java;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.*;


/**
 * Utils class for network ops.
 */
public class JavaNetworks {

    private static final boolean DEF_IGNORE_SSL_HOST_VERIFIER = false;

    // Socket checks

    /**
     * Check if specified host:port is reachable and active.
     *
     * @param host      a String containing the hostname/host address to resolve.
     * @param port      the port to check on host.
     * @param timeoutMs the connection timeout in ms.
     * @return true if the method connected successfully to the host:port.
     */
    public static boolean checkSocketReachability(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;

        } catch (Throwable e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }


    // Url checks

    /**
     * Check if given url is reachable or not.
     * <p>
     * This method perform a GET request and consider url reachable
     * only if request's return 200 code.
     *
     * @param urlString the url to chek if it's reachable.
     * @return true only if URL response with 200 code.
     */
    public static boolean checkURLReachability(String urlString) throws MalformedURLException {
        return checkURLReachability(new URL(urlString), DEF_IGNORE_SSL_HOST_VERIFIER);
    }

    /**
     * Check if given url is reachable or not.
     * <p>
     * This method perform a GET request and consider url reachable
     * only if request's return 200 code.
     *
     * @param urlString             the url to chek if it's reachable.
     * @param ignoreSSLHostVerifier if true, disable the hostname
     *                              verifier for
     *                              {@link HttpURLConnection} used
     *                              to send request.
     * @return true only if URL response with 200 code.
     */
    public static boolean checkURLReachability(String urlString, boolean ignoreSSLHostVerifier) throws MalformedURLException {
        return checkURLReachability(new URL(urlString), ignoreSSLHostVerifier);
    }

    /**
     * Check if given url is reachable or not.
     * <p>
     * This method perform a GET request and consider url reachable
     * only if request's return 200 code.
     *
     * @param url the url to chek if it's reachable.
     * @return true only if URL response with 200 code.
     */
    public static boolean checkURLReachability(URL url) {
        return checkURLReachability(url, DEF_IGNORE_SSL_HOST_VERIFIER);
    }

    /**
     * Check if given url is reachable or not.
     * <p>
     * This method perform a GET request and consider url reachable
     * only if request's return 200 code.
     *
     * @param url                   the url to chek if it's reachable.
     * @param ignoreSSLHostVerifier if true, disable the hostname
     *                              verifier for
     *                              {@link HttpURLConnection} used
     *                              to send request.
     * @return true only if URL response with 200 code.
     */
    public static boolean checkURLReachability(URL url, boolean ignoreSSLHostVerifier) {
        int code;
        try {
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            if (ignoreSSLHostVerifier && httpUrlConn instanceof HttpsURLConnection)
                JavaSSLIgnoreChecks.disableSSLHostVerifierOnAllHost((HttpsURLConnection) httpUrlConn);
            httpUrlConn.setRequestMethod("GET");
            code = httpUrlConn.getResponseCode();

            if (code == 200)
                return true;

        } catch (IOException ignore) { /* ignored because if occurs IOException, probably server is not reachable */ }

        return false;
    }

}

package com.robypomper.java;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class JavaSSLIgnoreChecks {

    public static void disableSSLChecks(HostnameVerifier hostVerifier) throws JavaSSLIgnoreChecksException {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            throw new JavaSSLIgnoreChecksException(e);
        }

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(hostVerifier);
    }

    public static HostnameVerifier LOCALHOST = (hostname, sslSession) -> hostname.equals("localhost");

    public static HostnameVerifier ALLHOSTS = (hostname, session) -> true;

    private static TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

            }
    };

    public static class JavaSSLIgnoreChecksException extends Throwable {
        public JavaSSLIgnoreChecksException(Exception e) {
            super("Can't disable SSL checks because error occurred",e);
        }
    }
}

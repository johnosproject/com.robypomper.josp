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

package com.robypomper.java;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class JavaSSLIgnoreChecks {

    public static void disableSSLChecks(HostnameVerifier hostVerifier) throws JavaSSLIgnoreChecksException {
        SSLContext sc = newInstanceSSLDisableSSLChecks();

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(hostVerifier);
    }

    public static SSLContext newInstanceSSLDisableSSLChecks() throws JavaSSLIgnoreChecksException {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            throw new JavaSSLIgnoreChecksException(e);
        }

        return sc;
    }

    public static HostnameVerifier LOCALHOST = (hostname, sslSession) -> hostname.equals("localhost");

    public static HostnameVerifier ALLHOSTS = (hostname, session) -> true;

    private static TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) { }

                public void checkServerTrusted(X509Certificate[] certs, String authType) { }

            }
    };

    public static class JavaSSLIgnoreChecksException extends Throwable {
        public JavaSSLIgnoreChecksException(Exception e) {
            super("Can't disable SSL checks because error occurred", e);
        }
    }
}

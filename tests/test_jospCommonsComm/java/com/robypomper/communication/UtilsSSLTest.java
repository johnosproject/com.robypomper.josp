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

package com.robypomper.communication;

import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyStore;

public class UtilsSSLTest {

    // Class constants

    final static String ID_CERTIFICATE = "TestSSLCertServer";
    final static String CERT_ALIAS = "certAlias";
    final static String KS_PASS = "ksPass";


    // Generate SSL Context

    @Test
    public void testGenerateSSLCtxForServer() throws UtilsJKS.GenerationException, UtilsSSL.GenerationException {
        KeyStore keyStore = UtilsJKS.generateKeyStore(ID_CERTIFICATE, KS_PASS, CERT_ALIAS);
        TrustManager trustManager = new DynAddTrustManager();
        SSLContext sslCtx = UtilsSSL.generateSSLContext(keyStore, KS_PASS, trustManager);
    }

    @Test
    public void testGenerateSSLCtxForClient() throws UtilsJKS.GenerationException, UtilsSSL.GenerationException {
        KeyStore keyStore = UtilsJKS.generateKeyStore(ID_CERTIFICATE, KS_PASS, CERT_ALIAS);
        SSLContext sslCtx = UtilsSSL.generateSSLContext(keyStore, KS_PASS, null);
    }

    // With loaded

//    @Test
//    public void testGenerateSSLCtxFromLoadedKeyStore() throws UtilsJKS.GenerationException, UtilsSSL.GenerationException {
//        File keyStoreFile = new File("keyStore.ks");
//
//        KeyStore keyStore;
//        if (keyStoreFile.exists())
//            keyStore = UtilsJKS.loadKeyStore(keyStoreFile,KS_PASS);
//        else
//            keyStore = UtilsJKS.generateKeyStore(ID_CERTIFICATE,KS_PASS,CERT_ALIAS);
//        SSLContext sslCtx = UtilsSSL.generateServerContext(keyStore,KS_PASS,null);
//    }

}
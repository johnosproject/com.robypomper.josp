package com.robypomper.communication;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
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
        KeyStore keyStore = UtilsJKS.generateKeyStore(ID_CERTIFICATE,KS_PASS,CERT_ALIAS);
        TrustManager trustManager = new DynAddTrustManager();
        SSLContext sslCtx = UtilsSSL.generateSSLContext(keyStore,KS_PASS,trustManager);
    }

    @Test
    public void testGenerateSSLCtxForClient() throws UtilsJKS.GenerationException, UtilsSSL.GenerationException {
        KeyStore keyStore = UtilsJKS.generateKeyStore(ID_CERTIFICATE,KS_PASS,CERT_ALIAS);
        SSLContext sslCtx = UtilsSSL.generateSSLContext(keyStore,KS_PASS,null);
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
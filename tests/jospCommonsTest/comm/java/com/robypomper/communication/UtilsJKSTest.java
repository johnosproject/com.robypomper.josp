package com.robypomper.communication;

import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import com.robypomper.log.Mrk_Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class UtilsJKSTest {

    // Class constants

    final static String ID_CERTIFICATE = "TestSSLCertServer";
    final static String EXPORT_CERT_PATH = String.format("pubCert-%s.crt", UtilsJKSTest.class.getSimpleName());
    final static String EXPORT_KS_PATH = String.format("storedKeystore-%s.crt", UtilsJKSTest.class.getSimpleName());
    final static String CERT_ALIAS = "certAlias";
    final static String KS_PASS = "ksPass";


    // Internal vars

    protected static Logger log = LogManager.getLogger();


    // Generate

    @Test
    public void testGenerateKeyStore() throws UtilsJKS.GenerationException, KeyStoreException {
        // Create keystore
        System.out.println("\nGENERATE KEYSTORE");
        KeyStore keyStore = UtilsJKS.generateKeyStore(ID_CERTIFICATE, KS_PASS, CERT_ALIAS);

        // Check keys
        Assertions.assertTrue(keyStore.isKeyEntry(CERT_ALIAS));

        // Check certificate chain
        Certificate[] certChain = keyStore.getCertificateChain(CERT_ALIAS);
        Assertions.assertNotNull(certChain);
        Assertions.assertNotEquals(0, certChain.length);
    }


    // Imports and exports

    @Test
    public void testStoreAndLoadKeyStore() throws UtilsJKS.GenerationException, UtilsJKS.LoadingException, KeyStoreException {
        // Create keystore
        System.out.println("\nGENERATE KEYSTORE");
        KeyStore keyStore = UtilsJKS.generateKeyStore(ID_CERTIFICATE, KS_PASS, CERT_ALIAS);

        // Store keystore
        System.out.println("\nSTORE KEYSTORE");
        UtilsJKS.storeKeyStore(keyStore, EXPORT_KS_PATH, KS_PASS);

        // Load keystore
        System.out.println("\nSTORE KEYSTORE");
        KeyStore keyStore2 = UtilsJKS.loadKeyStore(EXPORT_KS_PATH, KS_PASS);

        Certificate[] certs = keyStore.getCertificateChain(CERT_ALIAS);
        Certificate[] certs2 = keyStore2.getCertificateChain(CERT_ALIAS);
        Assertions.assertEquals(certs.length, certs2.length);
        Assertions.assertEquals(certs[0].hashCode(), certs2[0].hashCode());

        // Delete exported certificate files
        if (new File(EXPORT_KS_PATH).delete())
            log.debug(Mrk_Test.TEST, String.format("Public certificate file '%s' delete successfully", EXPORT_CERT_PATH));
        else
            log.debug(Mrk_Test.TEST, String.format("Error on deleting public certificate file '%s'", EXPORT_CERT_PATH));
    }

    @Test
    public void testCopyCertificates() throws UtilsJKS.GenerationException, KeyStoreException, UtilsJKS.StoreException {
        AbsCustomTrustManager trustManager = new DynAddTrustManager();

        // Create keystore
        System.out.println("\nGENERATE KEYSTORE");
        KeyStore keyStore = UtilsJKS.generateKeyStore(ID_CERTIFICATE, KS_PASS, CERT_ALIAS);

        System.out.println("\nCOPY KEYSTORE TO TRUSTMANAGER");
        Assertions.assertEquals(0, trustManager.getCertificates().size());
        UtilsJKS.copyCertsFromKeyStoreToTrustManager(keyStore, trustManager);
        Assertions.assertEquals(1, trustManager.getCertificates().size());

        KeyStore keyStore2 = UtilsJKS.generateKeyStore(ID_CERTIFICATE, KS_PASS, CERT_ALIAS + "XY");

        System.out.println("\nCOPY TRUSTMANAGER TO KEYSTORE");
        Assertions.assertNull(keyStore2.getCertificate(CERT_ALIAS));
        UtilsJKS.copyCertsFromTrustManagerToKeyStore(keyStore2, trustManager);
        Assertions.assertNotNull(keyStore2.getCertificate(CERT_ALIAS));
    }

    @Test
    public void testExportAndLoadCertificate() throws UtilsJKS.GenerationException, UtilsJKS.LoadingException {
        // Create keystore
        System.out.println("\nGENERATE KEYSTORE");
        KeyStore keyStore = UtilsJKS.generateKeyStore(ID_CERTIFICATE, KS_PASS, CERT_ALIAS);

        // Export certificate
        System.out.println("\nEXPORT CERTIFICATE");
        UtilsJKS.exportCertificate(keyStore, EXPORT_CERT_PATH, CERT_ALIAS);

        // Check file exported
        File exportedCertFile = new File(EXPORT_CERT_PATH);
        Assertions.assertTrue(exportedCertFile.exists());

        // Load certificate
        System.out.println("\nLOAD CERTIFICATE");
        Certificate exportedCert = UtilsJKS.loadCertificateFromFile(exportedCertFile);

        // Check file content
        Assertions.assertEquals(UtilsJKS.CERT_TYPE, exportedCert.getType());
        PublicKey exportedCertPubKey = exportedCert.getPublicKey();
        Assertions.assertEquals(UtilsJKS.KEYPAIR_ALG, exportedCertPubKey.getAlgorithm());
        Assertions.assertEquals(UtilsJKS.CERT_TYPE, exportedCertPubKey.getFormat());

        // Delete exported certificate files
        if (exportedCertFile.delete())
            log.debug(Mrk_Test.TEST, String.format("Public certificate file '%s' delete successfully", EXPORT_CERT_PATH));
        else
            log.debug(Mrk_Test.TEST, String.format("Error on deleting public certificate file '%s'", EXPORT_CERT_PATH));
    }

}
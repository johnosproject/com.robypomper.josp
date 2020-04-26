package com.robypomper.communication;

import com.robypomper.log.Markers;
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
    final static String CERT_ALIAS = "certAlias";
    final static String KS_PASS = "ksPass";


    // Internal vars

    protected static Logger log = LogManager.getLogger();


    // Generate

    @Test
    public void testGenerateKeyStore() throws UtilsJKS.GenerationException, KeyStoreException {
        // Create keystore
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
    public void testExportAndLoadCertificate() throws UtilsJKS.GenerationException, UtilsJKS.LoadingException {
        // Create keystore and export certificate
        KeyStore keyStore = UtilsJKS.generateKeyStore(ID_CERTIFICATE, KS_PASS, CERT_ALIAS);
        UtilsJKS.exportCertificate(keyStore, EXPORT_CERT_PATH, CERT_ALIAS);
        File exportedCertFile = new File(EXPORT_CERT_PATH);

        // Check file exported
        Assertions.assertTrue(exportedCertFile.exists());

        // Check file content
        // this check also UtilsJKS.loadCertificateFromBytes(byte[])
        Certificate exportedCert = UtilsJKS.loadCertificateFromFile(exportedCertFile);
        Assertions.assertEquals(UtilsJKS.CERT_TYPE, exportedCert.getType());

        PublicKey exportedCertPubKey = exportedCert.getPublicKey();
        Assertions.assertEquals(UtilsJKS.KEYPAIR_ALG, exportedCertPubKey.getAlgorithm());
        Assertions.assertEquals(UtilsJKS.CERT_TYPE, exportedCertPubKey.getFormat());

        // Delete exported certificate files
        if (exportedCertFile.delete())
            log.debug(Markers.TEST, String.format("Public certificate file '%s' delete successfully", EXPORT_CERT_PATH));
        else
            log.debug(Markers.TEST, String.format("Error on deleting public certificate file '%s'", EXPORT_CERT_PATH));
    }

}
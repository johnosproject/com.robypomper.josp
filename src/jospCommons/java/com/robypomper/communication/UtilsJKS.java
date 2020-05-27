package com.robypomper.communication;

import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;


/**
 * Utils class to manage Java Key Stores (JKS).
 */
public class UtilsJKS {

    // Class constants

    public static final String KEYSTORE_TYPE = "PKCS12";        // KeyStore.getDefaultType() => jks
    public static final String CERT_TYPE = "X.509";
    public static final String KEYPAIR_ALG = "RSA";
    public static final String SIGING_ALG = "SHA256withRSA";
    public static final int KEY_SIZE = 2048;
    public static final int CERT_VALIDITY_DAYS = 3650;
    public static final String EXT_KEYSTORE = ".p12";
    public static final String EXT_CERTIFICATE = ".crt";


    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // JKS, Keys, cert generators

    /**
     * Generate new Java Key Store containing the Key Pair and the corresponding
     * Certificate Chain.
     * <p>
     * The certificate chain contain only the Key Pair certificate with given id.
     *
     * @param certificateID the string used as certificate commonName (this will
     *                      be visible also to other peer side).
     * @param ksPass        the JKS password.
     * @param certAlias     the alias associated to the generated certificate.
     * @return a valid Key Store with Key Pair and Certificate Chain.
     */
    public static KeyStore generateKeyStore(String certificateID, String ksPass, String certAlias) throws GenerationException {
        log.trace(Mrk_Commons.COMM_SSL_UTILS, String.format("Generating KeyStore for '%s' certificate with '%s' id", certAlias, certificateID));

        if (ksPass == null) ksPass = "";
        KeyPair localKP = UtilsJKS.generateKeyPair();
        Certificate localCert = UtilsJKS.generateCertificate(certificateID, localKP);
        Certificate[] localCertChain = UtilsJKS.toCertificateChain(localCert);

        KeyStore localKS = UtilsJKS.generateNewKeyStore();
        UtilsJKS.addPrivateCertificateToKeyStore(localKS, localKP, localCertChain, ksPass, certAlias);
        return localKS;
    }

    /**
     * Generate new Key Pair with {@value KEYPAIR_ALG} algorithm and
     * {@value KEY_SIZE} size.
     *
     * @return new (random) key pair.
     */
    private static KeyPair generateKeyPair() throws GenerationException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEYPAIR_ALG);
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.generateKeyPair();

        } catch (GeneralSecurityException e) {
            throw new GenerationException(String.format("Error generating keys pair because %s", e.getMessage()), e);
        }
    }

    /**
     * Generate new Certificate based on given Key Pair and valid for
     * {@value CERT_VALIDITY_DAYS} days.
     *
     * @param commonName the certificate commonName.
     * @param keyPair    keys to associate to the certificate.
     * @return certificate based on given Key Pair.
     */
    private static X509Certificate generateCertificate(String commonName, KeyPair keyPair) throws GenerationException {
        String dn = String.format("cn=%s", commonName);
        try {
            PrivateKey privateKey = keyPair.getPrivate();
            Date from = new Date();
            Date to = new Date(from.getTime() + CERT_VALIDITY_DAYS * 1000L * 24L * 60L * 60L);
            CertificateValidity interval = new CertificateValidity(from, to);
            BigInteger serialNumber = new BigInteger(64, new SecureRandom());
            X500Name owner = new X500Name(dn);
            AlgorithmId sigAlgId = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);

            X509CertInfo info = new X509CertInfo();
            info.set(X509CertInfo.VALIDITY, interval);
            info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(serialNumber));
            info.set(X509CertInfo.SUBJECT, owner);
            info.set(X509CertInfo.ISSUER, owner);
            info.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
            info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
            info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(sigAlgId));

            // Sign the cert to identify the algorithm that's used.
            X509CertImpl certificate = new X509CertImpl(info);
            certificate.sign(privateKey, SIGING_ALG);

            // Update the algorith, and resign.
            sigAlgId = (AlgorithmId) certificate.get(X509CertImpl.SIG_ALG);
            info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, sigAlgId);
            certificate = new X509CertImpl(info);
            certificate.sign(privateKey, SIGING_ALG);

            return certificate;

        } catch (IOException | CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
            throw new GenerationException(String.format("Error generating certificate pair because %s", e.getMessage()), e);
        }
    }

    /**
     * Generate a Certificate Chain containing only given certificate.
     *
     * @param cert the certificate to add to the chain.
     * @return the certificate in the certificate chain form.
     */
    private static Certificate[] toCertificateChain(Certificate cert) {
        return new Certificate[]{cert};
    }

    /**
     * Generate new Java Key Store of {@value KEYSTORE_TYPE} type.
     *
     * @return new and empty KeyStore.
     */
    private static KeyStore generateNewKeyStore() throws GenerationException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            keyStore.load(null, null);   //ksPass.toCharArray()
            return keyStore;

        } catch (Exception e) {
            throw new GenerationException(String.format("Error generating key store because %s", e.getMessage()), e);
        }
    }


    // JKS import and exports

    /**
     * Load {@link KeyStore} from given path and use given password.
     *
     * @param ksPath the file path containing the KeyStore to load.
     * @param ksPass the string containing the KeyStore password.
     * @return the loaded KeyStore.
     */
    public static KeyStore loadKeyStore(String ksPath, String ksPass) throws LoadingException {
        log.trace(Mrk_Commons.COMM_SSL_UTILS, String.format("Loading KeyStore from '%s' file", ksPath));

        try {
            KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
            InputStream keyStoreInputStream = new FileInputStream(ksPath);
            ks.load(keyStoreInputStream, ksPass.toCharArray());
            return ks;
        } catch (Exception e) {
            throw new LoadingException(String.format("Error loading key store from '%s' file because %s", ksPath, e.getMessage()), e);
        }
    }

    /**
     * Save given KeyStore to given file path and use given password.
     *
     * @param ks     the KeyStore to store.
     * @param ksPath the file path where to store the KeyStore.
     * @param ksPass the string containing the KeyStore password.
     */
    public static void storeKeyStore(KeyStore ks, String ksPath, String ksPass) throws LoadingException {
        log.trace(Mrk_Commons.COMM_SSL_UTILS, String.format("Storing KeyStore on '%s' file", ksPath));

        try {
            dirExistOrCreate(ksPath);
            FileOutputStream keyStoreOutputStream = new FileOutputStream(ksPath);
            ks.store(keyStoreOutputStream, ksPass.toCharArray());
        } catch (Exception e) {
            throw new LoadingException(String.format("Error storing key store from '%s' file because %s", ksPath, e.getMessage()), e);
        }
    }

    /**
     * Add given certificate chain as private certificate to the KeyStore
     * (given certificate require private key).
     */
    private static void addPrivateCertificateToKeyStore(KeyStore keyStore, KeyPair keyPair, Certificate[] certChain, String ksPass, String certAlias) throws GenerationException {
        try {
            keyStore.setKeyEntry(certAlias, keyPair.getPrivate(), ksPass.toCharArray(), certChain);

        } catch (GeneralSecurityException e) {
            throw new GenerationException(String.format("Error adding certificate to key store because %s", e.getMessage()), e);
        }
    }

    /**
     * Save <code>ksAlias</code> certificate contained in <code>ksFile</code>
     * to <code>exportCertFile</code>given certificate chain as public certificate to the KeyStore.
     */
    public static void exportCertificate(KeyStore keyStore, String exportCertFile, String ksAlias) throws GenerationException {
        log.trace(Mrk_Commons.COMM_SSL_UTILS, String.format("Exporting certificate '%s' on '%s' file", ksAlias, exportCertFile));

        try {
            Certificate cert = keyStore.getCertificate(ksAlias);
            byte[] buf = cert.getEncoded();

            dirExistOrCreate(exportCertFile);
            FileOutputStream os = new FileOutputStream(new File(exportCertFile));
            os.write(buf);
            os.close();

        } catch (IOException | KeyStoreException | CertificateEncodingException e) {
            throw new GenerationException(String.format("Error exporting certificate to file %s because %s", exportCertFile, e.getMessage()), e);
        }
    }

    /**
     * Load the Certificate from given string.
     *
     * @param file the file containing the certificate.
     * @return the loaded certificate.
     */
    public static Certificate loadCertificateFromFile(File file) throws LoadingException {
        log.trace(Mrk_Commons.COMM_SSL_UTILS, String.format("Loading certificate from '%s' file", file.getPath()));

        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            return loadCertificateFromBytes(fileBytes);

        } catch (Exception e) {
            throw new LoadingException(String.format("Error loading certificate from bytes because %s", e.getMessage()), e);
        }
    }

    /**
     * Load the Certificate from given string.
     *
     * @param bytesCert the byte array containing the certificate.
     * @return the loaded certificate.
     */
    public static Certificate loadCertificateFromBytes(byte[] bytesCert) throws LoadingException {
        log.trace(Mrk_Commons.COMM_SSL_UTILS, "Loading certificate from bytes");

        try {
            CertificateFactory certFactory = CertificateFactory.getInstance(CERT_TYPE);
            InputStream inputStream = new ByteArrayInputStream(bytesCert);
            return certFactory.generateCertificate(inputStream);

        } catch (Exception e) {
            throw new LoadingException(String.format("Error loading certificate from bytes because %s", e.getMessage()), e);
        }
    }

    /**
     * Copy all certificates from given {@link javax.net.ssl.TrustManager} to given
     * {@link KeyStore}.
     * <p>
     * It copies all certificates without duplicates. Certificates are duplicated
     * if their alias is already present in the destination.
     *
     * @param keyStore     the certificates source.
     * @param trustManager the certificates destination.
     */
    public static void copyCertsFromTrustManagerToKeyStore(KeyStore keyStore, AbsCustomTrustManager trustManager) throws StoreException {
        log.trace(Mrk_Commons.COMM_SSL_UTILS, "Synchronizing certificate from trust manager to key store");

        for (Map.Entry<String, Certificate> aliasAndCert : trustManager.getCertificates().entrySet())
            try {
                keyStore.setCertificateEntry(aliasAndCert.getKey(), aliasAndCert.getValue());
            } catch (KeyStoreException e) {
                if (!e.getMessage().startsWith("Cannot overwrite own certificate"))
                    throw new StoreException(String.format("Error coping certificate from trust manager to key store because %s", e.getMessage()), e);
            }
    }

    /**
     * Copy all certificates from given {@link KeyStore} to given
     * {@link javax.net.ssl.TrustManager}.
     * <p>
     * It copies all certificates without duplicates. Certificates are duplicated
     * if their alias is already present in the destination.
     *
     * @param keyStore     the certificates destination.
     * @param trustManager the certificates source.
     */
    public static void copyCertsFromKeyStoreToTrustManager(KeyStore keyStore, AbsCustomTrustManager trustManager) {
        log.trace(Mrk_Commons.COMM_SSL_UTILS, "Synchronizing certificate from key store to trust manager");

        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = keyStore.getCertificate(alias);
                if (cert == null) {
                    log.error(Mrk_Commons.COMM_SSL_UTILS, String.format("Can't find certificate for alias '%s'", alias));
                    continue;
                }
                trustManager.addCertificate(alias, cert);
            }

        } catch (KeyStoreException | AbsCustomTrustManager.UpdateException e) {
            e.printStackTrace();
        }
    }


    // Utils methods

    /**
     * Create directory for given path.
     * <p>
     * The path can define a directory or a file. If it represent a file, then
     * this method will create the parent directory.
     *
     * @param path string containing the path to create.
     */
    private static void dirExistOrCreate(String path) throws IOException {
        File f = new File(path).getAbsoluteFile();
        File dir = f.isDirectory() ? f : f.getParentFile();
        if (!dir.exists())
            if (!dir.mkdirs())
                throw new IOException(String.format("Can't create directory for path %s", path));

    }


    // Exception

    /**
     * Exceptions thrown on errors during JKS generation processes.
     */
    public static class GenerationException extends Throwable {
        public GenerationException(String msg, Exception e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions thrown on errors during JKS loading processes.
     */
    public static class LoadingException extends Throwable {
        public LoadingException(String msg, Exception e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions thrown on errors during JKS storing processes.
     */
    public static class StoreException extends Throwable {
        public StoreException(String msg, Exception e) {
            super(msg, e);
        }
    }

}

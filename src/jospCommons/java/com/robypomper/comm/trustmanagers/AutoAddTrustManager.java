package com.robypomper.comm.trustmanagers;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * TrustManager that add and accept any certificate to check.
 */
public class AutoAddTrustManager extends AbsCustomTrustManager {

    // X509TrustManager impl

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            checkTrusted(chain, authType);
        } catch (UpdateException ignore) {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            checkTrusted(chain, authType);
        } catch (UpdateException ignore) {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return getTrustManager().getAcceptedIssuers();
    }


    // Utils

    /**
     * Check if given certificate chain is trusted by current TrustManager, if
     * not then add the certificate to the TrustManager and re-check the
     * certificate.
     *
     * @param chain    the certificate chain to check.
     * @param authType authentication type based on certificate type.
     */
    public void checkTrusted(X509Certificate[] chain, String authType) throws CertificateException, UpdateException {
        if (getTrustManager().getAcceptedIssuers().length == 0)
            addCertificate("Client#" + chain[0].getSubjectDN(), chain[0]);

        try {
            getTrustManager().checkClientTrusted(chain, authType);

        } catch (CertificateException cx) {
            addCertificate("Client#" + chain[0].getSubjectDN(), chain[0]);
            getTrustManager().checkClientTrusted(chain, authType);
        }
    }

}

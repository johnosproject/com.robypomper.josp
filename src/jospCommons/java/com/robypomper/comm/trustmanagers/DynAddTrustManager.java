package com.robypomper.comm.trustmanagers;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * TrustManager that allow to add certificates at runtime.
 */
public class DynAddTrustManager extends AbsCustomTrustManager {

    // X509TrustManager impl

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        getTrustManager().checkClientTrusted(chain, authType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        getTrustManager().checkServerTrusted(chain, authType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return getTrustManager().getAcceptedIssuers();
    }

}

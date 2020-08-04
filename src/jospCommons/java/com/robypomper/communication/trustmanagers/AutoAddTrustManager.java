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

package com.robypomper.communication.trustmanagers;

import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * TrustManager that add and accept any certificate to check.
 */
public class AutoAddTrustManager extends AbsCustomTrustManager {

    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Constructor

    /**
     * Default constructor.
     */
    public AutoAddTrustManager() {
        super();
    }


    // X509TrustManager impl

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        checkTrusted(chain, authType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        checkTrusted(chain, authType);
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
    public void checkTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            if (getTrustManager().getAcceptedIssuers().length == 0)
                addCertificate("Client#" + chain[0].getSubjectDN(), chain[0]);

            try {
                getTrustManager().checkClientTrusted(chain, authType);

            } catch (CertificateException cx) {
                addCertificate("Client#" + chain[0].getSubjectDN(), chain[0]);
                getTrustManager().checkClientTrusted(chain, authType);
            }

        } catch (UpdateException e) {
            log.error(Mrk_Commons.COMM_SSL_UTILS, String.format("Error on add client certificate dynamically because %s", e.getMessage()));
        }
    }

}

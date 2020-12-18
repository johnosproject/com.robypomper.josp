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

package com.robypomper.josp.jod.comm;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.client.Client;
import com.robypomper.communication.client.standard.DefaultSSLClient;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.josp.clients.AbsGWsClient;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.clients.apis.obj.APIGWsClient;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.params.jospgws.O2SAccessInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.states.StateException;
import com.robypomper.log.Mrk_JOD;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;


/**
 * Client implementation for Gateway Object2Service connection.
 * <p>
 * This class provide a SSLClient to connect to the O2S Gw.
 */
public class JODGwO2SClient extends AbsGWsClient<O2SAccessInfo> {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    // JOD
    private final JODCommunication_002 jodComm;
    // Configs
    private final JODObjectInfo objInfo;
    private final APIGWsClient apiGWsClient;


    // Constructor

    /**
     * Default constructor for JOSP GW O2S client.
     * <p>
     * Generate the SSL context, request the GW's access info and  to use for O2S Gw connection.
     * It use the object's id as certificate id and load the O2S Gw certificate
     * to the {@link javax.net.ssl.TrustManager} used for the SSL context.
     *
     * @param jodComm      instance of the {@link JODCommunication}
     *                     that initialized this client. It will used to
     *                     process data received from the O2S Gw.
     * @param objInfo      the info of the represented object.
     */
    public JODGwO2SClient(JODCommunication_002 jodComm, JODObjectInfo objInfo, JCPAPIsClientObj jcpClient, String instanceId) throws GWsClientException {
        super(objInfo.getObjId(), jcpClient);
        this.jodComm = jodComm;
        this.objInfo = objInfo;
        this.apiGWsClient = new APIGWsClient(jcpClient, instanceId);

        log.info(Mrk_JOD.JOD_COMM_SUB, String.format("Initialized JSLGwO2SClient %s his instance of DefaultSSLClient for service '%s'", getWrappedClient() != null ? "and" : "but NOT", objInfo.getObjId()));
        if (isConnected()) {
            log.debug(Mrk_JOD.JOD_COMM_SUB, String.format("                           connected to GW's '%s:%d'", getServerAddr(), getServerPort()));
        }
    }


    // Getter configs

    protected String getClientType() {
        return CLIENT_TYPE_O2S;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerName() {
        return NAME_SERVER_O2S;
    }


    // Client connection methods - O2S/S2O Sub classing

    @Override
    protected O2SAccessInfo getAccessInfo() {
        try {
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Getting service GW client access info");
            log.trace(Mrk_JSL.JSL_COMM_SUB, "Getting JOSP Gw S2O access info for service's cloud client");
            O2SAccessInfo accessInfo = apiGWsClient.getO2SAccessInfo(getClientCert());
            log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("Service GW client access info returned: '%s:%d'", accessInfo.gwAddress, accessInfo.gwPort));
            return accessInfo;

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | CertificateEncodingException | JCPClient2.ResponseException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on getting service GW info because %s", e.getMessage()));
            return null;
        }
    }

    @Override
    protected Client initGWClient(O2SAccessInfo accessInfo) {
        try {
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Initializing service GW client");
            log.trace(Mrk_JSL.JSL_COMM_SUB, "Registering JOSP Gw S2O certificate for service's cloud client");
            Certificate gwCertificate = UtilsJKS.loadCertificateFromBytes(accessInfo.gwCertificate);
            getClientTrustManager().addCertificate(JCP_CERT_ALIAS, gwCertificate);

            // Init SSL client
            Client client = new DefaultSSLClient(getSSLContext(), getClientId(), accessInfo.gwAddress, accessInfo.gwPort, null, clientServerEvents, clientMessagingEvents, getProtocolName(), getServerName());
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Service GW client initialized");
            return client;

        } catch (UtilsJKS.LoadingException | AbsCustomTrustManager.UpdateException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on initializing service GW client because %s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    protected boolean connectGWClient(Client client, O2SAccessInfo accessInfo) {
        try {
            log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("Connecting service GW client at %s:%d", accessInfo.gwAddress, accessInfo.gwPort));
            client.connect();
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Service GW client connected");
            return true;

        } catch (IOException | AAAException | StateException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on connecting service GW client because %s", e.getMessage()), e);
            return false;
        }
    }


    // Messages methods - O2S/S2O Sub classing

    @Override
    protected boolean processData(String readData, JOSPPerm.Connection connType) {
        return jodComm.processFromServiceMsg(readData, connType);
    }

}

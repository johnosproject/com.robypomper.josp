/* *****************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************** */

package com.robypomper.josp.jsl.comm;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.client.Client;
import com.robypomper.communication.client.standard.DefaultSSLClient;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.josp.clients.AbsGWsClient;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.clients.apis.srv.APIGWsClient;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.params.jospgws.S2OAccessInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.states.StateException;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;


/**
 * Client implementation for Gateway Service2Object connection.
 * <p>
 * This class provide a SSLClient to connect to the S2O Gw.
 */
public class JSLGwS2OClient extends AbsGWsClient<S2OAccessInfo> {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    // JSL
    private final JSLCommunication_002 jslComm;
    // Configs
    private final JSLServiceInfo srvInfo;
    private final APIGWsClient apiGWsClient;


    // Constructor

    /**
     * Generate the SSL context to use for O2S Gw connection.
     * It use the object's id as certificate id and load the S2O Gw certificate
     * to the {@link javax.net.ssl.TrustManager} used for the SSL context.
     *
     * @param jslComm instance of the {@link JSLCommunication}
     *                that initialized this client. It will used to
     *                process data received from the O2S Gw.
     * @param srvInfo the info of the represented service.
     */
    public JSLGwS2OClient(JSLCommunication_002 jslComm, JSLServiceInfo srvInfo, JCPAPIsClientSrv jcpClient, String instanceId) throws GWsClientException {
        super(srvInfo.getFullId(), jcpClient);
        this.jslComm = jslComm;
        this.srvInfo = srvInfo;
        this.apiGWsClient = new APIGWsClient(jcpClient, instanceId);

        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Initialized JSLGwS2OClient %s his instance of DefaultSSLClient for service '%s'", getWrappedClient() != null ? "and" : "but NOT", srvInfo.getSrvId()));
        if (isConnected()) {
            log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("                           connected to GW's '%s:%d'", getServerAddr(), getServerPort()));
        }
    }


    // Getter configs

    protected String getClientType() {
        return CLIENT_TYPE_S2O;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerName() {
        return NAME_SERVER_S2O;
    }


    // Client connection methods - O2S/S2O Sub classing

    @Override
    protected S2OAccessInfo getAccessInfo() {
        try {
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Getting service GW client access info");
            log.trace(Mrk_JSL.JSL_COMM_SUB, "Getting JOSP Gw S2O access info for service's cloud client");
            S2OAccessInfo accessInfo = apiGWsClient.getS2OAccessInfo(getClientCert());
            log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("Service GW client access info returned: '%s:%d'", accessInfo.gwAddress, accessInfo.gwPort));
            return accessInfo;

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | CertificateEncodingException | JCPClient2.ResponseException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on getting service GW info because %s", e.getMessage()));
            return null;
        }
    }

    @Override
    protected Client initGWClient(S2OAccessInfo accessInfo) {
        try {
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Initializing service GW client");
            log.trace(Mrk_JSL.JSL_COMM_SUB, "Registering JOSP Gw S2O certificate for service's cloud client");
            Certificate gwCertificate = UtilsJKS.loadCertificateFromBytes(accessInfo.gwCertificate);
            getClientTrustManager().addCertificate(JCP_CERT_ALIAS, gwCertificate);

            // Init SSL client
            Client client = new DefaultSSLClient(getSSLContext(), srvInfo.getFullId(), accessInfo.gwAddress, accessInfo.gwPort, null, clientServerEvents, clientMessagingEvents, getProtocolName(), getServerName());
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Service GW client initialized");
            return client;

        } catch (UtilsJKS.LoadingException | AbsCustomTrustManager.UpdateException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on initializing service GW client because %s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    protected boolean connectGWClient(Client client, S2OAccessInfo accessInfo) {
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
        return jslComm.processFromObjectMsg(readData, connType);
    }

}

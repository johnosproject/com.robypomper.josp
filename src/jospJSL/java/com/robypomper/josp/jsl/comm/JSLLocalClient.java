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
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.AbsClientWrapper;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.DefaultClientEvents;
import com.robypomper.communication.client.standard.DefaultSSLClientCertSharing;
import com.robypomper.communication.client.standard.SSLCertSharingClient;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Client implementation for JOD local server.
 * <p>
 * This class provide a {@link AbsClientWrapper} with {@link DefaultSSLClientCertSharing} (a client that allow to share
 * client and server certificates).
 */
@SuppressWarnings("un1used")
public class JSLLocalClient extends AbsClientWrapper {

    // Class constants

    public static final String NAME_PROTO = "josp-local";
    public static final String NAME_SERVER = "JOSP JOD Local Server";
    public static final String CERT_ALIAS = "JSL-Cert-Local";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    // JSL
    private final JSLCommunication_002 communication;
    private JSLRemoteObject remoteObject = null;


    // Constructor

    /**
     * Default constructor that initialize the internal {@link DefaultSSLClientCertSharing}.
     *
     * @param communication instance of the {@link JSLCommunication}
     *                      that initialized this client. It will used to
     *                      process data received from the O2S Gw.
     * @param srvFullId     the represented service's full id (srv, usr and instance ids).
     * @param address       the JOD local server address to connect with.
     * @param port          the JOD local server port to connect with.
     * @param pubCertFile   the file path of current client's public certificate.
     */
    public JSLLocalClient(JSLCommunication_002 communication, String srvFullId,
                          String address, int port, String pubCertFile) {
        super(srvFullId);
        this.communication = communication;

        try {
            DefaultSSLClientCertSharing client = new DefaultSSLClientCertSharing(srvFullId, address, port,
                    CERT_ALIAS, pubCertFile,
                    new JSLLocalClientMessagingListener(),
                    NAME_PROTO, NAME_SERVER
            );
            setWrappedClient(client);

        } catch (SSLCertSharingClient.SSLCertClientException | UtilsJKS.LoadingException | UtilsSSL.GenerationException | UtilsJKS.StoreException | UtilsJKS.GenerationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Initialized JSLLocalClient instance for '%s'", srvFullId));
        log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("                                    on server '%s:%d'", address, port));
    }


    // JSL Local Client methods

    /**
     * When created, add corresponding JSLRemoteObject to current local client.
     *
     * @param remoteObject the JSLRemoteObject instance that use current local client
     *                     to communicate with object.
     */
    public void setRemoteObject(JSLRemoteObject remoteObject) {
        if (this.remoteObject != null)
            throw new IllegalArgumentException("Can't set JSLRemoteObject twice for JSLLocalClient.");
        this.remoteObject = remoteObject;
    }

    /**
     * Version of method {@link #getObjId()} that do NOT throws exceptions.
     *
     * @return the represented server's object id.
     */
    public String tryObjId() {
        try {
            return getServerId();
        } catch (ServerNotConnectedException e) {
            return null;
        }
    }

    /**
     * The object id.
     *
     * @return the represented server's object id.
     */
    public String getObjId() throws ServerNotConnectedException {
        return getServerId();
    }


    // Getter configs

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProtocolName() {
        return NAME_PROTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerName() {
        return NAME_SERVER;
    }


    // Messages methods - Client's wrapping override methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(byte[] data) throws ServerNotConnectedException {
        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Data '%s...' send to object '%s' from '%s' service", new String(data).substring(0, new String(data).indexOf("\n")), getServerId(), getClientId()));
        super.sendData(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(String data) throws ServerNotConnectedException {
        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Data '%s...' send to object '%s' from '%s' service", data.substring(0, data.indexOf("\n")), getServerId(), getClientId()));
        super.sendData(data);
    }


    // Messages methods - Processing data

    private boolean processData(String readData, JOSPPerm.Connection connType) {
        return communication.processFromObjectMsg(readData, connType);
    }


    // Messages methods ClientMessagingEvents listener

    /**
     * Link the {@link #onDataReceived(String)} event to
     * {@link #processData(String, JOSPPerm.Connection)} method.
     */
    private class JSLLocalClientMessagingListener extends DefaultClientEvents implements ClientMessagingEvents {

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onDataSend(byte[] writtenData) {
        }

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onDataSend(String writtenData) {
        }

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public boolean onDataReceived(byte[] readData) {
            return false;
        }

        /**
         * {@inheritDoc}
         * <p>
         * Link to the {@link #processData(String, JOSPPerm.Connection)} method.
         */
        @Override
        public boolean onDataReceived(String readData) {
            return processData(readData, JOSPPerm.Connection.OnlyLocal);
        }

    }

}

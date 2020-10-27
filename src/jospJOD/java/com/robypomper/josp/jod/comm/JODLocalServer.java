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
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.server.CertSharingSSLServer;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.communication.server.events.DefaultServerEvent;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import com.robypomper.communication.server.standard.SSLCertServer;
import com.robypomper.josp.jod.events.Events;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Server that listen and process local client (JSL) connections.
 * <p>
 * This class provide a {@link CertSharingSSLServer} (a server that allow to share
 * client and server certificates).
 */
public class JODLocalServer implements Server {

    // Class constants

    public static final String CERT_ALIAS = "JOD-Cert-Local";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODObjectInfo objInfo;
    private final JODCommunication communication;
    private final JODPermissions permissions;
    private final CertSharingSSLServer server;
    private final List<JODLocalClientInfo> localClients = new ArrayList<>();


    // Constructor

    /**
     * Default constructor that initialize the internal {@link CertSharingSSLServer}.
     *
     * @param communication instance of the {@link JODCommunication}
     *                      that initialized this client. It will used to
     *                      process data received from the O2S Gw.
     * @param objInfo       the represented object's id.
     * @param permissions
     * @param port          the port used by the server to listen for new
     *                      connections.
     * @param pubCertFile   the file path of current server's public certificate.
     */
    public JODLocalServer(JODCommunication communication, JODObjectInfo objInfo,
                          JODPermissions permissions, int port, String pubCertFile) {
        this.objInfo = objInfo;
        this.communication = communication;
        this.permissions = permissions;

        try {
            server = new CertSharingSSLServer(objInfo.getObjId(), port,
                    CERT_ALIAS, pubCertFile, true,
                    null,
                    new JODLocalServerClientListener(),
                    new JODLocalServerMessagingListener()
            );

        } catch (SSLCertServer.SSLCertServerException | UtilsJKS.LoadingException | UtilsSSL.GenerationException | UtilsJKS.StoreException | UtilsJKS.GenerationException e) {
            log.info(Mrk_JOD.JOD_COMM_SUB, String.format("Error on initializing JODLocalServer for '%s' object", objInfo.getObjId()));
            throw new RuntimeException(e);
        }

        log.info(Mrk_JOD.JOD_COMM_SUB, String.format("Initialized JODLocalServer instance for '%s' object on port '%s'", objInfo.getObjId(), port));
        log.debug(Mrk_JOD.JOD_COMM_SUB, String.format("                                          with %s as server implementation", server.getClass().getSimpleName()));
    }


    // Connections mngm

    /**
     * Return all server's {@link JODLocalClientInfo}.
     *
     * @return an unmodifiable array containing client's info.
     */
    public List<JODLocalClientInfo> getLocalClientsInfo() {
        return Collections.unmodifiableList(localClients);
    }

    /**
     * Return the {@link JODLocalClientInfo} of the given <code>serviceId</code>.
     *
     * @param serviceId the required service's id.
     * @return the {@link JODLocalClientInfo} or <code>null</code> if given id
     * not found.
     */
    private JODLocalClientInfo getLocalConnectionByServiceId(String serviceId) {
        for (JODLocalClientInfo conn : localClients)
            if (conn.getClientId().equals(serviceId))
                return conn;

        return null;
    }

    /**
     * Return the {@link JODLocalClientInfo} of the given <code>clientId</code>.
     *
     * @param clientFullAddress the required client's id.
     * @return the {@link JODLocalClientInfo} or <code>null</code> if given id
     * not found.
     */
    private JODLocalClientInfo getLocalConnectionByClientFullAddress(String clientFullAddress) {
        for (JODLocalClientInfo conn : localClients)
            if (conn.getLocalFullAddress().equals(clientFullAddress))
                return conn;

        return null;
    }

    /**
     * Process the new cient connection.
     * <p>
     * Generate a new {@link JODLocalClientInfo} from the given {@link ClientInfo}
     * and check if another client from the same instance is already known. If it
     * is then check if the already known client is still connected, then discard
     * the new client; else it relplace the old client with the new one.
     *
     * @param client the new client's info.
     */
    private void onClientConnection(ClientInfo client) {
        log.info(Mrk_JOD.JOD_COMM, String.format("Connected client '%s' to '%s' object", client.getPeerFullAddress(), objInfo.getObjId()));

        JODLocalClientInfo locConn = new DefaultJODLocalClientInfo(client);

        log.debug(Mrk_JOD.JOD_COMM, String.format("Registering service '%s' of '%s' client to '%s' object", locConn.getClientId(), client.getPeerFullAddress(), objInfo.getObjId()));
        synchronized (localClients) {
            for (JODLocalClientInfo c : localClients)
                if (c.getClientId().equals(locConn.getClientId())) {
                    log.debug(Mrk_JOD.JOD_COMM, String.format("Service '%s' already registered to '%s' object", locConn.getClientId(), objInfo.getObjId()));

                    log.debug(Mrk_JOD.JOD_COMM, String.format("Checking service '%s' connection to '%s' object", locConn.getClientId(), objInfo.getObjId()));
                    boolean wasConnected = c.isConnected();
                    if (!wasConnected) {
                        localClients.remove(c);
                        localClients.add(locConn);
                        sendObjectPresentation(locConn);
                        log.trace(Mrk_JOD.JOD_COMM, String.format("Updated connection because service '%s' was NOT connected to '%s' object", locConn.getClientId(), objInfo.getObjId()));
                        Events.registerLocalConn("Local JSL updated connection", locConn,client);

                    } else {
                        try {
                            locConn.disconnectLocal();
                        } catch (JODCommunication.LocalCommunicationException e) {
                            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on disconnect new client '%s' because %s", client.getPeerFullAddress(), e.getMessage()), e);
                        }
                        log.trace(Mrk_JOD.JOD_COMM, String.format("New client discarded because service '%s' was already connected to '%s' object", locConn.getClientId(), objInfo.getObjId()));
                        Events.registerLocalConn("Local JSL refused connection because already connected service", locConn,client);
                    }

                    log.debug(Mrk_JOD.JOD_COMM, String.format("Service '%s' connection to '%s' object checked and%s updated", locConn.getClientId(), objInfo.getObjId(), wasConnected ? " NOT" : ""));
                    return;
                }

            localClients.add(locConn);
        }
        sendObjectPresentation(locConn);
        log.debug(Mrk_JOD.JOD_COMM, String.format("Service '%s' with client '%s' registered to '%s' object", locConn.getClientId(), locConn.getClientFullAddress(), objInfo.getObjId()));
        log.info(Mrk_JOD.JOD_COMM, String.format("Registered service '%s' to '%s' object", locConn.getClientId(), objInfo.getObjId()));
        Events.registerLocalConn("Local JSL connected",locConn,client);
    }

    private void sendObjectPresentation(JODLocalClientInfo locConn) {
        try {
            communication.sendToSingleLocalService(locConn, JOSPProtocol_ObjectToService.createObjectInfoMsg(objInfo.getObjId(), objInfo.getObjName(), objInfo.getJODVersion(), objInfo.getOwnerId(), objInfo.getModel(), objInfo.getBrand(), objInfo.getLongDescr(), communication.isCloudConnected()), JOSPPerm.Type.Status);
            communication.sendToSingleLocalService(locConn, JOSPProtocol_ObjectToService.createObjectStructMsg(objInfo.getObjId(), objInfo.getStructForJSL()), JOSPPerm.Type.Status);
            communication.sendToSingleLocalService(locConn, JOSPProtocol_ObjectToService.createObjectPermsMsg(objInfo.getObjId(), objInfo.getPermsForJSL()), JOSPPerm.Type.CoOwner);

            JOSPPerm.Type permType = permissions.getServicePermission(locConn.getSrvId(), locConn.getUsrId(), JOSPPerm.Connection.OnlyLocal);
            communication.sendToSingleLocalService(locConn, JOSPProtocol_ObjectToService.createServicePermMsg(objInfo.getObjId(), permType, JOSPPerm.Connection.OnlyLocal), permType);

        } catch (JODStructure.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on serialize object's structure to local service because %s", e.getMessage()), e);
        } catch (JODCommunication.ServiceNotConnected e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on sending object's presentation to local service because %s", e.getMessage()), e);
        }
    }

    /**
     * Process the client disconnection.
     *
     * @param client the disconnected client's info.
     */
    private void onClientDisconnection(ClientInfo client) {
        log.info(Mrk_JOD.JOD_COMM, String.format("Disconnected client '%s' for service '%s' from '%s' object", client.getPeerFullAddress(), client.getClientId(), objInfo.getObjId()));

        JODLocalClientInfo locConn;
        synchronized (localClients) {
            locConn = getLocalConnectionByServiceId(client.getClientId());
        }
        if (locConn == null) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Disconnected client '%s' for service '%s' was not present in '%s' object registered clients list", client.getPeerFullAddress(), client.getClientId(), objInfo.getObjId()));
            return;
        }

        if (locConn.isConnected()) {
            log.info(Mrk_JOD.JOD_COMM, String.format("Service '%s' still connected to '%s' object", locConn.getClientId(), objInfo.getObjId()));
            //Events.registerLocalDisc("Local JSL discharged", locConn, client);
        }
        else {
            log.info(Mrk_JOD.JOD_COMM, String.format("Service '%s' disconnected to '%s' object", locConn.getClientId(), objInfo.getObjId()));
            Events.registerLocalDisc("Local JSL disconnected", locConn, client);
        }
    }


    // Process incoming messages

    /**
     * Forward received data to the {@link JODCommunication} instance.
     * <p>
     * All data received by this method are send from local JSL services and
     * include action commands and service requests (like objectStruct or
     * objectInfo requests).
     *
     * @param client   the sender client's info.
     * @param readData the message string received from <code>client</code> client.
     * @return always true.
     */
    private boolean onDataReceived(ClientInfo client, String readData) {
        return communication.processFromServiceMsg(readData, JOSPPerm.Connection.OnlyLocal);
    }


    // Server's wrapping methods

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getAddress() {
        return server.getAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return server.getPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerId() {
        return server.getServerId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRunning() {
        return server.isRunning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws ListeningException {
        log.info(Mrk_JOD.JOD_COMM_SUB, String.format("Start local server for '%s' object", objInfo.getObjId()));
        server.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        log.info(Mrk_JOD.JOD_COMM_SUB, String.format("Stop local object's server '%s' on port '%d'", objInfo.getObjId(), getPort()));
        server.stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(String clientId, byte[] data) throws ServerStoppedException, ClientNotFoundException, ClientNotConnectedException {
        server.sendData(clientId, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(ClientInfo client, byte[] data) throws ServerStoppedException, ClientNotConnectedException {
        server.sendData(client, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(String clientId, String data) throws ServerStoppedException, ClientNotFoundException, ClientNotConnectedException {
        server.sendData(clientId, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(ClientInfo client, String data) throws ServerStoppedException, ClientNotConnectedException {
        server.sendData(client, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCliByeMsg(byte[] data) {
        return server.isCliByeMsg(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClientInfo> getClients() {
        return server.getClients();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo findClientById(String clientId) {
        return server.findClientById(clientId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo getClientById(String clientId) throws ClientNotFoundException {
        return server.getClientById(clientId);
    }


    // Server event listeners

    /**
     * Link the {@link #onClientConnection(ClientInfo)} and {@link #onClientDisconnection(ClientInfo)}
     * events to {@link JODLocalServer#onClientConnection(ClientInfo)} and
     * {@link JODLocalServer#onClientDisconnection(ClientInfo)} methods.
     */
    private class JODLocalServerClientListener extends DefaultServerEvent implements ServerClientEvents {

        /**
         * {@inheritDoc}
         * <p>
         * Link to the {@link JODLocalServer#onClientConnection(ClientInfo)} method.
         */
        @Override
        public void onClientConnection(ClientInfo client) {
            JODLocalServer.this.onClientConnection(client);
        }

        /**
         * {@inheritDoc}
         * <p>
         * Link to the {@link JODLocalServer#onClientDisconnection(ClientInfo)} method.
         */
        @Override
        public void onClientDisconnection(ClientInfo client) {
            JODLocalServer.this.onClientDisconnection(client);
        }

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onClientServerDisconnected(ClientInfo client) {

        }

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onClientGoodbye(ClientInfo client) {

        }

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onClientTerminated(ClientInfo client) {

        }

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onClientError(ClientInfo client, Throwable e) {

        }
    }

    /**
     * Link the {@link #onDataReceived(ClientInfo, String)} event to
     * {@link JODLocalServer#onDataReceived(ClientInfo, String)} ()} method.
     */
    private class JODLocalServerMessagingListener extends DefaultServerEvent implements ServerMessagingEvents {

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onDataSend(ClientInfo client, byte[] writtenData) {

        }

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onDataSend(ClientInfo client, String writtenData) {

        }

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public boolean onDataReceived(ClientInfo client, byte[] readData) {
            return false;
        }

        /**
         * {@inheritDoc}
         * <p>
         * Link to the {@link JODLocalServer#onDataReceived(ClientInfo, String)} method.
         */
        @Override
        public boolean onDataReceived(ClientInfo client, String readData) {
            return JODLocalServer.this.onDataReceived(client, readData);
        }
    }

}

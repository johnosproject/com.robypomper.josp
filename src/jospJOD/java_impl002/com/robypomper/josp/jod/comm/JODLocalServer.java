package com.robypomper.josp.jod.comm;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.server.CertSharingSSLServer;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.communication.server.events.DefaultServerEvent;
import com.robypomper.communication.server.events.LogServerLocalEventsListener;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import com.robypomper.communication.server.standard.SSLCertServer;
import com.robypomper.josp.jod.systems.JODCommunication;

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

    private final JODCommunication communication;
    private final CertSharingSSLServer server;
    private final List<JODLocalClientInfo> localClients = new ArrayList<>();


    // Constructor

    /**
     * Default constructor that initialize the internal {@link CertSharingSSLServer}.
     *
     * @param communication instance of the {@link JODCommunication}
     *                      that initialized this client. It will used to
     *                      process data received from the O2S Gw.
     * @param objId         the represented object's id.
     * @param port          the port used by the server to listen for new
     *                      connections.
     * @param ksFile        the file path of current server {@link java.security.KeyStore}.
     * @param ksPass        the password of the KeyStore at <code>ksFile</code>.
     * @param pubCertFile   the file path of current server's public certificate.
     */
    public JODLocalServer(JODCommunication communication, String objId,
                          int port, String ksFile, String ksPass, String pubCertFile) {
        this.communication = communication;

        try {
            server = new CertSharingSSLServer(objId, port,
                    ksFile, ksPass, CERT_ALIAS, pubCertFile, true,
                    new LogServerLocalEventsListener(),
                    new JODLocalServerClientListener(),
                    new JODLocalServerMessagingListener()
            );

        } catch (SSLCertServer.SSLCertServerException | UtilsJKS.LoadingException | UtilsSSL.GenerationException | UtilsJKS.StoreException | UtilsJKS.GenerationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
     * Return the {@link JODLocalClientInfo} of the given <code>clientId</code>.
     *
     * @param clientId the required client's id.
     * @return the {@link JODLocalClientInfo} or <code>null</code> if given id
     * not found.
     */
    private JODLocalClientInfo getLocalConnectionByClientId(String clientId) {
        for (JODLocalClientInfo conn : localClients)
            if (conn.getClientId().equals(clientId))
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
        System.out.println(String.format("DEB: connected client %s.", client.getClientId()));

        JODLocalClientInfo locConn = new DefaultJODLocalClientInfo(client);
        for (JODLocalClientInfo c : localClients)
            if (c.getClientId().equals(locConn.getClientId())) {
                if (!c.isConnected()) {
                    localClients.remove(c);
                    localClients.add(locConn);
                    System.out.println(String.format("DEB: already existing client %s connection but not connected, updated.", client.getClientId()));
                } else {
                    System.out.println(String.format("DEB: already existing client %s connection, discarded.", client.getClientId()));
                }
                return;
            }

        localClients.add(locConn);
        System.out.println(String.format("INF: added connection for service %s and user %s.", locConn.getSrvId(), locConn.getUsrId()));
    }

    /**
     * Process the client disconnection.
     *
     * @param client the disconnected client's info.
     */
    private void onClientDisconnection(ClientInfo client) {
        JODLocalClientInfo locConn = getLocalConnectionByClientId(client.getClientId());
        if (locConn == null) {
            System.out.println(String.format("WAR: client %s was not present in the local client list.", client.getClientId()));
            return;
        }

        if (locConn.isConnected())
            System.out.println(String.format("WAR: client %s disconnected, but object '%s' still connected via other client.", client.getPeerFullAddress(), locConn.getClientId()));
        else
            System.out.println(String.format("WAR: client %s and his object '%s' disconnected.", client.getPeerFullAddress(), locConn.getClientId()));

        assert !client.isConnected();
    }


    // Process incoming messages

    /**
     * Forward received data to the {@link JODCommunication}
     * instance.
     *
     * @param client   the sender client's info.
     * @param readData the message string received from <code>client</code> client.
     * @return always true.
     */
    private boolean onDataReceived(ClientInfo client, String readData) {
        communication.forwardAction(readData);
        return true;
    }


    // Server's wrapping methods

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
        server.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
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

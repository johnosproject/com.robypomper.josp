package com.robypomper.josp.jsl.comm;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.CertSharingSSLClient;
import com.robypomper.communication.client.Client;
import com.robypomper.communication.client.ServerInfo;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.ClientServerEvents;
import com.robypomper.communication.client.events.DefaultClientEvents;
import com.robypomper.communication.client.standard.SSLCertClient;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;


/**
 * Client implementation for JOD local server.
 * <p>
 * This class provide a {@link CertSharingSSLClient} (a client that allow to share
 * client and server certificates).
 */
public class JSLLocalClient implements Client {

    // Class constants

    public static final String CERT_ALIAS = "JSL-Cert-Local";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLCommunication communication;
    private final CertSharingSSLClient client;
    private JSLRemoteObject remoteObject = null;


    // Constructor

    /**
     * Default constructor that initialize the internal {@link CertSharingSSLClient}.
     *
     * @param communication instance of the {@link JSLCommunication}
     *                      that initialized this client. It will used to
     *                      process data received from the O2S Gw.
     * @param srvFullId     the represented service's full id (srv, usr and instance ids).
     * @param address       the JOD local server address to connect with.
     * @param port          the JOD local server port to connect with.
     * @param ksFile        the file path of current client {@link java.security.KeyStore}.
     * @param ksPass        the password of the KeyStore at <code>ksFile</code>.
     * @param pubCertFile   the file path of current client's public certificate.
     */
    public JSLLocalClient(JSLCommunication communication, String srvFullId,
                          InetAddress address, int port, String ksFile, String ksPass, String pubCertFile) {
        this.communication = communication;

        try {
            client = new CertSharingSSLClient(srvFullId, address, port,
                    ksFile, ksPass, CERT_ALIAS, pubCertFile,
                    null,
                    new JSLLocalClientServerListener(),
                    new JSLLocalClientMessagingListener()
            );

        } catch (SSLCertClient.SSLCertClientException | UtilsJKS.LoadingException | UtilsSSL.GenerationException | UtilsJKS.StoreException | UtilsJKS.GenerationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Initialized JSLLocalClient instance for '%s'", srvFullId));
        log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("                                    on server '%s:%d'", address, port));
    }

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


    // Object info

    /**
     * The object id.
     *
     * @return the represented server's object id.
     */
    public String getObjId() {
        return getServerInfo().getServerId();
    }


    // Connections mngm

    /**
     * Placeholder method
     */
    private void onServerConnection() {
        // ToDo: implement onServerConnection method
    }


    // Process incoming messages

    /**
     * Forward received data to the {@link JSLCommunication}
     * instance.
     *
     * @param readData the message string received from the connected server client.
     * @return always true.
     */
    private boolean onDataReceived(String readData) {
        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Data '%s...' received from object '%s' to '%s' service", readData.substring(0, readData.indexOf("\n")), getServerInfo().getServerId(), client.getClientId()));

        if (remoteObject.processUpdate(readData))
            return true;

        if (remoteObject.processServiceRequestResponse(readData))
            return true;

        log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on processing received data '%s...' from server '%s' to '%s' service because unknown request", readData.substring(0, 10), getServerInfo().getServerId(), client.getClientId()));
        return false;
    }


    // Client's wrapping methods

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getServerAddr() {
        return client.getServerAddr();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServerPort() {
        return client.getServerPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerInfo getServerInfo() {
        return client.getServerInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getClientAddr() {
        return client.getClientAddr();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getClientPort() {
        return client.getClientPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientId() {
        return client.getClientId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return client.isConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws ConnectionException {
        client.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Disconnect local communication service '%s''s client from address '%s:%d'", getClientId(), getClientAddr(), getClientPort()));
        client.disconnect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(byte[] data) throws ServerNotConnectedException {
        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Data '%s...' send to server '%s' from '%s' service", new String(data).substring(0, new String(data).indexOf("\n")), getServerInfo().getServerId(), client.getClientId()));
        client.sendData(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(String data) throws ServerNotConnectedException {
        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Data '%s...' send to server '%s' from '%s' service", data.substring(0, data.indexOf("\n")), getServerInfo().getServerId(), client.getClientId()));
        client.sendData(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSrvByeMsg(byte[] data) {
        return client.isSrvByeMsg(data);
    }


    // Client event listeners

    /**
     * Link the {@link #onServerConnection()} event to
     * {@link JSLLocalClient#onServerConnection()} methods.
     */
    private class JSLLocalClientServerListener extends DefaultClientEvents implements ClientServerEvents {

        /**
         * {@inheritDoc}
         * <p>
         * Link to the {@link JSLLocalClient#onServerConnection()} method.
         */
        @Override
        public void onServerConnection() {
            JSLLocalClient.this.onServerConnection();
        }

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onServerDisconnection() {}

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onServerClientDisconnected() {}

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onServerGoodbye() {}

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onServerTerminated() {}

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onServerError(Throwable e) {}

    }

    /**
     * Link the {@link #onDataReceived(String)} event to
     * {@link JSLLocalClient#onDataReceived(String)} ()} method.
     */
    private class JSLLocalClientMessagingListener extends DefaultClientEvents implements ClientMessagingEvents {

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onDataSend(byte[] writtenData) {}

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onDataSend(String writtenData) {}

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
         * Link to the {@link JSLLocalClient#onDataReceived(String)} method.
         */
        @Override
        public boolean onDataReceived(String readData) {
            return JSLLocalClient.this.onDataReceived(readData);
        }

    }

}

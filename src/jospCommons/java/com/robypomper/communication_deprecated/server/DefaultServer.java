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

package com.robypomper.communication_deprecated.server;

import com.robypomper.communication_deprecated.CommunicationBase;
import com.robypomper.communication_deprecated.client.AbsClient;
import com.robypomper.communication_deprecated.peer.DefaultHeartBeat;
import com.robypomper.communication_deprecated.peer.HeartBeatConfigs;
import com.robypomper.communication_deprecated.peer.HeartBeatWrapperList;
import com.robypomper.communication_deprecated.peer.PeerInfo;
import com.robypomper.communication_deprecated.server.events.ServerClientEvents;
import com.robypomper.communication_deprecated.server.events.ServerLocalEvents;
import com.robypomper.communication_deprecated.server.events.ServerMessagingEvents;
import com.robypomper.java.JavaDate;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Default implementation of Server interface.
 */
public class DefaultServer extends CommunicationBase implements Server {

    // Class constants

    public static final String TH_SRV_NAME_FORMAT = "_SRV_%s";
    public static final String ID_CLI_FORMAT = "CL-%s:%d";
    public static final String MSG_BYE_SRV_STR = "byesrv";
    public static final byte[] MSG_BYE_SRV = MSG_BYE_SRV_STR.getBytes(PeerInfo.CHARSET);


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final String serverId;
    private final int port;
    private final ServerLocalEvents sle;
    private final ServerClientEvents sce;
    private final ServerMessagingEvents sme;
    private final List<ClientInfo> clients = new ArrayList<>();
    private Thread serverThread = null;
    private boolean mustShutdown = false;
    private ServerSocket serverSocket = null;
    private final boolean delimiter = true;
    private final boolean sleep = false;
    // Last connections
    private Date lastStartup;
    private Date lastShutdown;
    private Date lastConnection;
    private ClientInfo lastConnectionClient;
    private Date lastDisconnection;
    private ClientInfo lastDisconnectionClient;
    private Date lastHeartbeat;
    private ClientInfo lastHeartbeatClient;
    private Date lastHeartbeatFailed;
    private ClientInfo lastHeartbeatFailedClient;
    private Date lastDataReceived;
    private ClientInfo lastDataReceivedClient;
    private Date lastDataSend;
    private ClientInfo lastDataSendClient;
    // Heartbeat
    private final HeartBeatWrapperList heartbeat = new HeartBeatWrapperList();


    // Constructor

    /**
     * Default constructor that initialize a server on <code>port</code> port
     * and with given id.
     *
     * @param serverId                      the server id.
     * @param port                          the server's port.
     * @param serverMessagingEventsListener the tx and rx messaging listener.
     */
    public DefaultServer(String serverId, int port, ServerMessagingEvents serverMessagingEventsListener) {
        this(serverId, port, null, null, serverMessagingEventsListener);
    }

    /**
     * Full constructor that initialize a server on <code>port</code> port
     * and with given id.
     *
     * @param serverId                      the server id.
     * @param port                          the server's port.
     * @param serverLocalEventsListener     the local server events listener.
     * @param serverClientEventsListener    the clients events listener.
     * @param serverMessagingEventsListener the tx and rx messaging listener.
     */
    public DefaultServer(String serverId, int port,
                         ServerLocalEvents serverLocalEventsListener,
                         ServerClientEvents serverClientEventsListener,
                         ServerMessagingEvents serverMessagingEventsListener) {
        this.serverId = serverId;
        this.port = port;
        this.sle = serverLocalEventsListener;
        if (this.sle != null) this.sle.setServer(this);
        this.sce = serverClientEventsListener;
        if (this.sce != null) this.sce.setServer(this);
        if (serverMessagingEventsListener == null)
            throw new IllegalArgumentException("DefaultServer can't be initialized with a null ServerMessagingEvents param.");
        this.sme = serverMessagingEventsListener;
        this.sme.setServer(this);

        log.info(Mrk_Commons.COMM_SRV, String.format("Initialized DefaultServer/%s instance for '%s' server on '%d' port", this.getClass().getSimpleName(), getServerId(), getPort()));
        log.debug(Mrk_Commons.COMM_SRV, String.format("                                          with %s, %s and %s listeners",
                sle != null ? sle.getClass() : "no SLE",
                sce != null ? sce.getClass() : "no SCE",
                sme.getClass()
        ));
    }


    // Server getter

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getAddress() {
        return serverSocket != null ? serverSocket.getInetAddress() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerId() {
        return serverId;
    }


    // Server listening methods

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRunning() {
        return serverSocket != null && serverThread != null && serverThread.isAlive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastStartup() {
        return lastStartup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastShutdown() {
        return lastShutdown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastClientConnection() {
        return lastConnection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo getLastClientConnectionClient() {
        return lastConnectionClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastClientDisconnection() {
        return lastDisconnection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo getLastClientDisconnectionClient() {
        return lastDisconnectionClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastHeartbeat() {
        return lastHeartbeat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo getLastHeartbeatClient() {
        return lastHeartbeatClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastHeartbeatFailed() {
        return lastHeartbeatFailed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo getLastHeartbeatClientFailed() {
        return lastHeartbeatFailedClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastDataReceived() {
        return lastDataReceived;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo getLastDataReceivedClient() {
        return lastDataReceivedClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastDataSend() {
        return lastDataSend;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo getLastDataSendClient() {
        return lastDataSendClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws ListeningException {
        if (isRunning()) {
            log.warn(Mrk_Commons.COMM_SRV, String.format("Server '%s' already started", getServerId()));
            return;
        }

        log.info(Mrk_Commons.COMM_SRV, String.format("Start server '%s' of '%s' on port '%d'", getServerId(), this.getClass().getSimpleName(), getPort()));

        try {
            serverSocket = generateAndBoundServerSocket();
        } catch (IOException e) {
            log.warn(Mrk_Commons.COMM_SRV, String.format("Error on starting server '%s' because %s", getServerId(), e.getMessage()), e);
            throw new ListeningException(String.format("Can't start server '%s' because %s", getServerId(), e.getMessage()));
        }

        serverThread = new Thread(this::infiniteLoop);
        serverThread.setName(String.format(TH_SRV_NAME_FORMAT, getServerId()));
        log.debug(Mrk_Commons.COMM_SRV, String.format("Starting thread server loop for '%s' server", getServerId()));
        serverThread.start();

        updateLastStartup();
        if (sle != null) sle.onStarted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (!isRunning()) {
            log.warn(Mrk_Commons.COMM_SRV, String.format("Server '%s' already stopped", getServerId()));
            return;
        }

        log.info(Mrk_Commons.COMM_SRV, String.format("Halt server '%s'", getServerId()));

        // Terminate server thread
        mustShutdown = true;
        serverThread.interrupt();

        // Close server socket
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.warn(Mrk_Commons.COMM_SRV, String.format("Listener socket for server '%s' not closed", getServerId()));
            if (sle != null) sle.onStopError(e);
        }

        // Join server thread
        try {
            serverThread.join(1000);

        } catch (InterruptedException e) {
            if (serverThread.isAlive()) {
                log.warn(Mrk_Commons.COMM_SRV, String.format("Thread server loop '%s' not terminated", serverThread.getName()));
                if (sle != null) sle.onStopError(e);
            }
        }

        if (!serverThread.isAlive()) {
            updateLastShutdown();
            if (sle != null) sle.onStopped();
            log.debug(Mrk_Commons.COMM_SRV, String.format("Thread server loop '%s' stopped", serverThread.getName()));
        }

        // Disconnect clients
        for (ClientInfo c : getClients())
            if (c.isConnected())
                if (!c.closeConnection())
                    log.warn(Mrk_Commons.COMM_SRV, String.format("Client '%s' NOT disconnected", c.getClientId()));

        // Reset internal vars
        serverSocket = null;
        serverThread = null;
    }


    // Messages methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(String clientId, byte[] data) throws ServerStoppedException, ClientNotFoundException, ClientNotConnectedException {
        sendData(getClientById(clientId), data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(ClientInfo client, byte[] data) throws ServerStoppedException, ClientNotConnectedException {
        if (!isRunning())
            throw new ServerStoppedException();

        try {
            transmitData(client.getOutStream(), data, delimiter);

        } catch (PeerInfo.PeerNotConnectedException | PeerInfo.PeerStreamsException | IOException e) {
            throw new ClientNotConnectedException(client.getClientId(), e);
        }

        updateLastDataSend(client);
        log.trace(Mrk_Commons.COMM_SRV, String.format("Server '%s' send to client '%s' data '%s...'", getServerId(), client.getClientId(), truncateMid(data, 30)));
        sme.onDataSend(client, data);
        sme.onDataSend(client, new String(data, PeerInfo.CHARSET));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(String clientId, String data) throws ServerStoppedException, ClientNotFoundException, ClientNotConnectedException {
        sendData(clientId, data.getBytes(PeerInfo.CHARSET));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(ClientInfo client, String data) throws ServerStoppedException, ClientNotConnectedException {
        sendData(client, data.getBytes(PeerInfo.CHARSET));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCliByeMsg(byte[] data) {
        return Arrays.equals(AbsClient.MSG_BYE_CLI, data);
    }


    // Clients mngm

    /**
     * {@inheritDoc}
     * <p>
     * This method returns a copy of internal client list.
     */
    @Override
    public List<ClientInfo> getClients() {
        return new ArrayList<>(clients);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo findClientById(String clientId) {
        for (ClientInfo c : getClients())
            if (clientId.equals(c.getClientId()))
                return c;

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo getClientById(String clientId) throws ClientNotFoundException {
        ClientInfo c = findClientById(clientId);
        if (c == null)
            throw new ClientNotFoundException(clientId);

        return c;
    }


    // Standard behaviours

    /**
     * {@inheritDoc}
     */
    @Override
    public HeartBeatConfigs getHeartBeatConfigs() {
        return heartbeat;
    }

    class ServerHearBeatListener implements DefaultHeartBeat.HeartBeatListener {

        private final ClientInfo client;

        ServerHearBeatListener(ClientInfo client) {
            this.client = client;
        }

        @Override
        public void onSend() {
            log.debug(Mrk_Commons.COMM_SRV, String.format("Server '%s' send HB to client '%s'", getServerId(), client.getClientId()));
        }

        @Override
        public void onSuccess() {
            updateLastHeartbeat(client);
            log.debug(Mrk_Commons.COMM_SRV, String.format("Server '%s' received HB response from client '%s'", getServerId(), client.getClientId()));
        }

        @Override
        public void onError() {
            updateLastHeartbeatFailed(client);
            log.warn(Mrk_Commons.COMM_SRV, String.format("Server '%s' reached HB timeout from client '%s', close connection.", getServerId(), client.getClientId()));
            client.closeConnection();
        }

    }


    // Subclassing customization

    /**
     * Generate a {@link ServerSocket} and bound it on server's port.
     * <p>
     * Subclasses can override this method to set a different ServerSocket instance.
     *
     * @return the ServerSocket instance.
     */
    protected ServerSocket generateAndBoundServerSocket() throws IOException {
        log.debug(Mrk_Commons.COMM_SRV, String.format("Bounding server '%s' as TCP server on port '%d'", getServerId(), getPort()));
        ServerSocket s = new ServerSocket(getPort());
        log.debug(Mrk_Commons.COMM_SRV, String.format("Server '%s' bounded", getServerId()));
        return s;
    }

    /**
     * Generate a {@link ClientInfo} based on give socket.
     *
     * @return the ClientInfo instance representing given client's socket.
     */
    protected ClientInfo generateAndStartClientInfo(Socket socket) {
        String clientId = String.format(ID_CLI_FORMAT, socket.getInetAddress(), socket.getPort());
        log.info(Mrk_Commons.COMM_SRV, String.format("Connect client '%s' to '%s' server", clientId, getServerId()));

        DefaultClientInfo clientInfo = new DefaultClientInfo(socket, clientId, getServerId());
        Thread clientThread = new Thread(() -> processClient(clientInfo));
        clientInfo.startThread(clientThread);

        return clientInfo;
    }


    // Last connection updaters

    /**
     * Called when server is started successfully.
     */
    protected void updateLastStartup() {
        lastStartup = JavaDate.getNowDate();
    }

    /**
     * Called when server is shutdown successfully.
     */
    protected void updateLastShutdown() {
        lastShutdown = JavaDate.getNowDate();
    }

    /**
     * Called when a client connect successfully to the server.
     */
    protected void updateLastClientConnection(ClientInfo clientInfo) {
        lastConnection = JavaDate.getNowDate();
        lastConnectionClient = clientInfo;
    }

    /**
     * Called when a client disconnect successfully to the server.
     */
    protected void updateLastClientDisconnection(ClientInfo clientInfo) {
        lastDisconnection = JavaDate.getNowDate();
        lastDisconnectionClient = clientInfo;
    }

    /**
     * Called when a client respond to a heartbeat request from current server.
     */
    protected void updateLastHeartbeat(ClientInfo clientInfo) {
        lastHeartbeat = JavaDate.getNowDate();
        lastHeartbeatClient = clientInfo;
    }

    /**
     * Called when a client DON'T respond to a heartbeat request from current server.
     */
    protected void updateLastHeartbeatFailed(ClientInfo clientInfo) {
        lastHeartbeatFailed = JavaDate.getNowDate();
        lastHeartbeatFailedClient = clientInfo;
    }

    /**
     * Called when current server receive data from a client.
     */
    protected void updateLastDataReceived(ClientInfo clientInfo) {
        lastDataReceived = JavaDate.getNowDate();
        lastDataReceivedClient = clientInfo;
    }

    /**
     * Called when current server send data to a client.
     */
    protected void updateLastDataSend(ClientInfo clientInfo) {
        lastDataSend = JavaDate.getNowDate();
        lastDataSendClient = clientInfo;
    }


    // Internal thread methods

    /**
     * The server infinite loop.
     * <p>
     * This method loop until the server must shutdown and wait for new clients
     * connections. For each connection, it generate ClientInfo and start his
     * process thread. Then add or update the client in the {@link #clients}
     * array.
     */
    private void infiniteLoop() {
        log.debug(Mrk_Commons.COMM_SRV, String.format("Thread server loop '%s' started for '%s' server", Thread.currentThread().getName(), getServerId()));
        mustShutdown = false;
        while (!mustShutdown) {
            try {
                ClientInfo newClient = generateAndStartClientInfo(serverSocket.accept());
                if (newClient == null)
                    continue;

                clients.add(newClient);
                updateLastClientConnection(newClient);
                if (sce != null) sce.onClientConnection(newClient);

            } catch (IOException e) {
                if (!mustShutdown && !serverSocket.isClosed()) {
                    log.warn(Mrk_Commons.COMM_SRV, String.format("Thread server loop '%s' interrupted because %s", getServerId(), e.getMessage()));
                    if (sle != null) sle.onServerError(e);
                }

                log.debug(Mrk_Commons.COMM_SRV, String.format("Terminating thread server loop '%s'", Thread.currentThread().getName()));
                break;
            }
        }

        log.trace(Mrk_Commons.COMM_SRV, String.format("Thread server loop '%s' terminated", Thread.currentThread().getName()));
    }

    /**
     * The client's process thread.
     * <p>
     * This tread receive all data rx from the client and emit the
     * {@link ServerMessagingEvents#onDataReceived(ClientInfo, byte[])} event.
     *
     * @param client the reference of client that send data to current server.
     */
    protected void processClient(DefaultClientInfo client) {
        // Get client's input stream
        DataInputStream in;
        try {
            in = client.getInStream();
        } catch (PeerInfo.PeerNotConnectedException | PeerInfo.PeerStreamsException e) {
            log.error(Mrk_Commons.COMM_SRV, String.format("Client processor can't get input stream from client '%s' because %s", client, e.getMessage()));
            if (sce != null) sce.onClientError(client, e);
            return;
        }

        log.debug(Mrk_Commons.COMM_SRV, String.format("Thread client processor '%s'  started for client '%s'", Thread.currentThread().getName(), client.getClientId()));
        boolean socketReadError = false;
        boolean socketResetError = false;
        boolean socketSSLError = false;
        boolean clientHBTimeoutError = false;
        boolean clientTerminatedError = false;
        boolean heartbeatSendError = false;
        boolean serverShutdown = false;
        boolean clientSendByeMsg = false;

        byte[] dataBuffered = new byte[0];
        DefaultHeartBeat clientHeartbeat = new DefaultHeartBeat(getServerId(), HeartBeatConfigs.SideType.Server, client.getClientId(), client,
                new ServerHearBeatListener(client),
                heartbeat.getTimeout(), heartbeat.getResponseTimeout(), heartbeat.getEchoEnabled());
        heartbeat.addWrapped(clientHeartbeat);
        while (!mustShutdown) {
            byte[] dataRead;

            // Listen for client data
            try {
                byte[][] dataReadTmp = listenForData(in, dataBuffered, delimiter);
                if (dataReadTmp == null) {
                    socketReadError = true;
                    break;
                }
                dataRead = dataReadTmp[0];          // data to process (before delimiter)
                dataBuffered = dataReadTmp[1];      // data to buffer (after delimiter)

            } catch (IOException e) {
                if (mustShutdown) {
                    serverShutdown = true;                                          //log.info(Mrk_Commons.COMM_SRV, String.format("Client '%s' disconnected because server '%s' is shutingdown", client.getClientId(), getServerId()));
                    break;
                }

                if (!client.isConnected()) {
                    if (clientHeartbeat.isTimedout())
                        clientHBTimeoutError = true;
                    else
                        clientTerminatedError = true;                                   //log.info(Mrk_Commons.COMM_SRV, String.format("Client '%s' disconnected, client disconnected to server '%s'", client.getClientId(), getServerId()));
                    break;
                }

                if (e instanceof SSLException && e.getMessage().startsWith("Received fatal alert: internal_error")) {
                    socketSSLError = true;                                          //log.warn(Mrk_Commons.COMM_SRV, String.format("Client '%s' can't connect, wait for sharing certificate with server '%s'", client.getClientId(), getServerId()));
                    break;
                }

                if (e instanceof SocketTimeoutException && e.getMessage().equalsIgnoreCase("Read timed out"))
                    if (!clientHeartbeat.sendHeartBeat(delimiter)) {
                        heartbeatSendError = true;                                  //log.info(Mrk_Commons.COMM_SRV, String.format("Client '%s' disconnected to server '%s', can't send heartbeat", client.getClientId(), getServerId()));
                        break;
                    } else continue;

                if (e instanceof SocketException && e.getMessage().equals("Connection reset")) {
                    socketResetError = true;                                        //log.warn(Mrk_Commons.COMM_SRV, String.format("Client '%s' disconnected, client reset connection to server  '%s'", client.getClientId(), getServerId()));
                    break;
                }

                // Rx error, but not client closed
                log.warn(Mrk_Commons.COMM_SRV, String.format("Server can't read data from client '%s' because %s", client.getClientId(), e.getMessage()));
                if (sce != null) sce.onClientError(client, e);
                continue;
            }

            // Check and process HB message
            try {
                if (clientHeartbeat.tryProcessHeartBeat(dataRead, delimiter))
                    continue;
            } catch (IOException e) {
                log.warn(Mrk_Commons.COMM_SRV, String.format("Client processor can't process HB message from '%s' because %s", client.getClientId(), e.getMessage()));
                log.warn(Mrk_Commons.COMM_SRV, String.format("                                   (dataRx: '%s')", new String(dataRead, PeerInfo.CHARSET)));
                if (sce != null) sce.onClientError(client, e);
            }

            // Check and process client's BYE message
            if (isCliByeMsg(dataRead)) {
                clientSendByeMsg = true;
                break;
            }

            // Process received data from client
            updateLastDataReceived(client);
            try {
                if (!sme.onDataReceived(client, dataRead))
                    if (!sme.onDataReceived(client, new String(dataRead, PeerInfo.CHARSET))) {
                        log.warn(Mrk_Commons.COMM_SRV, String.format("Client processor can't process data from '%s' data because unknown data", client.getClientId()));
                        log.warn(Mrk_Commons.COMM_SRV, String.format("                                   (dataRx: %s)", new String(dataRead, PeerInfo.CHARSET)));
                    }

            } catch (Throwable e) {
                log.warn(Mrk_Commons.COMM_SRV, String.format("Client processor can't process data from '%s' because %s", client.getClientId(), e.getMessage()));
                log.warn(Mrk_Commons.COMM_SRV, String.format("                                   (dataRx: '%s')", new String(dataRead, PeerInfo.CHARSET)));
                if (sce != null) sce.onClientError(client, e);
            }

        }
        heartbeat.removeWrapped(clientHeartbeat);

        String msg;
        if (socketReadError) msg = "Error reading data from socket (listenForData() method returned null)";
        else if (socketResetError) msg = "Error because client reset connection (catched SocketException)";
        else if (socketSSLError) msg = "Error on SSL";
        else if (clientHBTimeoutError) msg = "Error client didn't respond to HB";
        else if (clientTerminatedError) msg = "Error client disconnected with out BYEMSG";
        else if (heartbeatSendError) msg = "Error on send HB message";
        else if (serverShutdown) msg = "Required server shutdown";
        else if (clientSendByeMsg) msg = "Required client disconnection";
        else msg = "Unknown";
        log.info(Mrk_Commons.COMM_SRV, String.format("### ### ###"));
        log.info(Mrk_Commons.COMM_SRV, String.format("### Server's client processor exit because: %s", msg));
        log.info(Mrk_Commons.COMM_SRV, String.format("### ### ###"));

        if (!mustShutdown)
            log.debug(Mrk_Commons.COMM_SRV, String.format("Self terminating thread client processor '%s'", Thread.currentThread().getName()));

        // Client disconnection events
        log.info(Mrk_Commons.COMM_SRV, String.format("Disconnect client '%s' to '%s' server", client.getClientId(), getServerId()));
        clients.remove(client);
        updateLastClientDisconnection(client);
        if (sce != null) {
            if (clientSendByeMsg) {
                sce.onClientGoodbye(client);
                if (client.isConnected()) client.closeConnection();

            } else if (mustShutdown) {
                sce.onClientServerDisconnected(client);
            } else {
                sce.onClientTerminated(client);
                if (client.isConnected())
                    try {
                        client.closeConnection();
                    } catch (Exception e) {
                        log.warn(Mrk_Commons.COMM_SRV, String.format("Error disconnecting client '%s' to '%s' server because %s", client.getClientId(), getServerId(), e.getMessage()), e);
                    }
            }
            sce.onClientDisconnection(client);
        }

        log.trace(Mrk_Commons.COMM_SRV, String.format("Thread client processor '%s' terminated", Thread.currentThread().getName()));

        if (!mustShutdown)
            log.debug(Mrk_Commons.COMM_SRV, String.format("Thread client processor '%s' stopped", Thread.currentThread().getName()));
    }

}

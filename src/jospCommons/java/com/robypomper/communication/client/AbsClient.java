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

package com.robypomper.communication.client;

import com.robypomper.communication.CommunicationBase;
import com.robypomper.communication.client.events.ClientLocalEvents;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.ClientServerEvents;
import com.robypomper.communication.peer.PeerInfo;
import com.robypomper.communication.server.DefaultServer;
import com.robypomper.java.JavaEnum;
import com.robypomper.java.JavaThreads;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.states.StateException;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLHandshakeException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;


/**
 * Default implementation of Client interface that use {@link Socket} as
 * communication channel and can be extended with {@link Socket} subclasses.
 * <p>
 * ...
 */
@SuppressWarnings({"UnnecessaryReturnStatement", "unused"})
public abstract class AbsClient implements Client {

    // Class constants

    public static final String TH_CLI_NAME_FORMAT = "CLI-%s@%s";
    public static final String ID_SRV_FORMAT = "SRV-%s:%d";
    public static final String MSG_BYE_CLI_STR = "byecli";
    public static final byte[] MSG_BYE_CLI = MSG_BYE_CLI_STR.getBytes(PeerInfo.CHARSET);
    public static final String TIMER_CONNECTION_NAME_FRMT = "_CONN_S2OGW_";
    public static final int TIMER_DELAY_MS = 30 * 1000;


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JavaEnum.SynchronizableState<Client.State> state = new JavaEnum.SynchronizableState<>(Client.State.DISCONNECTED, log);
    // Configs
    private final String clientId;
    private final String serverAddr;
    private final int serverPort;
    // Client's events
    private final ClientLocalEvents cle;
    private final ClientServerEvents cse;
    private final ClientMessagingEvents cme;
    // Last connections
    private Date lastConnection;
    private Date lastDisconnection;
    // Listeners
    private final List<ClientListener> statusListeners = new ArrayList<>();
    // Connection timer
    private Timer connectionTimer = null;

    private ServerInfo serverInfo;
    private Socket clientSocket;
    private DataInputStream server2ClientStream;
    private DataOutputStream client2ServerStream;
    private Thread clientThread;
    private boolean mustShutdown = false;
    private final boolean delimiter = true;


    // Constructor

    /**
     * Full constructor that initialize a client for <code>serverAddr:serverPort</code>
     * server and with given id.
     *
     * @param clientId                      the clinet id.
     * @param serverAddr                    the server's address.
     * @param serverPort                    the server's port.
     * @param clientMessagingEventsListener the tx and rx messaging listener.
     */
    protected AbsClient(String clientId, String serverAddr, int serverPort, ClientMessagingEvents clientMessagingEventsListener) {
        this(clientId, serverAddr, serverPort, null, null, clientMessagingEventsListener);
    }

    /**
     * Full constructor that initialize a client for <code>serverAddr:serverPort</code>
     * server and with given id.
     *
     * @param clientId                      the clinet id.
     * @param serverAddr                    the server's address.
     * @param serverPort                    the server's port.
     * @param clientLocalEventsListener     the local client events listener.
     * @param clientServerEventsListener    the server events listener.
     * @param clientMessagingEventsListener the tx and rx messaging listener.
     */
    protected AbsClient(String clientId, String serverAddr, int serverPort,
                        ClientLocalEvents clientLocalEventsListener,
                        ClientServerEvents clientServerEventsListener,
                        ClientMessagingEvents clientMessagingEventsListener) {
        this.clientId = clientId;
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
        this.cle = clientLocalEventsListener;
        if (this.cle != null) this.cle.setClient(this);
        this.cse = clientServerEventsListener;
        if (this.cse != null) this.cse.setClient(this);
        if (clientMessagingEventsListener == null)
            throw new IllegalArgumentException("DefaultClient can't be initialized with a null ClientMessagingEvents param.");
        this.cme = clientMessagingEventsListener;
        this.cme.setClient(this);

        log.info(Mrk_Commons.COMM_CL, String.format("Initialized DefaultClient/%s instance for '%s' client of '%s' server on '%d' port", this.getClass().getSimpleName(), clientId, serverAddr, serverPort));
        log.debug(Mrk_Commons.COMM_CL, String.format("                                          with %s, %s and %s listeners",
                cle != null ? cle.getClass() : "no CLE",
                cse != null ? cse.getClass() : "no CSE",
                cme.getClass()
        ));
    }


    // Getter state

    /**
     * {@inheritDoc}
     */
    @Override
    public State getState() {
        return state.get();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is based on internal {@link #getState()}.
     */
    @Override
    public boolean isConnected() {
        return state.enumEquals(State.CONNECTED);
    }

    /**
     * This method is based on internal {@link Socket} ({@link Socket#isConnected()}
     * and !{@link Socket#isClosed()}) checks.
     */
    public boolean isSocketConnected() {
        return (clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed());
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is based on internal {@link #getState()}.
     */
    @Override
    public boolean isConnecting() {
        //assert (state.enumEquals(State.CONNECTING)) == ();
        return state.get().isCONNECTING();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is based on internal {@link #getState()}.
     */
    @Override
    public boolean isDisconnecting() {
        //assert (state.enumEquals(State.DISCONNECTING)) == ();
        return state.enumEquals(State.DISCONNECTING);
    }

    /**
     * This method is based on internal {@link Socket} ({@link Socket#isConnected()}
     * and !{@link Socket#isClosed()}) checks.
     */
    public boolean isSocketDisconnecting() {
        return (clientSocket != null && clientSocket.isConnected() && clientSocket.isClosed());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastConnection() {
        return lastConnection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastDisconnection() {
        return lastDisconnection;
    }


    // Getter configs

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientId() {
        return clientId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String tryClientHostname() {
        try {
            return getClientHostname();
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientHostname() throws ServerNotConnectedException {
        return getClientAddr() + ":" + getClientPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String tryClientAddr() {
        try {
            return getClientAddr();
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientAddr() throws ServerNotConnectedException {
        if (!isConnected())
            throw new ServerNotConnectedException(this, "ClientAddr");
        return clientSocket.getLocalAddress().getHostAddress();
    }

    /**
     * {@inheritDoc}
     * <p>
     * On error, return -1.
     */
    @Override
    public int tryClientPort() {
        try {
            return getClientPort();
        } catch (Throwable ignore) {
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getClientPort() throws ServerNotConnectedException {
        if (!isConnected())
            throw new ServerNotConnectedException(this, "ClientPort");
        return clientSocket.getLocalPort();
    }

    /**
     * {@inheritDoc}
     * <p>
     * On error, return -1.
     */
    @Override
    public String tryServerId() {
        try {
            return getServerId();
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerId() throws ServerNotConnectedException {
        if (!isConnected() || serverInfo == null)
            throw new ServerNotConnectedException(this, "serverInfo");
        return serverInfo.getServerId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerUrl() {
        return getProtocolName() + "://" + getServerHostname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerHostname() {
        return getServerAddr() + ":" + getServerPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerAddr() {
        return serverAddr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServerPort() {
        return serverPort;
    }

    /**
     * {@inheritDoc}
     * <p>
     * On error, return -1.
     */
    @Override
    public String tryServerRealAddr() {
        try {
            return getServerRealAddr();
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerRealAddr() throws ServerNotConnectedException {
        if (!isConnected() || serverInfo == null)
            throw new ServerNotConnectedException(this, "serverInfo");
        return serverInfo.getPeerAddress().getHostAddress();
    }

    /**
     * {@inheritDoc}
     * <p>
     * On error, return -1.
     */
    @Override
    public int tryServerRealPort() {
        try {
            return getServerRealPort();
        } catch (Throwable ignore) {
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServerRealPort() throws ServerNotConnectedException {
        if (!isConnected() || serverInfo == null)
            throw new ServerNotConnectedException(this, "serverInfo");
        return serverInfo.getPeerPort();
    }


    // Client connection methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws IOException, AAAException, StateException {
        if (state.enumEquals(State.CONNECTED))
            return; // Already done

        else if (state.get().isCONNECTING())
            return; // Already in progress

        else if (state.enumEquals(State.DISCONNECTED))
            initConnection();

        else if (state.enumEquals(State.DISCONNECTING)) {
            if (stopDisconnecting())
                initConnection();
            else
                throw new StateException("Can't connect GW Client because is disconnecting, try again later");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() throws StateException {
        if (state.enumEquals(State.CONNECTED))
            closeConnection(false);

        else if (state.get().isCONNECTING()) {
            if (stopConnecting()) {
                if (state.enumEquals(State.CONNECTED))
                    closeConnection(false);
            } else
                throw new StateException("Can't disconnect GW Client because is connecting, try again later");

        } else if (state.enumEquals(State.DISCONNECTED))
            return; // Already done

        else if (state.enumEquals(State.DISCONNECTING))
            return; // Already in progress
    }

    private void initConnection() throws IOException, AAAException {
        assert state.enumEquals(Client.State.DISCONNECTED)
                || state.get().isCONNECTING() :
                "Method initConnection() can be called only from DISCONNECTED or CONNECTING_ state";

        synchronized (state) {
            if (!state.get().isCONNECTING())
                state.set(Client.State.CONNECTING);

            // Connect server
            try {
                clientSocket = generateAndBoundClientSocket();
                client2ServerStream = new DataOutputStream(clientSocket.getOutputStream());
                server2ClientStream = new DataInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                if (state.enumNotEquals(State.CONNECTING_WAITING_SERVER)) {
                    log.warn("GW Client can't connect, add JCP APIs's connection listener; state = CONNECTING_WAITING_JCP_APIS");
                    state.set(State.CONNECTING_WAITING_SERVER);
                    startConnectionTimer();
                }
                throw e;
            }

            serverInfo = generateAndStartServerInfo(clientSocket);
            clientThread = serverInfo.getThread();

            // Clean up connection waiting stuff
            if (state.enumEquals(State.CONNECTING_WAITING_SERVER)) {
                log.warn("Client connect, stop server reachability timer");
                stopConnectionTimer();
            }

            state.set(State.CONNECTED);
            updateLastConnection();
            if (cle != null) cle.onConnected();
            if (cse != null) cse.onServerConnection();
            emit_ClientConnected();
        }
    }

    private boolean stopConnecting() {
        assert state.get().isCONNECTING() :
                "Method stopConnecting() can be called only from CONNECTING_ state";

        // If connecting (1st attempt)
        if (state.enumEquals(State.CONNECTING)) {
            JavaThreads.softSleep(1000);
            return !state.get().isCONNECTING();
        }

        synchronized (state) {
            // Clean up connection waiting stuff
            if (state.enumEquals(State.CONNECTING_WAITING_SERVER)) {
                log.warn("Client disconnect, stop server reachability timer");
                stopConnectionTimer();
            }

            state.set(State.DISCONNECTED);
            return true;
        }
    }

    private void closeConnection(boolean internalCall) {
        assert state.enumEquals(State.CONNECTED) :
                "Method closeConnection() can be called only from CONNECTED state";

        synchronized (state) {
            state.set(State.DISCONNECTING);

            try {
                CommunicationBase.transmitData(clientSocket.getOutputStream(), MSG_BYE_CLI);

            } catch (IOException e) {
                log.warn(Mrk_Commons.COMM_CL, String.format("Client '%s' can't send BYE message, continue disconnecting", getClientId()));
                if (cle != null) cle.onDisconnectionError(e);
            }

            // Terminate server thread
            mustShutdown = true;
            clientThread.interrupt();

            // Close server socket
            try {
                clientSocket.close();

            } catch (IOException e) {
                log.warn(Mrk_Commons.COMM_CL, String.format("Socket for client '%s' not closed, continue disconnecting", getClientId()));
                if (cle != null) cle.onDisconnectionError(e);
            }

            // Join server thread
            try {
                if (Thread.currentThread() != clientThread)
                    clientThread.join(1000);


            } catch (InterruptedException e) {
                if (clientThread.isAlive() && !internalCall) {
                    log.warn(Mrk_Commons.COMM_CL, String.format("Thread for client '%s' not terminated, continue disconnecting", getClientId()));
                    if (cle != null) cle.onDisconnectionError(e);
                }
            }

            if (!clientThread.isAlive() && !internalCall) {
                log.info(Mrk_Commons.COMM_CL, String.format("Client '%s' disconnected successfully", getClientId()));
                if (cle != null) cle.onDisconnected();
                emit_ClientDisconnected();
            }

            // Reset internal vars
            clientSocket = null;
            clientThread = null;
            client2ServerStream = null;
            server2ClientStream = null;

            state.set(State.DISCONNECTED);
            updateLastDisconnection();
            emit_ClientDisconnected();
        }
    }

    private boolean stopDisconnecting() {
        assert state.enumEquals(State.DISCONNECTING) :
                "Method stopDisconnecting() can be called only from DISCONNECTING state";

        // If disconnecting (1st attempt)
        JavaThreads.softSleep(1000 + 100);
        return state.enumNotEquals(State.DISCONNECTING);
    }


    // Client connection methods - GWs re-connection timer

    private void startConnectionTimer() {
        assert state.enumEquals(State.CONNECTING_WAITING_SERVER) :
                "Method startConnectionTimer() can be called only from CONNECTING_WAITING_SERVER state";

        long waitMs = TIMER_DELAY_MS * 1000;
        connectionTimer = new Timer(true);
        connectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                assert state.enumEquals(State.CONNECTING_WAITING_SERVER) :
                        "Method startConnectionTimer() can be called only from CONNECTING_WAITING_SERVER state";

                Thread.currentThread().setName(TIMER_CONNECTION_NAME_FRMT);
                try {
                    initConnection();

                } catch (IOException e) {
                    emit_ClientConnectionIOException(e);

                } catch (AAAException e) {
                    emit_ClientConnectionAAAException(e);
                }
            }
        }, waitMs, waitMs);
    }

    private void stopConnectionTimer() {
        assert state.enumEquals(State.CONNECTING_WAITING_SERVER) :
                "Method stopConnectionTimer() can be called only from CONNECTING_WAITING_SERVER state";

        if (connectionTimer == null) return;

        connectionTimer.cancel();
        connectionTimer = null;
    }


    // Connection listeners

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(ClientListener listener) {
        if (statusListeners.contains(listener))
            return;

        statusListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(ClientListener listener) {
        if (!statusListeners.contains(listener))
            return;

        statusListeners.remove(listener);
    }

    /**
     * Called when a connection was stabilised successfully with a server.
     */
    private void emit_ClientConnected() {
        updateLastConnection();
        for (ClientListener l : statusListeners)
            l.onConnected(this);
    }

    /**
     * Called when an error occurs on opening communication channel.
     * <p>
     * The exception emitted by method {@link #generateAndBoundClientSocket()}
     * generate a {@link com.robypomper.communication.client.Client.ClientListener#onConnectionIOException(Client, IOException)}
     * event.
     */
    private void emit_ClientConnectionIOException(IOException ioException) {
        for (ClientListener l : statusListeners)
            l.onConnectionIOException(this, ioException);
    }

    /**
     * Called when an error occurs on AAA processes of connection.
     * <p>
     * The exception emitted by method {@link #generateAndStartServerInfo(Socket)}
     * generate a {@link com.robypomper.communication.client.Client.ClientListener#onConnectionAAAException(Client, AAAException)}
     * event.
     */
    private void emit_ClientConnectionAAAException(AAAException aaaException) {
        for (ClientListener l : statusListeners)
            l.onConnectionAAAException(this, aaaException);
    }

    /**
     * Called when a connection was closed (because required or an erro occured).
     */
    private void emit_ClientDisconnected() {
        updateLastDisconnection();
        for (ClientListener l : statusListeners)
            l.onDisconnected(this);
    }


    // Messages methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(byte[] data) throws ServerNotConnectedException {
        if (!isConnected())
            throw new ServerNotConnectedException(getServerId());

        try {
            CommunicationBase.transmitData(client2ServerStream, data, delimiter);

        } catch (IOException e) {
            throw new ServerNotConnectedException(getServerId(), e);
        }

        log.debug(Mrk_Commons.COMM_CL, String.format("Client '%s' send to server '%s' data '%s...'", getClientId(), getServerId(), CommunicationBase.truncateMid(data, 30)));
        cme.onDataSend(data);
        cme.onDataSend(new String(data, PeerInfo.CHARSET));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(String data) throws ServerNotConnectedException {
        sendData(data.getBytes(PeerInfo.CHARSET));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSrvByeMsg(byte[] data) {
        return Arrays.equals(DefaultServer.MSG_BYE_SRV, data);
    }


    // Subclassing customization

    /**
     * Called when a connection was stabilised successfully with a server.
     */
    private void updateLastConnection() {
        lastConnection = JOSPProtocol.getNowDate();
    }

    /**
     * Called when a connection was closed (because required or because an error).
     */
    private void updateLastDisconnection() {
        lastDisconnection = JOSPProtocol.getNowDate();
    }

    /**
     * Generate a {@link Socket} and connect to server's address:port.
     * <p>
     * Subclasses can override this method to set a different Socket instance.
     *
     * @return the Socket instance.
     */
    protected Socket generateAndBoundClientSocket() throws IOException {
        log.debug(Mrk_Commons.COMM_CL, String.format("client '%s' initialized as TCP client for '%s:%d' server", getClientId(), getServerAddr(), getServerPort()));
        return new Socket(getServerAddr(), getServerPort());
    }

    /**
     * Generate a {@link ServerInfo} based on give socket.
     *
     * @return the ServerInfo instance representing given client's socket.
     */
    @SuppressWarnings("RedundantThrows")
    protected ServerInfo generateAndStartServerInfo(Socket socket) throws AAAException {
        String serverId = tryServerId();

        Thread cThread = new Thread(this::processServer);
        cThread.setName(String.format(TH_CLI_NAME_FORMAT, getClientId(), serverId));
        ServerInfo sInfo = new DefaultServerInfo(socket, serverId, cThread);
        cThread.start();
        return sInfo;
    }


    // Internal thread methods

    /**
     * The server's process thread.
     * <p>
     * This tread receive all data rx from the server and emit the
     * {@link ClientMessagingEvents#onDataReceived(byte[])} event.
     */
    protected void processServer() {
        log.debug(Mrk_Commons.COMM_CL, String.format("Server '%s' processor thread '%s' started", getServerUrl(), Thread.currentThread().getName()));
        mustShutdown = false;
        boolean serverSendByeMsg = false;
        DataInputStream in = server2ClientStream;
        byte[] dataBuffered = new byte[0];
        while (!mustShutdown) {

            try {
                // Listen for server data
                byte[][] dataReadTmp = CommunicationBase.listenForData(in, dataBuffered, delimiter);

                if (dataReadTmp == null)
                    break;

                byte[] dataRead = dataReadTmp[0];
                dataBuffered = dataReadTmp[1];

                // Process received data from server
                try {
                    if (isSrvByeMsg(dataRead)) {
                        serverSendByeMsg = true;
                        break;
                    }

                    if (!cme.onDataReceived(dataRead))
                        if (!cme.onDataReceived(new String(dataRead, PeerInfo.CHARSET))) {
                            log.warn(Mrk_Commons.COMM_CL, String.format("Server processor can't process data from '%s' data because unknown data", getServerId()));
                            log.debug(Mrk_Commons.COMM_CL, String.format("(dataRx: %s)", new String(dataRead, PeerInfo.CHARSET)));
                            //if (cme != null) cme.onProcessError(data);
                            //if (cme != null) cme.onProcessError(new String(dataRead, PeerInfo.CHARSET));
                        }

                } catch (Throwable e) {
                    log.warn(Mrk_Commons.COMM_CL, String.format("Server processor can't process data from '%s' because %s", getServerUrl(), e.getMessage()));
                    String dataStr = new String(dataRead, PeerInfo.CHARSET);
                    log.warn(Mrk_Commons.COMM_CL, String.format("(dataRx: '%s')", dataStr.substring(0, Math.min(dataStr.length(), 10))));
                    if (cse != null) cse.onServerError(e);
                }

            } catch (IOException e) {
                if (!isConnected()
                        || mustShutdown
                        || (e instanceof SocketException && e.getMessage().equals("Connection reset"))
                        || (e instanceof SSLHandshakeException && e.getMessage().equals("Remote host closed connection during handshake"))
                )
                    break;

                // Rx error, but not client closed
                log.warn(Mrk_Commons.COMM_CL, String.format("Client can't read data from server '%s' because %s", getServerUrl(), e.getMessage()));
                if (cse != null) cse.onServerError(e);
            }
        }

        boolean wasMustShutdown = mustShutdown;
        if (isConnected() && (serverSendByeMsg || !mustShutdown))
            closeConnection(true);

        // Server disconnection events
        log.info(Mrk_Commons.COMM_CL, String.format("Disconnect server '%s' to '%s' client", serverInfo.getServerId(), getClientId()));
        if (cse != null) {
            if (serverSendByeMsg)
                cse.onServerGoodbye();
            else if (wasMustShutdown)
                cse.onServerClientDisconnected();
            else
                cse.onServerTerminated();

            cse.onServerDisconnection();
        }

        log.debug(Mrk_Commons.COMM_CL, String.format("Server '%s' processor thread terminated", getServerUrl()));
    }

}

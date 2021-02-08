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

package com.robypomper.communication_deprecated.client;

import com.robypomper.communication_deprecated.CommunicationBase;
import com.robypomper.communication_deprecated.client.events.ClientLocalEvents;
import com.robypomper.communication_deprecated.client.events.ClientMessagingEvents;
import com.robypomper.communication_deprecated.client.events.ClientServerEvents;
import com.robypomper.communication_deprecated.peer.DefaultHeartBeat;
import com.robypomper.communication_deprecated.peer.HeartBeatConfigs;
import com.robypomper.communication_deprecated.peer.PeerInfo;
import com.robypomper.communication_deprecated.server.DefaultServer;
import com.robypomper.java.JavaDate;
import com.robypomper.java.JavaEnum;
import com.robypomper.java.JavaThreads;
import com.robypomper.josp.states.StateException;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


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
    public static final String TIMER_CONNECTION_NAME_FRMT = "_CONN_CLI_";
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
    private Date lastHeartbeat;
    private Date lastHeartbeatFailed;
    private Date lastDataReceived;
    private Date lastDataSend;
    private String lastClientAddr;
    private Integer lastClientPort;
    private NetworkInterface lastClientIntf;
    // Connection timer
    private Timer connectionTimer = null;
    // Heartbeat
    private final DefaultHeartBeat heartBeat;

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
        this.heartBeat = new DefaultHeartBeat(clientId, HeartBeatConfigs.SideType.Client, new ClientHearBeatListener());

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
     * {@inheritDoc}
     * <p>
     * This method is based on internal {@link #getState()}.
     */
    @Override
    public boolean isDisconnected() {
        return state.enumEquals(State.DISCONNECTED);
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
    public Date getLastHeartbeatFailed() {
        return lastHeartbeatFailed;
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
    public Date getLastDataSend() {
        return lastDataSend;
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
            String addr = tryClientAddr();
            Integer port = tryClientPort();
            return (addr != null && port != null) ? addr + ":" + port : null;
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
     * <p>
     * After first connection, this class store internally the client's
     * address and return it also if it's not connected to the server.
     */
    @Override
    public String tryClientAddr() {
        try {
            return getClientAddr();
        } catch (Throwable ignore) {
            return lastClientAddr;
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
     * After first connection, this class store internally the client's
     * port and return it also if it's not connected to the server.
     */
    @Override
    public Integer tryClientPort() {
        try {
            return getClientPort();
        } catch (Throwable ignore) {
            return lastClientPort;
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
     */
    @Override
    public NetworkInterface getClientIntf() throws ServerNotConnectedException {
        if (!isConnected())
            throw new ServerNotConnectedException(this, "ClientIntf");
        try {
            return NetworkInterface.getByInetAddress(clientSocket.getLocalAddress());
        } catch (SocketException e) {
            throw new ServerNotConnectedException(this, "ClientIntf");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * After first connection, this class store internally the client's
     * port and return it also if it's not connected to the server.
     */
    @Override
    public NetworkInterface tryClientIntf() {
        try {
            return getClientIntf();
        } catch (Throwable ignore) {
            return lastClientIntf;
        }
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
    public Integer tryServerRealPort() {
        try {
            return getServerRealPort();
        } catch (Throwable ignore) {
            return null;
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
                throw new StateException("Can't connect Client because is disconnecting, try again later");
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
                throw new StateException("Can't disconnect Client because is connecting, try again later");

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
                lastClientAddr = clientSocket.getLocalAddress().getHostAddress();
                lastClientPort = clientSocket.getLocalPort();
                lastClientIntf = NetworkInterface.getByInetAddress(clientSocket.getLocalAddress());
                client2ServerStream = new DataOutputStream(clientSocket.getOutputStream());
                server2ClientStream = new DataInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                if (state.enumNotEquals(State.CONNECTING_WAITING_SERVER)) {
                    log.warn("Client can't connect, start Connection timer; state = CONNECTING_WAITING_SERVER");
                    state.set(State.CONNECTING_WAITING_SERVER);
                    startConnectionTimer();
                }
                if (cle != null) cle.onConnectionError(e);
                throw e;
            }

            try {
                serverInfo = generateAndStartServerInfo(clientSocket);
                heartBeat.setPeer(tryServerId(), serverInfo);
            } catch (AAAException e) {
                if (cle != null) cle.onConnectionError(e);
                throw e;
            }
            clientThread = serverInfo.getThread();

            // Clean up connection waiting stuff
            if (state.enumEquals(State.CONNECTING_WAITING_SERVER)) {
                log.warn("Client connect, stop server reachability timer");
                stopConnectionTimer();
            }

            state.set(State.CONNECTED);
            if (cle != null) cle.onConnected();
            if (cse != null) cse.onServerConnection();
            updateLastConnection();
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

            } catch (IOException | NullPointerException e) {
                log.warn(Mrk_Commons.COMM_CL, String.format("Client '%s' can't send BYE message, continue disconnecting", getClientId()));
                if (cle != null) cle.onDisconnectionError(e);
            }

            // Terminate server thread
            mustShutdown = true;
            if (clientThread != null)
                clientThread.interrupt();

            // Close server socket
            try {
                clientSocket.close();

            } catch (IOException | NullPointerException e) {
                log.warn(Mrk_Commons.COMM_CL, String.format("Socket for client '%s' not closed, continue disconnecting", getClientId()));
                if (cle != null) cle.onDisconnectionError(e);
            }

            // Join server thread
            try {
                if (Thread.currentThread() != clientThread)
                    clientThread.join(1000);

            } catch (InterruptedException | NullPointerException e) {
                if (clientThread != null && clientThread.isAlive() && !internalCall) {
                    log.warn(Mrk_Commons.COMM_CL, String.format("Thread for client '%s' not terminated, continue disconnecting", getClientId()));
                    if (cle != null) cle.onDisconnectionError(e);
                }
            }
            updateLastDisconnection();

            if (!internalCall) {
                log.info(Mrk_Commons.COMM_CL, String.format("Client '%s' disconnected successfully", getClientId()));
                if (cle != null) cle.onDisconnected();
            }

            // Reset internal vars (reset after event emit)
            clientSocket = null;
            clientThread = null;
            client2ServerStream = null;
            server2ClientStream = null;
            heartBeat.resetPeer();

            state.set(State.DISCONNECTED);
        }
    }

    private boolean stopDisconnecting() {
        assert state.enumEquals(State.DISCONNECTING) :
                "Method stopDisconnecting() can be called only from DISCONNECTING state";

        // If disconnecting (1st attempt)
        JavaThreads.softSleep(1000 + 100);
        return state.enumNotEquals(State.DISCONNECTING);
    }


    // Client connection methods - Client re-connection timer

    private void startConnectionTimer() {
        assert state.enumEquals(State.CONNECTING_WAITING_SERVER) :
                "Method startConnectionTimer() can be called only from CONNECTING_WAITING_SERVER state";

        long waitMs = TIMER_DELAY_MS;
        connectionTimer = new Timer(true);
        connectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                assert state.enumEquals(State.CONNECTING_WAITING_SERVER) :
                        "Method startConnectionTimer() can be called only from CONNECTING_WAITING_SERVER state";

                Thread.currentThread().setName(TIMER_CONNECTION_NAME_FRMT);
                try {
                    initConnection();

                } catch (IOException | AAAException ignore) { /* auto-start connection timer */}
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

        updateLastDataSend();
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


    // HeartBeat

    /**
     * {@inheritDoc}
     */
    @Override
    public HeartBeatConfigs getHeartBeatConfigs() {
        return heartBeat;
    }

    class ClientHearBeatListener implements DefaultHeartBeat.HeartBeatListener {

        @Override
        public void onSend() {
            log.debug(Mrk_Commons.COMM_CL, String.format("Client '%s' send HB to server '%s'", getClientId(), tryServerId()));
        }

        @Override
        public void onSuccess() {
            updateLastHeartbeat();
            log.debug(Mrk_Commons.COMM_CL, String.format("Client '%s' received HB response from server '%s'", getClientId(), tryServerId()));
        }

        @Override
        public void onError() {
            updateLastHeartbeatFailed();
            log.warn(Mrk_Commons.COMM_CL, String.format("Client '%s' reached HB timeout from server '%s', close connection.", getClientId(), tryServerId()));
            //closeConnection(true);
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                initConnection();
            } catch (IOException | AAAException ignore) { /* auto-start connection timer */}
        }

    }


    // Last connection updaters

    /**
     * Called when a connection was stabilised successfully with a server.
     */
    private void updateLastConnection() {
        lastConnection = JavaDate.getNowDate();
    }

    /**
     * Called when a connection was closed (because required or because an error).
     */
    private void updateLastDisconnection() {
        lastDisconnection = JavaDate.getNowDate();
    }

    /**
     * Called when ...
     */
    private void updateLastHeartbeat() {
        lastHeartbeat = JavaDate.getNowDate();
    }

    /**
     * Called when a client DON'T respond to a heartbeat request from current server.
     */
    protected void updateLastHeartbeatFailed() {
        lastHeartbeatFailed = JavaDate.getNowDate();
    }

    /**
     * Called when ...
     */
    private void updateLastDataReceived() {
        lastDataReceived = JavaDate.getNowDate();
    }

    /**
     * Called when ...
     */
    private void updateLastDataSend() {
        lastDataSend = JavaDate.getNowDate();
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
        boolean socketReadError = false;
        boolean socketResetError = false;
        boolean socketSSLHandshakeError = false;
        boolean socketSSLError = false;
        boolean serverHBTimeoutError = false;
        boolean serverTerminatedError = false;
        boolean heartbeatSendError = false;
        boolean clientDisconnect = false;
        boolean serverSendByeMsg = false;

        mustShutdown = false;
        byte[] dataBuffered = new byte[0];
        while (!mustShutdown) {
            byte[] dataRead;

            try {
                // Listen for server data
                byte[][] dataReadTmp = CommunicationBase.listenForData(server2ClientStream, dataBuffered, delimiter);

                if (dataReadTmp == null) {
                    socketReadError = true;
                    break;
                }

                dataRead = dataReadTmp[0];
                dataBuffered = dataReadTmp[1];

            } catch (IOException e) {
                if (mustShutdown) {
                    clientDisconnect = true;                                        //log.info(Mrk_Commons.COMM_SRV, String.format("Client '%s' disconnected to server '%s', client shutdown", getClientId(), tryServerId()));
                    break;
                }

                if (!isSocketConnected()) {
                    if (heartBeat.isTimedout())
                        serverHBTimeoutError = true;
                    else
                        serverTerminatedError = true;                                   //log.info(Mrk_Commons.COMM_SRV, String.format("Client '%s' disconnected to server '%s', server disconnected to client", getClientId(), tryServerId()));
                    break;
                }

                if (e instanceof SSLHandshakeException && e.getMessage().equals("Remote host closed connection during handshake")) {
                    socketSSLHandshakeError = true;                                          //log.warn(Mrk_Commons.COMM_SRV, String.format("Client '%s' can't connect, must sharing certificate with server '%s'", client.getClientId(), getServerId()));
                    break;
                }

                if (e instanceof SSLException && e.getMessage().equals("Connection has been shutdown: javax.net.ssl.SSLException: java.net.SocketException: Operation timed out (Read failed)")) {
                    socketSSLError = true;                                          //log.warn(Mrk_Commons.COMM_SRV, String.format("Client '%s' can't connect, must sharing certificate with server '%s'", client.getClientId(), getServerId()));
                    break;
                }


                if (e instanceof SocketTimeoutException && e.getMessage().equalsIgnoreCase("Read timed out")) {
                    if (!heartBeat.sendHeartBeat(delimiter)) {
                        heartbeatSendError = true;                                  //log.info(Mrk_Commons.COMM_SRV, String.format("Client '%s' disconnected to server '%s', can't send heartbeat", getClientId(), tryServerId()));
                        break;
                    } else
                        continue;
                }

                if (e instanceof SocketException && e.getMessage().equals("Connection reset")) {
                    socketResetError = true;                                        //log.warn(Mrk_Commons.COMM_SRV, String.format("Server '%s' disconnected, server reset connection to client  '%s'", client.getClientId(), getServerId()));
                    break;
                }

                // Rx error, but not client closed
                log.warn(Mrk_Commons.COMM_CL, String.format("Client can't read data from server '%s' because %s", getServerUrl(), e.getMessage()));
                if (cse != null) cse.onServerError(e);
                continue;
            }

            // Check and process HB message
            try {
                if (heartBeat.tryProcessHeartBeat(dataRead, delimiter))
                    continue;
            } catch (IOException e) {
                log.warn(Mrk_Commons.COMM_CL, String.format("Server processor can't process HB from '%s' data because %s", tryServerId(), e.getMessage()));
                log.debug(Mrk_Commons.COMM_CL, String.format("                                   (dataRx: %s)", new String(dataRead, PeerInfo.CHARSET)));
                if (cse != null) cse.onServerError(e);
            }

            // Check and process client's BYE message
            if (isSrvByeMsg(dataRead)) {
                serverSendByeMsg = true;
                break;
            }


            // Process received data from server
            updateLastDataReceived();
            try {
                if (!cme.onDataReceived(dataRead))
                    if (!cme.onDataReceived(new String(dataRead, PeerInfo.CHARSET))) {
                        log.warn(Mrk_Commons.COMM_CL, String.format("Server processor can't process data from '%s' data because unknown data", getServerId()));
                        log.warn(Mrk_Commons.COMM_SRV, String.format("                                   (dataRx: '%s')", new String(dataRead, PeerInfo.CHARSET)));
                    }

            } catch (Throwable e) {
                log.warn(Mrk_Commons.COMM_CL, String.format("Server processor can't process data from '%s' because %s", getServerUrl(), e.getMessage()), e);
                log.warn(Mrk_Commons.COMM_SRV, String.format("                                   (dataRx: '%s')", new String(dataRead, PeerInfo.CHARSET)));
                if (cse != null) cse.onServerError(e);
            }
        }

        String msg;
        if (socketReadError) msg = "Error reading data from socket (listenForData() method returned null)";
        else if (socketResetError) msg = "Error because server reset connection (catched SocketException)";
        else if (socketSSLHandshakeError) msg = "Error on SSL Handshake";
        else if (socketSSLError) msg = "Error on SSL";
        else if (serverHBTimeoutError) msg = "Error server didn't respond to HB";
        else if (serverTerminatedError) msg = "Error server disconnected with out BYEMSG";
        else if (heartbeatSendError) msg = "Error on send HB message";
        else if (clientDisconnect) msg = "Required client disconnection";
        else if (serverSendByeMsg) msg = "Required server shutdown";
        else msg = "Unknown";
        log.info(Mrk_Commons.COMM_SRV, String.format("### ### ###"));
        log.info(Mrk_Commons.COMM_SRV, String.format("### Client's server processor exit because: %s", msg));
        log.info(Mrk_Commons.COMM_SRV, String.format("### ### ###"));

        boolean wasMustShutdown = mustShutdown;
        if (isConnected() && (serverSendByeMsg || !mustShutdown))
            closeConnection(true);

        // Server disconnection events
        log.info(Mrk_Commons.COMM_CL, String.format("Disconnect server '%s' to '%s' client", getServerUrl(), getClientId()));
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

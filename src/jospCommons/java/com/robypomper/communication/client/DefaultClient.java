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
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLHandshakeException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;


/**
 * Default implementation of Client interface.
 */
public class DefaultClient extends CommunicationBase implements Client {

    // Class constants

    public static final String TH_CLI_NAME_FORMAT = "CLI-%s@%s";
    public static final String ID_SRV_FORMAT = "SRV-%s:%d";
    public static final String MSG_BYE_CLI_STR = "byecli";
    public static final byte[] MSG_BYE_CLI = MSG_BYE_CLI_STR.getBytes(PeerInfo.CHARSET);


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final String clientId;
    private final InetAddress serverAddr;
    private final int serverPort;
    private final ClientLocalEvents cle;
    private final ClientServerEvents cse;
    private final ClientMessagingEvents cme;
    private Socket clientSocket;
    private Thread clientThread;
    private boolean mustShutdown = false;
    private DataInputStream server2ClientStream;
    private DataOutputStream client2ServerStream;
    private ServerInfo serverInfo;
    private final boolean delimiter = true;
    private final boolean sleep = false;
    private Date lastConnection;
    private Date lastDisconnection;


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
    protected DefaultClient(String clientId, InetAddress serverAddr, int serverPort, ClientMessagingEvents clientMessagingEventsListener) {
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
    protected DefaultClient(String clientId, InetAddress serverAddr, int serverPort,
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


    // Client getter

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getServerAddr() {
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
     */
    @Override
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getClientAddr() {
        if (!isConnected())
            return null;
        return clientSocket.getLocalAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getClientPort() {
        if (!isConnected())
            return -1;
        return clientSocket.getLocalPort();
    }

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


    // Client connection methods

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws ConnectionException {
        if (isConnected()) {
            log.warn(Mrk_Commons.COMM_CL, String.format("Client '%s' already connected", getClientId()));
            return;
        }

        log.info(Mrk_Commons.COMM_CL, String.format("Connecting client '%s'", getClientId()));
        try {
            clientSocket = generateAndBoundClientSocket();
            client2ServerStream = new DataOutputStream(clientSocket.getOutputStream());
            server2ClientStream = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            throw new ConnectionException(String.format("Can't connect client '%s' because %s", getClientId(), e.getMessage()));
        }

        serverInfo = generateAndStartServerInfo(clientSocket);
        clientThread = serverInfo.getThread();
        lastConnection = JOSPProtocol.getNowDate();

        log.info(Mrk_Commons.COMM_CL, String.format("Client '%s' connected successfully", getClientId()));
        if (cle != null) cle.onConnected();
        if (cse != null) cse.onServerConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        disconnect(false);
    }

    /**
     * Internal version of {@link #disconnect()} method.
     * <p>
     * With param <code>internalCaller</code> set to <code>true</code> disable
     * exceptions and warning messages in the method.
     *
     * @param internalCaller <code>true</code> to disable exceptions and
     *                       warnings logs.
     */
    private void disconnect(boolean internalCaller) {
        if (!isConnected()) {
            log.warn(Mrk_Commons.COMM_CL, String.format("Client '%s' already disconnected", getClientId()));
            return;
        }

        log.info(Mrk_Commons.COMM_CL, String.format("Disconnecting client '%s'", getClientId()));
        try {
            CommunicationBase.transmitData(clientSocket.getOutputStream(), MSG_BYE_CLI);

        } catch (IOException e) {
            log.warn(Mrk_Commons.COMM_CL, String.format("Client '%s' can't send BYE message", getClientId()));
            if (cle != null) cle.onDisconnectionError(e);
        }

        // Terminate server thread
        mustShutdown = true;
        clientThread.interrupt();

        // Close server socket
        try {
            clientSocket.close();
        } catch (IOException e) {
            log.warn(Mrk_Commons.COMM_CL, String.format("Socket for client '%s' not closed", getClientId()));
            if (cle != null) cle.onDisconnectionError(e);
        }

        // Join server thread
        try {
            if (Thread.currentThread() != clientThread)
                clientThread.join(1000);


        } catch (InterruptedException e) {
            if (clientThread.isAlive() && !internalCaller) {
                log.warn(Mrk_Commons.COMM_CL, String.format("Thread for client '%s' not terminated", getClientId()));
                if (cle != null) cle.onDisconnectionError(e);
            }
        }

        try {
            if (!clientThread.isAlive() && !internalCaller) {
                log.info(Mrk_Commons.COMM_CL, String.format("Client '%s' disconnected successfully", getClientId()));
                if (cle != null) cle.onDisconnected();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();    // not corrected bug, probably because sync errors in tests execution
        }

        // Reset internal vars
        clientSocket = null;
        clientThread = null;
        client2ServerStream = null;
        server2ClientStream = null;
        lastDisconnection = JOSPProtocol.getNowDate();
    }


    // Messages methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(byte[] data) throws ServerNotConnectedException {
        if (!isConnected())
            throw new ServerNotConnectedException(getServerInfo().getServerId());

        try {
            transmitData(client2ServerStream, data, delimiter);

        } catch (IOException e) {
            throw new ServerNotConnectedException(getServerInfo().getServerId(), e);
        }

        log.debug(Mrk_Commons.COMM_CL, String.format("Client '%s' send to server '%s' data '%s...'", getClientId(), getServerInfo().getServerId(), truncateMid(data, 30)));
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
    protected ServerInfo generateAndStartServerInfo(Socket socket) {
        String serverId = String.format(ID_SRV_FORMAT, socket.getInetAddress(), socket.getPort());

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
        try {
            log.debug(Mrk_Commons.COMM_CL, String.format("server '%s' processor thread '%s' started", getServerInfo().getServerId(), Thread.currentThread().getName()));
        } catch (NullPointerException e) {
            log.debug(Mrk_Commons.COMM_CL, String.format("server '%s' processor thread '%s' started", getServerInfo(), Thread.currentThread().getName()));
            log.warn(Mrk_Commons.COMM_CL, String.format("server '%s' missing server's id", getServerInfo()));
        }
        mustShutdown = false;
        boolean serverSendByeMsg = false;
        DataInputStream in = server2ClientStream;
        byte[] dataBuffered = new byte[0];
        while (!mustShutdown) {

            try {
                // Listen for server data
                byte[][] dataReadTmp = DefaultClient.listenForData(in, dataBuffered, delimiter);

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
                            log.warn(Mrk_Commons.COMM_CL, String.format("Server processor can't process data from '%s' data because unknown data", getServerInfo().getServerId()));
                            log.debug(Mrk_Commons.COMM_CL, String.format("(dataRx: %s)", new String(dataRead, PeerInfo.CHARSET)));
                        }

                } catch (Throwable e) {
                    log.warn(Mrk_Commons.COMM_CL, String.format("Server processor can't process data from '%s' because %s", getServerInfo().getServerId(), e.getMessage()));
                    String dataStr = new String(dataRead, PeerInfo.CHARSET);
                    log.warn(Mrk_Commons.COMM_CL, String.format("(dataRx: '%s')", dataStr.substring(0, Math.min(dataStr.length(), 10))));
                    if (cse != null) cse.onServerError(e);
                }

            } catch (IOException e) {
                if (!getServerInfo().isConnected()
                        || mustShutdown
                        || (e instanceof SocketException && e.getMessage().equals("Connection reset"))
                        || (e instanceof SSLHandshakeException && e.getMessage().equals("Remote host closed connection during handshake"))
                )
                    break;

                // Rx error, but not client closed
                log.warn(Mrk_Commons.COMM_CL, String.format("Client can't read data from server '%s' because %s", getServerInfo().getServerId(), e.getMessage()));
                if (cse != null) cse.onServerError(e);
            }
        }

        boolean wasMustShutdown = mustShutdown;
        if (isConnected() && (serverSendByeMsg || !mustShutdown))
            disconnect(true);

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

        log.debug(Mrk_Commons.COMM_CL, String.format("Server '%s' processor thread terminated", getServerInfo().getServerId()));
    }

}

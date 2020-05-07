package com.robypomper.communication.client;

import com.robypomper.communication.client.events.ClientLocalEvents;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.ClientServerEvents;
import com.robypomper.communication.peer.PeerInfo;
import com.robypomper.communication.server.DefaultServer;
import com.robypomper.log.Markers;
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


/**
 * Default implementation of Client interface.
 */
public class DefaultClient implements Client {

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
    public String getClientId() {
        return clientId;
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
            log.warn(Markers.COMM_CL, String.format("Client '%s' already connected", getClientId()));
            return;
        }

        log.info(Markers.COMM_CL, String.format("Connecting client '%s'", getClientId()));


        try {
            clientSocket = generateAndBoundClientSocket();
            client2ServerStream = new DataOutputStream(clientSocket.getOutputStream());
            server2ClientStream = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            throw new ConnectionException(String.format("Can't connect client '%s' because %s", getClientId(), e.getMessage()));
        }

        serverInfo = generateAndStartServerInfo(clientSocket);
        clientThread = serverInfo.getThread();

        log.info(Markers.COMM_CL, String.format("Client '%s' connected successfully", getClientId()));
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
            log.warn(Markers.COMM_CL, String.format("Client '%s' already disconnected", getClientId()));
            return;
        }

        log.info(Markers.COMM_CL, String.format("Disconnecting client '%s'", getClientId()));
        try {
            clientSocket.getOutputStream().write(MSG_BYE_CLI);
        } catch (IOException e) {
            log.warn(Markers.COMM_CL, String.format("Client '%s' can't send BYE message", getClientId()));
            if (cle != null) cle.onDisconnectionError(e);
        }

        // Terminate server thread
        mustShutdown = true;
        clientThread.interrupt();

        // Close server socket
        try {
            clientSocket.close();
        } catch (IOException e) {
            log.warn(Markers.COMM_CL, String.format("Socket for client '%s' not closed", getClientId()));
            if (cle != null) cle.onDisconnectionError(e);
        }

        // Join server thread
        try {
            if (Thread.currentThread() != clientThread)
                clientThread.join(1000);


        } catch (InterruptedException e) {
            if (clientThread.isAlive() && !internalCaller) {
                log.warn(Markers.COMM_CL, String.format("Thread for client '%s' not terminated", getClientId()));
                if (cle != null) cle.onDisconnectionError(e);
            }
        }

        try {
            if (!clientThread.isAlive() && !internalCaller) {
                log.info(Markers.COMM_CL, String.format("Client '%s' disconnected successfully", getClientId()));
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
            client2ServerStream.write(data, 0, data.length);
        } catch (IOException e) {
            throw new ServerNotConnectedException(getServerInfo().getServerId(), e);
        }

        log.debug(Markers.COMM_CL, String.format("Client '%s' send to server '%s' data '%s...'", getClientId(), getServerInfo().getServerId(), new String(data, PeerInfo.CHARSET).substring(0, Math.min(data.length, 10))));
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
        log.debug(Markers.COMM_CL, String.format("client '%s' initialized as TCP client for '%s:%d' server", getClientId(), getServerAddr(), getServerPort()));
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
     * {@link ClientMessagingEvents#onDataReceived()} event.
     */
    @SuppressWarnings("JavadocReference")
    protected void processServer() {
        log.debug(Markers.COMM_CL, String.format("server '%s' processor thread '%s' started", getServerInfo().getServerId(), Thread.currentThread().getName()));

        mustShutdown = false;
        boolean serverSendByeMsg = false;
        while (!mustShutdown) {

            try {
                DataInputStream in = server2ClientStream;

                // Read data, force blocking read op
                int available = in.available();
                byte[] dataRead = new byte[available > 0 ? available : 1];
                int bytesRead = in.read(dataRead);

                // Check if disconnected by client
                if (bytesRead == -1)
                    break;

                // Check if there are missing data
                if (available == 0) {
                    byte[] dataMissing = new byte[in.available()];
                    int bytesMissing = in.read(dataMissing);
                    byte[] dataTmp = new byte[bytesRead + bytesMissing];
                    System.arraycopy(dataRead, 0, dataTmp, 0, bytesRead);
                    System.arraycopy(dataMissing, 0, dataTmp, bytesRead, bytesMissing);
                    dataRead = dataTmp;
                }

                // Process received data
                try {
                    if (isSrvByeMsg(dataRead)) {
                        serverSendByeMsg = true;
                        break;
                    }

                    if (!cme.onDataReceived(dataRead))
                        if (!cme.onDataReceived(new String(dataRead, PeerInfo.CHARSET))) {
                            log.warn(Markers.COMM_CL, String.format("Server processor can't process data from '%s' data because unknown data", getServerInfo().getServerId()));
                            log.debug(Markers.COMM_CL, String.format("(dataRx: %s)", new String(dataRead, PeerInfo.CHARSET)));
                        }

                } catch (Throwable e) {
                    log.warn(Markers.COMM_CL, String.format("Server processor can't process data from '%s' because %s", getServerInfo().getServerId(), e.getMessage()));
                    String dataStr = new String(dataRead, PeerInfo.CHARSET);
                    log.warn(Markers.COMM_CL, String.format("(dataRx: '%s')", dataStr.substring(0, Math.min(dataStr.length(), 10))));
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
                log.warn(Markers.COMM_SRV, String.format("Client can't read data from server '%s' because %s", getServerInfo().getServerId(), e.getMessage()));
                if (cse != null) cse.onServerError(e);
            }
        }


        // Client disconnection events
        if (cse != null) {
            if (serverSendByeMsg) {
                cse.onServerGoodbye();
                if (isConnected()) disconnect(true);

            } else if (mustShutdown)
                cse.onServerClientDisconnected();

            else {
                cse.onServerTerminated();
                if (isConnected()) disconnect(true);
            }
            cse.onServerDisconnection();
        }

        log.debug(Markers.COMM_CL, String.format("server '%s' processor thread terminated", getServerInfo().getServerId()));
    }

}

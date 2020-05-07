package com.robypomper.communication.server;

import com.robypomper.communication.client.DefaultClient;
import com.robypomper.communication.peer.PeerInfo;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerLocalEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import com.robypomper.log.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Default implementation of Server interface.
 */
public class DefaultServer implements Server {

    // Class constants

    public static final String TH_SRV_NAME_FORMAT = "SRV-%s";
    public static final String TH_CLI_NAME_FORMAT = "CLI-%s@%s";
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


    // Constructor

    /**
     * Default constructor that initialize a server on <code>port</code> port
     * and with given id.
     *
     * @param serverId                      the server id.
     * @param port                          the server's port.
     * @param serverMessagingEventsListener the tx and rx messaging listener.
     */
    protected DefaultServer(String serverId, int port, ServerMessagingEvents serverMessagingEventsListener) {
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
    protected DefaultServer(String serverId, int port,
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
    }


    // Server getter

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
    public void start() throws ListeningException {
        if (isRunning()) {
            log.warn(Markers.COMM_SRV, String.format("Server '%s' already started", getServerId()));
            return;
        }

        log.info(Markers.COMM_SRV, String.format("Starting server '%s'", getServerId()));

        try {
            serverSocket = generateAndBoundServerSocket();
        } catch (IOException e) {
            throw new ListeningException(String.format("Can't start server '%s' because %s", getServerId(), e.getMessage()));
        }

        serverThread = new Thread(this::infiniteLoop);
        serverThread.setName(String.format(TH_SRV_NAME_FORMAT, getServerId()));
        serverThread.start();

        log.info(Markers.COMM_SRV, String.format("Server '%s' started successfully", getServerId()));
        if (sle != null) sle.onStarted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (!isRunning()) {
            log.warn(Markers.COMM_SRV, String.format("Server '%s' already stopped", getServerId()));
            return;
        }

        log.info(Markers.COMM_SRV, String.format("Stopping server '%s'", getServerId()));

        // Terminate server thread
        mustShutdown = true;
        serverThread.interrupt();

        // Close server socket
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.warn(Markers.COMM_SRV, String.format("Lister socket for server '%s' not closed", getServerId()));
            if (sle != null) sle.onStopError(e);
        }

        // Join server thread
        try {
            serverThread.join(1000);


        } catch (InterruptedException e) {
            if (serverThread.isAlive()) {
                log.warn(Markers.COMM_SRV, String.format("Thread for server '%s' not terminated", getServerId()));
                if (sle != null) sle.onStopError(e);
            }
        }

        if (!serverThread.isAlive()) {
            log.info(Markers.COMM_SRV, String.format("Server '%s' stopped successfully", getServerId()));
            if (sle != null) sle.onStopped();
        }

        // Disconnect clients
        for (ClientInfo c : getClients())
            if (c.isConnected()) {
                if (c.closeConnection())
                    log.debug(Markers.COMM_SRV, String.format("Client '%s' disconnected", c.getClientId()));
                else
                    log.debug(Markers.COMM_SRV, String.format("Client '%s' not disconnected", c.getClientId()));
            }

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
            DataOutputStream out = client.getOutStream();
            out.write(data, 0, data.length);
        } catch (PeerInfo.PeerNotConnectedException | PeerInfo.PeerStreamsException | IOException e) {
            throw new ClientNotConnectedException(client.getClientId(), e);
        }

        log.debug(Markers.COMM_SRV, String.format("Server '%s' send to client '%s' data '%s...'", getServerId(), client.getClientId(), new String(data, PeerInfo.CHARSET).substring(0, 10)));
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
        return Arrays.equals(DefaultClient.MSG_BYE_CLI, data);
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


    // Subclassing customization

    /**
     * Generate a {@link ServerSocket} and bound it on server's port.
     * <p>
     * Subclasses can override this method to set a different ServerSocket instance.
     *
     * @return the ServerSocket instance.
     */
    protected ServerSocket generateAndBoundServerSocket() throws IOException {
        log.debug(Markers.COMM_SRV, String.format("server '%s' initialized as TCP server on port '%d'", getServerId(), getPort()));
        return new ServerSocket(getPort());
    }

    /**
     * Generate a {@link ClientInfo} based on give socket.
     *
     * @return the ClientInfo instance representing given client's socket.
     */
    protected ClientInfo generateAndStartClientInfo(Socket socket) {
        String clientId = String.format(ID_CLI_FORMAT, socket.getInetAddress(), socket.getPort());

        DefaultClientInfo clientInfo = new DefaultClientInfo(socket, clientId);
        Thread clientThread = new Thread(() -> processClient(clientInfo));
        clientInfo.setThread(clientThread);
        clientThread.setName(String.format(TH_CLI_NAME_FORMAT, clientId.substring(3), getServerId()));
        clientThread.start();

        return new DefaultClientInfo(socket, clientId);
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
        log.debug(Markers.COMM_SRV, String.format("server '%s' loop thread '%s' started", getServerId(), Thread.currentThread().getName()));

        mustShutdown = false;

        while (!mustShutdown) {
            try {
                ClientInfo newClient = generateAndStartClientInfo(serverSocket.accept());
                if (newClient == null)
                    continue;

                clients.add(newClient);
                if (sce != null) sce.onClientConnection(newClient);

            } catch (IOException e) {
                if (!mustShutdown && !serverSocket.isClosed()) {
                    log.warn(Markers.COMM_SRV, String.format("Server '%s' loop interrupted because %s", getServerId(), e.getMessage()));
                    if (sle != null) sle.onServerError(e);
                }
                break;
            }
        }

        log.debug(Markers.COMM_SRV, String.format("server '%s' loop thread terminated", getServerId()));
    }

    /**
     * The client's process thread.
     * <p>
     * This tread receive all data rx from the client and emit the
     * {@link ServerMessagingEvents#onDataReceived()} event.
     *
     * @param client the reference of client that send data to current server.
     */
    @SuppressWarnings("JavadocReference")
    protected void processClient(DefaultClientInfo client) {
        log.debug(Markers.COMM_SRV, String.format("client '%s' processor thread '%s' started", client.getClientId(), Thread.currentThread().getName()));

        // Get client's input stream
        DataInputStream in;
        try {
            in = client.getInStream();
        } catch (PeerInfo.PeerNotConnectedException | PeerInfo.PeerStreamsException e) {
            log.error(Markers.COMM_SRV, String.format("Client processor can't get input stream from client '%s' because %s", client, e.getMessage()));
            if (sce != null) sce.onClientError(client, e);
            return;
        }

        boolean clientSendByeMsg = false;
        while (!mustShutdown) {
            try {

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
                    if (isCliByeMsg(dataRead)) {
                        clientSendByeMsg = true;
                        break;
                    }

                    if (!sme.onDataReceived(client, dataRead))
                        if (!sme.onDataReceived(client, new String(dataRead, PeerInfo.CHARSET))) {
                            log.warn(Markers.COMM_SRV, String.format("Client processor can't process data from '%s' data because unknown data", client));
                            log.debug(Markers.COMM_SRV, String.format("(dataRx: %s)", new String(dataRead, PeerInfo.CHARSET)));
                        }

                } catch (Throwable e) {
                    log.warn(Markers.COMM_SRV, String.format("Client processor can't process data from '%s' because %s", client, e.getMessage()));
                    log.debug(Markers.COMM_SRV, String.format("(dataRx: '%s')", new String(dataRead, PeerInfo.CHARSET)));
                    if (sce != null) sce.onClientError(client, e);
                }

            } catch (IOException e) {
                if (mustShutdown || !client.isConnected())
                    break;

                if ((e instanceof SSLException && e.getMessage().startsWith("Received fatal alert: internal_error"))) {
                    log.warn(Markers.COMM_SRV, String.format("Client '%s' can't connect, wait for sharing certificate with server '%s'", client.getClientId(), getServerId()));
                    break;
                }

                // Rx error, but not client closed
                log.warn(Markers.COMM_SRV, String.format("Server can't read data from client '%s' because %s", client.getClientId(), e.getMessage()));
                if (sce != null) sce.onClientError(client, e);

                if ((e instanceof SocketException && e.getMessage().equals("Connection reset"))) {
                    break;
                }
            }
        }

        // Client disconnection events
        clients.remove(client);
        if (sce != null) {
            if (clientSendByeMsg) {
                sce.onClientGoodbye(client);
                if (client.isConnected()) client.closeConnection();

            } else if (mustShutdown) {
                sce.onClientServerDisconnected(client);
            } else {
                sce.onClientTerminated(client);
                if (client.isConnected()) client.closeConnection();
            }
            sce.onClientDisconnection(client);
        }

        log.debug(Markers.COMM_SRV, String.format("client '%s' processor thread terminated", client));
    }

}

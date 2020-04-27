package com.robypomper.communication.server;

import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerLocalEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import com.robypomper.log.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * SSL implementation of Server interface.
 *
 * This implementation provide a SSL based server.
 */
public class DefaultSSLServer extends DefaultServer {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final SSLContext sslCtx;
    private final boolean requireAuth;


    // Constructor

    /**
     * Default constructor that initialize a SSL server on <code>port</code> port
     * and with given id. This server is initialized on given {@link SSLContext}
     * instance.
     *
     * @param sslCtx the SSL context instance used to instantiate server's components.
     * @param serverId the server id.
     * @param port the server's port.
     * @param requireAuth if set to <code>true</code> the server require
     *                    authentication from clients (client must provide their
     *                    certificate).
     * @param serverMessagingEventsListener the tx and rx messaging listener.
     */
    public DefaultSSLServer(SSLContext sslCtx, String serverId, int port, boolean requireAuth, ServerMessagingEvents serverMessagingEventsListener) {
        super(serverId, port, serverMessagingEventsListener);
        this.sslCtx = sslCtx;
        this.requireAuth = requireAuth;
    }

    /**
     * Full constructor that initialize a SSL server on <code>port</code> port
     * and with given id. This server is initialized on given {@link SSLContext}
     * instance.
     *
     * @param sslCtx the SSL context instance used to instantiate server's components.
     * @param serverId the server id.
     * @param port the server's port.
     * @param requireAuth if set to <code>true</code> the server require
     *                    authentication from clients (client must provide their
     *                    certificate).
     * @param serverLocalEventsListener the local server events listener.
     * @param serverClientEventsListener the clients events listener.
     * @param serverMessagingEventsListener the tx and rx messaging listener.
     */
    public DefaultSSLServer(SSLContext sslCtx, String serverId, int port, boolean requireAuth, ServerLocalEvents serverLocalEventsListener, ServerClientEvents serverClientEventsListener, ServerMessagingEvents serverMessagingEventsListener) {
        super(serverId, port, serverLocalEventsListener, serverClientEventsListener, serverMessagingEventsListener);
        this.sslCtx = sslCtx;
        this.requireAuth = requireAuth;
    }


    // Subclassing customization

    /**
     * {@inheritDoc}
     *
     * This implementation use set {@link SSLContext} to initialize a new
     * {@link SSLServerSocket}.
     *
     * @return the ServerSocket instance.
     */
    protected ServerSocket generateAndBoundServerSocket() throws IOException {
        log.debug(Markers.COMM_SSL_SRV, String.format("Server '%s' initialized as SSL TCP server on port '%d'", getServerId(), getPort()));
        SSLServerSocket sslSS = (SSLServerSocket) sslCtx.getServerSocketFactory().createServerSocket(getPort());
        sslSS.setNeedClientAuth(requireAuth);
        return sslSS;
    }

    /**
     * {@inheritDoc}
     *
     * This implementation use the client's certificate commonName as client
     * id. This value is read from SSL session, so client must start the handshake
     * right after the connection is established.
     *
     * @return the ClientInfo instance representing given client's socket.
     */
    protected ClientInfo generateAndStartClientInfo(Socket socket) {
        SSLSocket sslSocket = (SSLSocket) socket;
        String id = null;

        if (requireAuth)
            try {
                id = UtilsSSL.getPeerId(sslSocket, log);

            } catch (Throwable t) {
                log.warn(Markers.COMM_SSL_SRV, String.format("Can't get client id from SSL session because %s", t.getMessage()));
                log.debug(Markers.COMM_SSL_SRV, "Close connection and discharge client");
                try {
                    socket.close();
                } catch (IOException ignore) {}
                return null;
            }

        if (id == null) {
            id = String.format(ID_CLI_FORMAT, socket.getInetAddress(), socket.getPort());
        }

        String clientId = id;
        String clientId4ThName = clientId.startsWith("CL-") ? clientId.substring(3) : clientId;
        String thName = String.format(TH_CLI_NAME_FORMAT, clientId4ThName, getServerId());

        Thread clientThread = new Thread(() -> processClient(clientId));
        clientThread.setName(thName);
        clientThread.start();

        return new DefaultClientInfo(socket, clientId, clientThread);
    }

}

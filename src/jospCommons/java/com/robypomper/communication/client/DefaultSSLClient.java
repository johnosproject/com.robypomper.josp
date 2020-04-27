package com.robypomper.communication.client;

import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.events.ClientLocalEvents;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.ClientServerEvents;
import com.robypomper.log.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


/**
 * SSL implementation of Client interface.
 * <p>
 * This implementation provide a SSL based client.
 */
public class DefaultSSLClient extends DefaultClient {

    // Class constants

    public static final String TH_CLI_NAME_FORMAT = "CLI-%s@%s";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final SSLContext sslCtx;


    // Constructor

    /**
     * Full constructor that initialize a client for <code>serverAddr:serverPort</code>
     * server and with given id.
     *
     * @param sslCtx                        the SSL context instance used to instantiate server's components.
     * @param clientId                      the client id.
     * @param serverAddr                    the server's address.
     * @param serverPort                    the server's port.
     * @param clientMessagingEventsListener the tx and rx messaging listener.
     */
    protected DefaultSSLClient(SSLContext sslCtx, String clientId, InetAddress serverAddr, int serverPort, ClientMessagingEvents clientMessagingEventsListener) {
        this(sslCtx, clientId, serverAddr, serverPort,
                null,
                null,
                clientMessagingEventsListener);
    }

    /**
     * Full constructor that initialize a client for <code>serverAddr:serverPort</code>
     * server and with given id.
     *
     * @param sslCtx                        the SSL context instance used to instantiate server's components.
     * @param clientId                      the client id.
     * @param serverAddr                    the server's address.
     * @param serverPort                    the server's port.
     * @param clientLocalEventsListener     the local client events listener.
     * @param clientServerEventsListener    the server events listener.
     * @param clientMessagingEventsListener the tx and rx messaging listener.
     */
    protected DefaultSSLClient(SSLContext sslCtx, String clientId, InetAddress serverAddr, int serverPort,
                               ClientLocalEvents clientLocalEventsListener,
                               ClientServerEvents clientServerEventsListener,
                               ClientMessagingEvents clientMessagingEventsListener) {
        super(clientId, serverAddr, serverPort, clientLocalEventsListener, clientServerEventsListener, clientMessagingEventsListener);

        this.sslCtx = sslCtx;
    }


    // Subclassing customization

    /**
     * {@inheritDoc}
     * <p>
     * This implementation use set {@link SSLContext} to initialize a new
     * {@link SSLSocket}.
     *
     * @return the ServerSocket instance.
     */
    protected Socket generateAndBoundClientSocket() throws IOException {
        log.debug(Markers.COMM_CL, String.format("client '%s' initialized as SSL TCP client for '%s:%d' server", getClientId(), getServerAddr(), getServerPort()));
        SSLSocket s = (SSLSocket) sslCtx.getSocketFactory().createSocket(getServerAddr(), getServerPort());
        s.startHandshake();
        return s;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation use the server's certificate commonName as server
     * id. This value is read from SSL session, so client must start the handshake
     * right after the connection is established.
     *
     * @return the ClientInfo instance representing given client's socket.
     */
    protected ServerInfo generateAndStartServerInfo(Socket socket) {
        SSLSocket sslSocket = (SSLSocket) socket;
        String id;
        try {
            id = UtilsSSL.getPeerId(sslSocket, log);

        } catch (Throwable t) {
            log.warn(Markers.COMM_SSL_CL, String.format("Can't get server id from SSL session because %s", t.getMessage()));
            log.debug(Markers.COMM_SSL_CL, "Close connection to server");
            try {
                socket.close();
            } catch (IOException ignore) {}
            return null;
        }

        String serverId = id;

        Thread clientThread = new Thread(this::processServer);
        clientThread.setName(String.format(TH_CLI_NAME_FORMAT, getClientId(), serverId));
        ServerInfo serverInfo = new DefaultServerInfo(socket, serverId, clientThread);
        clientThread.start();
        return serverInfo;
    }

}

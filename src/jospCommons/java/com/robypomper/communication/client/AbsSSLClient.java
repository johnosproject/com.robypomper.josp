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

import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.events.ClientLocalEvents;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.ClientServerEvents;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;


/**
 * SSL implementation of Client interface.
 * <p>
 * This implementation provide a SSL based client.
 */
@SuppressWarnings("unused")
public abstract class AbsSSLClient extends AbsClient {

    // Class constants

    public static final String TH_CLI_NAME_FORMAT = "CLI-%s@%s";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    // SSL
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
    protected AbsSSLClient(SSLContext sslCtx, String clientId, String serverAddr, int serverPort, ClientMessagingEvents clientMessagingEventsListener) {
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
    public AbsSSLClient(SSLContext sslCtx, String clientId, String serverAddr, int serverPort,
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
        log.debug(Mrk_Commons.COMM_SSL_CL, String.format("client '%s' initialized as SSL TCP client for '%s:%d' server", getClientId(), getServerAddr(), getServerPort()));
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
            log.warn(Mrk_Commons.COMM_SSL_CL, String.format("Can't get server id from SSL session because %s", t.getMessage()));
            log.debug(Mrk_Commons.COMM_SSL_CL, "Close connection to server");
            try {
                socket.close();
            } catch (IOException ignore) {
            }
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

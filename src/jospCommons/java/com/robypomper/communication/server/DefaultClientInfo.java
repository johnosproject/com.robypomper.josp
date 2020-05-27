package com.robypomper.communication.server;

import com.robypomper.communication.peer.DefaultPeerInfo;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;


/**
 * Default implementation of ClientInfo interface.
 */
public class DefaultClientInfo extends DefaultPeerInfo implements ClientInfo {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();
    private final String clientId;
    private final String serverId;
    private Thread thread = null;


    // Constructor

    /**
     * Default constructor that initialize the ClientInfo.
     * <p>
     * Remember to set the corresponding thread, immediately after call this
     * constructor and create the thread.
     *
     * @param socket   the communication channel socket.
     * @param clientId the represented client's id.
     * @param serverId the server's id.
     */
    public DefaultClientInfo(Socket socket, String clientId, String serverId) {
        super(socket);
        this.clientId = clientId;
        this.serverId = serverId;
        log.info(Mrk_Commons.COMM_SRV, String.format("Initialized DefaultClientInfo instance for '%s' client '%s' of '%s' server", clientId, socket.getInetAddress(), socket.getPort()));
        log.debug(Mrk_Commons.COMM_SRV, String.format("                                       from '%s:%d' (client) to '%s:%d' (server)", socket.getInetAddress(), socket.getPort(), socket.getLocalAddress(), socket.getLocalPort()));
    }


    // Client getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientId() {
        return clientId;
    }


    // Thread getters

    /**
     * Set the client processor thread.
     * <p>
     * This method can be called only one time.
     *
     * @param thread the thread that process data received from the client.
     */
    public void setThread(Thread thread) {
        if (this.thread != null)
            throw new IllegalArgumentException("Thread can be set only once");
        this.thread = thread;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProcessorRunning() {
        return thread.isAlive();
    }


    // Connection mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean closeConnection() {
        if (!getSocket().isConnected())
            return true;

        log.debug(Mrk_Commons.COMM_SRV, String.format("Disconnecting client '%s' from server '%s'", getClientId(), serverId));

        // Send goodbye message
        try {
            log.trace(Mrk_Commons.COMM_SRV, String.format("Sending goodbye message to client '%s'", getClientId()));
            getSocket().getOutputStream().write(DefaultServer.MSG_BYE_SRV);
        } catch (IOException e) {
            log.warn(Mrk_Commons.COMM_SRV, String.format("Can't send goodbye message to disconnecting client '%s' because %s", getClientId(), e.getMessage()), e);
        }

        // Close client's socket
        try {
            log.trace(Mrk_Commons.COMM_SRV, String.format("Closing client '%s' socket", getClientId()));
            getSocket().close();
        } catch (IOException e) {
            return false;
        }


        log.debug(Mrk_Commons.COMM_SRV, String.format("Client '%s'%s disconnected", getClientId(), getSocket().isConnected() ? "" : " NOT"));
        return getSocket().isConnected();
    }

}

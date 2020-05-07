package com.robypomper.communication.server;

import com.robypomper.communication.peer.DefaultPeerInfo;

import java.io.IOException;
import java.net.Socket;


/**
 * Default implementation of ClientInfo interface.
 */
public class DefaultClientInfo extends DefaultPeerInfo implements ClientInfo {

    // Internal vars

    private final String clientId;
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
     */
    public DefaultClientInfo(Socket socket, String clientId) {
        super(socket);
        this.clientId = clientId;
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

        try {
            getSocket().getOutputStream().write(DefaultServer.MSG_BYE_SRV);
        } catch (IOException e) {
            //throw new Server.ClientNotConnectedException(getClientId(),e);
            // log warn cant send bye msg
        }

        try {
            getSocket().close();
        } catch (IOException e) {
            return false;
        }

        return getSocket().isConnected();
    }

}

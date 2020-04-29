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
    private final Thread thread;


    // Constructor

    /**
     * Default constructor that initialize the ClientInfo.
     *
     * @param socket the communication channel socket.
     * @param clientId the represented client's id.
     * @param thread the thread charged to process all client requests.
     */
    public DefaultClientInfo(Socket socket, String clientId, Thread thread) {
        super(socket);
        this.clientId = clientId;
        this.thread = thread;
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

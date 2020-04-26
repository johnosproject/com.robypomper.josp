package com.robypomper.communication.server;

import com.robypomper.communication.peer.PeerInfo;


/**
 * The ClientInfo class provide information to the server's client.
 */
public interface ClientInfo extends PeerInfo {

    // Client getters

    /**
     * @return the client id.
     */
    String getClientId();


    // Thread getters

    /**
     * @return <code>true</code> if the client processor thread is running.
     */
    boolean isProcessorRunning();


    // Connection mngm

    /**
     * Close the client connection (on server side).
     *
     * @return <code>true</code> if the connection can closed successfully.
     */
    boolean closeConnection();

}

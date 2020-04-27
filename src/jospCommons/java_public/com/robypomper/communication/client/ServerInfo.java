package com.robypomper.communication.client;

import com.robypomper.communication.peer.PeerInfo;


/**
 * The ServerInfo class provide information to the client connected server.
 */
public interface ServerInfo extends PeerInfo {

    // Server getters

    /**
     * @return the server id.
     */
    String getServerId();


    // Thread getters

    /**
     * @return <code>true</code> if the server processor thread is running.
     */
    boolean isProcessorRunning();

    /**
     * @return the reference to the server processor's thread.
     */
    Thread getThread();

}

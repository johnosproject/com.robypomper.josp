package com.robypomper.communication.client;

import com.robypomper.communication.peer.DefaultPeerInfo;

import java.net.Socket;


/**
 * Default implementation of ServerInfo interface.
 */
public class DefaultServerInfo extends DefaultPeerInfo implements ServerInfo {

    // Internal vars

    private final String serverId;
    private final Thread thread;


    // Constructor

    /**
     * Default constructor that initialize the ServerInfo.
     *
     * @param socket   the communication channel socket.
     * @param serverId the represented client's id.
     * @param thread   the thread charged to process all client requests.
     */
    public DefaultServerInfo(Socket socket, String serverId, Thread thread) {
        super(socket);
        this.serverId = serverId;
        this.thread = thread;
    }


    // Server getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerId() {
        return serverId;
    }


    // Thread getters

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProcessorRunning() {
        return thread.isAlive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Thread getThread() {
        return thread;
    }

}

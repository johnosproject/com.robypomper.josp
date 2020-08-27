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

    // Class constants

    public static final String TH_CLI_NAME_FORMAT = "_CLI_%s@%s";


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
        log.info(Mrk_Commons.COMM_SRV, String.format("Initialized DefaultClientInfo instance for '%s' client '%s:%d' of '%s' server", clientId, socket.getInetAddress(), socket.getPort(), serverId));
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
     * Set and start the client processor thread.
     * <p>
     * This method can be called only one time.
     *
     * @param thread the thread that process data received from the client.
     */
    public void startThread(Thread thread) {
        if (this.thread != null)
            throw new IllegalArgumentException("Thread can be set only once");

        this.thread = thread;
        thread.setName(String.format(TH_CLI_NAME_FORMAT, getClientId(), serverId));
        log.debug(Mrk_Commons.COMM_SRV, String.format("Starting thread client processor for '%s' client", clientId));
        thread.start();
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
            log.warn(Mrk_Commons.COMM_SRV, String.format("Can't send goodbye message to disconnecting client '%s' because %s", getClientId(), e.getMessage()));//, e);
        }

        // Close client's socket
        try {
            log.trace(Mrk_Commons.COMM_SRV, String.format("Closing client '%s' socket", getClientId()));
            getSocket().close();
        } catch (IOException e) {
            log.warn(Mrk_Commons.COMM_SRV, String.format("Error on close client '%s' socket because %s ", getClientId(), e.getMessage()), e);
        }

        // Join server thread
        if (!Thread.currentThread().equals(thread)) {

            log.debug(Mrk_Commons.COMM_SRV, String.format("Terminating thread client processor '%s'", thread.getName()));

            try {
                thread.join(1000);

            } catch (InterruptedException e) {
                if (thread.isAlive())
                    log.warn(Mrk_Commons.COMM_SRV, String.format("Thread client processor '%s' not terminated because %s", thread.getName(), e.getMessage()), e);
            }

            if (!thread.isAlive())
                log.debug(Mrk_Commons.COMM_SRV, String.format("Thread client processor '%s' stopped", thread.getName()));
            else
                log.warn(Mrk_Commons.COMM_SRV, String.format("Thread client processor '%s' NOT stopped", thread.getName()));
        }


        log.debug(Mrk_Commons.COMM_SRV, String.format("Client '%s'%s disconnected", getClientId(), getSocket().isConnected() ? "" : " NOT"));
        return getSocket().isConnected();
    }

}

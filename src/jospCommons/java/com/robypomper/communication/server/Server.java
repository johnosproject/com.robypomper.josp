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

import java.net.InetAddress;
import java.util.List;


/**
 * Network server interface.
 */
public interface Server {

    // Server getter

    /**
     * Return the address bounded by current server.
     *
     * @return port used by the server.
     */
    InetAddress getAddress();

    /**
     * Return the port bounded by current server.
     *
     * @return port used by the server.
     */
    int getPort();

    /**
     * @return the server id.
     */
    String getServerId();


    // Server listening methods

    /**
     * @return true if the server is running.
     */
    boolean isRunning();

    /**
     * Initialize the server and start listening for new connections.
     */
    void start() throws ListeningException;

    /**
     * Stop the server, close client connections and release all server's resources.
     */
    void stop();


    // Messages methods

    /**
     * Method to send data from the server to the specified client.
     */
    void sendData(String clientId, byte[] data) throws ServerStoppedException, ClientNotFoundException, ClientNotConnectedException;

    /**
     * Method to send data from the server to the specified client.
     */
    void sendData(ClientInfo client, byte[] data) throws ServerStoppedException, ClientNotConnectedException;

    /**
     * Method to send data from the server to the specified client.
     */
    void sendData(String clientId, String data) throws ServerStoppedException, ClientNotFoundException, ClientNotConnectedException;

    /**
     * Method to send data from the server to the specified client.
     */
    void sendData(ClientInfo client, String data) throws ServerStoppedException, ClientNotConnectedException;

    /**
     * Return <code>true</code> if given param is a "good bye" message from the
     * client.
     */
    boolean isCliByeMsg(byte[] data);


    // Clients mngm

    /**
     * Return connected clients list.
     *
     * @return an array containing all known clients, also the disconnected once.
     */
    List<ClientInfo> getClients();

    /**
     * Looks for given id on client list.
     *
     * @param clientId the client id searched.
     * @return the ClientInfo corresponding to given client id, or null if not found.
     */
    ClientInfo findClientById(String clientId);

    /**
     * Looks for given id on client list.
     *
     * @param clientId the client id searched.
     * @return the ClientInfo corresponding to given client id, or throw an
     *         {@link ClientNotFoundException}.
     */
    ClientInfo getClientById(String clientId) throws ClientNotFoundException;


    // Exception

    /**
     * Exceptions thrown on client to server connection errors.
     */
    class ListeningException extends Throwable {
        public ListeningException(String msg) {
            super(msg);
        }

        public ListeningException(String msg, Throwable e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions thrown when the client try to send/receive the data to/from
     * the server but the connection is closed.
     */
    class ServerStoppedException extends Throwable {
        private static final String MSG = "Can't tx/rx data because the connection is closed.";

        public ServerStoppedException() {
            super(MSG);
        }
    }

    /**
     * Exceptions thrown when the server try to access to a not existing client.
     */
    class ClientNotFoundException extends Throwable {
        private static final String MSG = "Client '%s' not found.";

        public ClientNotFoundException(String clientId) {
            super(String.format(MSG, clientId));
        }
    }

    /**
     * Exceptions thrown when the server try to send data to a not connected client.
     */
    class ClientNotConnectedException extends Throwable {
        private static final String MSG = "Client '%s' not connected.";

        public ClientNotConnectedException(String clientId, Throwable e) {
            super(String.format(MSG, clientId), e);
        }
    }

}

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

package com.robypomper.communication_deprecated.server;

import com.robypomper.communication_deprecated.peer.HeartBeatConfigs;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;


/**
 * Network server interface.
 */
public interface Server {

    // Server getter

    /**
     * Return the address bounded by current server.
     *
     * @return address bounded by the socket.
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
     * @return the Date when the server was started for the last time, null
     * if connection was never opened.
     */
    Date getLastStartup();

    /**
     * @return the Date when the server was shutdown for the last time, null
     * if connection was never opened.
     */
    Date getLastShutdown();

    /**
     * @return the Date when last connection was opened successfully, null
     * if connection was never opened.
     */
    Date getLastClientConnection();

    /**
     * @return the ClientInfo of last connection opened successfully, null
     * if connection was never opened.
     */
    ClientInfo getLastClientConnectionClient();

    /**
     * @return the Date when last connection was closed, null if connection
     * was never closed.
     */
    Date getLastClientDisconnection();

    /**
     * @return the ClientInfo of last connection closed, null if connection
     * was never closed.
     */
    ClientInfo getLastClientDisconnectionClient();

    /**
     * @return the Date when last heartbeat was received, null if never received
     * heartbeat.
     */
    Date getLastHeartbeat();

    /**
     * @return the ClientInfo of last heartbeat received, null if never received
     * heartbeat.
     */
    ClientInfo getLastHeartbeatClient();

    /**
     * @return the Date when last heartbeat failed, null if heartbeat never
     * failed.
     */
    Date getLastHeartbeatFailed();

    /**
     * @return the ClientInfo of last heartbeat failed, null if heartbeat never
     * failed.
     */
    ClientInfo getLastHeartbeatClientFailed();

    /**
     * @return the Date when last data was received, null if never received
     * data.
     */
    Date getLastDataReceived();

    /**
     * @return the ClientInfo of last data received, null if never received
     * data.
     */
    ClientInfo getLastDataReceivedClient();

    /**
     * @return the Date when last data was send, null if never send
     * data.
     */
    Date getLastDataSend();

    /**
     * @return the ClientInfo of last data send, null if never send
     * data.
     */
    ClientInfo getLastDataSendClient();

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
     * {@link ClientNotFoundException}.
     */
    ClientInfo getClientById(String clientId) throws ClientNotFoundException;


    // Standard behaviours

    /**
     * @return the HeartBeat object associated to current server.
     */
    HeartBeatConfigs getHeartBeatConfigs();


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

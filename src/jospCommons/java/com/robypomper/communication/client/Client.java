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

import com.robypomper.josp.states.StateException;

import java.io.IOException;
import java.util.Date;


/**
 * Network client interface.
 */
@SuppressWarnings("unused")
public interface Client {

    // Getter state

    /**
     * @return the Client's state.
     */
    State getState();

    /**
     * @return true only if current Client is connected
     */
    boolean isConnected();

    /**
     * @return true only if current Client is connecting (or waiting for
     * re-connecting).
     */
    boolean isConnecting();

    /**
     * @return true only if current Client is NOT connected and is not try to
     * reconnect to the designed JCP service.
     */
    boolean isDisconnecting();

    /**
     * @return the Date when last connection was opened successfully, null
     * if connection was never opened.
     */
    Date getLastConnection();

    /**
     * @return the Date when last connection was closed, null if connection
     * was never closed.
     */
    Date getLastDisconnection();


    // Getter configs

    /**
     * @return current client ID.
     */
    String getClientId();

    /**
     * Version of {@link #getClientHostname()} that NOT throws exception.
     *
     * @return {@link #getClientAddr()} + ":" + {@link #getClientPort()}
     */
    String tryClientHostname();

    /**
     * @return {@link #getClientAddr()} + ":" + {@link #getClientPort()}
     * @throws ServerNotConnectedException if client is not connected to a server.
     */
    String getClientHostname() throws ServerNotConnectedException;

    /**
     * Version of {@link #getClientAddr()} that NOT throws exception.
     *
     * @return current client address.
     */
    String tryClientAddr();

    /**
     * @return current client address.
     * @throws ServerNotConnectedException if client is not connected to a server.
     */
    String getClientAddr() throws ServerNotConnectedException;

    /**
     * Version of {@link #getClientPort()} that NOT throws exception.
     *
     * @return current client port.
     */
    int tryClientPort();

    /**
     * @return current client port.
     * @throws ServerNotConnectedException if client is not connected to a server.
     */
    int getClientPort() throws ServerNotConnectedException;

    /**
     * The protocol name used to communicate with the server (p.e. 'http', 'ftp'...).
     * <p>
     * It is used in {@link #getServerUrl()} method.
     *
     * @return current client protocol name.
     */
    String getProtocolName();

    /**
     * @return human readable server name (always available).
     */
    String getServerName();

    /**
     * Version of {@link #getServerId()} that NOT throws exception.
     *
     * @return current connected server's id.
     */
    String tryServerId();

    /**
     * @return current connected server's id.
     * @throws ServerNotConnectedException if client is not connected to a server.
     */
    String getServerId() throws ServerNotConnectedException;

    /**
     * @return {@link #getProtocolName()} + "://" + #getServerAddr() + ":" + {@link #getServerPort()}
     */
    String getServerUrl();

    /**
     * @return #getServerAddr() + ":" + {@link #getServerPort()}
     */
    String getServerHostname();

    /**
     * @return the server address.
     */
    String getServerAddr();

    /**
     * @return the server port.
     */
    int getServerPort();

    /**
     * Version of {@link #getServerRealAddr()} that NOT throws exception.
     *
     * @return the connection's server address.current connected server's id.
     */
    String tryServerRealAddr();

    /**
     * @return the connection's server address.
     * @throws ServerNotConnectedException if client is not connected to a server.
     */
    String getServerRealAddr() throws ServerNotConnectedException;

    /**
     * Version of {@link #getServerRealPort()} that NOT throws exception.
     *
     * @return the connection's server port.
     */
    int tryServerRealPort();

    /**
     * @return the connection's server port.
     * @throws ServerNotConnectedException if client is not connected to a server.
     */
    int getServerRealPort() throws ServerNotConnectedException;


    // Client connection methods

    /**
     * Method to connect current client to the server.
     */
    void connect() throws IOException, AAAException, StateException;

    /**
     * Method to disconnect current client to the server.
     */
    void disconnect() throws StateException;


    // Connection listeners

    /**
     * Add given listener to current client events.
     *
     * @param listener the listener to add.
     */
    void addListener(ClientListener listener);

    /**
     * Remove given listener from current client events.
     *
     * @param listener the listener to remove.
     */
    void removeListener(ClientListener listener);


    // Connection listeners interfaces

    /**
     * Client events interface.
     */
    interface ClientListener {

        void onConnected(Client client);

        /**
         * This event is emitted only if exception occurs on connection timer's thread.
         */
        void onConnectionIOException(Client client, IOException ioException);

        /**
         * This event is emitted only if exception occurs on connection timer's thread.
         */
        void onConnectionAAAException(Client client, AAAException aaaException);

        void onDisconnected(Client client);

    }


    // Messages methods

    /**
     * Method to send data from the client to the server.
     */
    void sendData(byte[] data) throws ServerNotConnectedException;

    /**
     * Method to send data from the client to the server.
     */
    void sendData(String data) throws ServerNotConnectedException;

    /**
     * Return <code>true</code> if given param is a "good bye" message from the
     * server.
     */
    boolean isSrvByeMsg(byte[] data);


    // Exception

    /**
     * Exceptions thrown on client to server connection errors.
     */
    class ConnectionException extends Throwable {
        public ConnectionException(String msg) {
            super(msg);
        }
    }

    /**
     * Exceptions thrown when the client try to send/receive the data to/from
     * the server but the connection is closed.
     */
    class ServerNotConnectedException extends Throwable {
        private static final String MSG = "Server '%s' not connected.";
        private static final String MSG_E = "Server '%s' not connected because %s.";

        public ServerNotConnectedException(String clientId) {
            super(String.format(MSG, clientId));
        }

        public ServerNotConnectedException(String clientId, Throwable e) {
            super(String.format(MSG_E, clientId, e.getMessage()), e);
        }


        private Client c;

        public ServerNotConnectedException(Client c, String field) {
            super(String.format("Can't get %s field because, client '%s' not connected", field, c.getClientId()));
        }

        public Client getClient() {
            return c;
        }

    }

    /**
     * Exceptions thrown on client to server connection errors.
     */
    class AAAException extends Throwable {

        private Client c;

        public AAAException(Client c, String msg) {
            super(msg);
        }

        public Client getClient() {
            return c;
        }

    }


    // Client State enum

    /**
     * Client state representations.
     */
    enum State {

        /**
         * Client is connected to the server and it's fully operative.
         */
        CONNECTED,

        /**
         * Client is connecting to the server. This state is reached only on
         * first connection attempt. Other connection retry have others
         * CONNECTION_XYZ states depending on what was wrong on first connection
         * attempt.
         */
        CONNECTING,

        /**
         * Client is trying connect to the server but it's not reachable.
         * When this state is set, an internal timer is started to check
         * server's reachability (if implemented).
         */
        CONNECTING_WAITING_SERVER,

        /**
         * Client is disconnected to the server.
         */
        DISCONNECTED,

        /**
         * Client is disconnecting to the server.
         */
        DISCONNECTING;


        /**
         * Join all CONNECTING_ states.
         *
         * @return true if current state is a CONNECTING_ state.
         */
        public boolean isCONNECTING() {
            return this == CONNECTING
                    || this == CONNECTING_WAITING_SERVER
                    ;
        }

    }

}

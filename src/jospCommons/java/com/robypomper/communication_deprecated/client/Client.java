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

package com.robypomper.communication_deprecated.client;

import com.robypomper.communication_deprecated.peer.HeartBeatConfigs;
import com.robypomper.josp.states.StateException;

import java.io.IOException;
import java.net.NetworkInterface;
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
     * @return true only if current Client is disconnected.
     */
    boolean isDisconnected();

    /**
     * @return true only if current Client is connecting (or waiting for
     * re-connecting).
     */
    boolean isConnecting();

    /**
     * @return true only if current Client is disconnecting.
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

    /**
     * @return the Date when last heartbeat was received, null if never received
     * heartbeat.
     */
    Date getLastHeartbeat();

    /**
     * @return the Date when last heartbeat was failed, null if never failed
     * heartbeat.
     */
    Date getLastHeartbeatFailed();

    /**
     * @return the Date when last data was received, null if never received
     * data.
     */
    Date getLastDataReceived();

    /**
     * @return the Date when last data was send, null if never send
     * data.
     */
    Date getLastDataSend();


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
    Integer tryClientPort();

    /**
     * @return current client port.
     * @throws ServerNotConnectedException if client is not connected to a server.
     */
    int getClientPort() throws ServerNotConnectedException;

    /**
     * Version of {@link #getClientIntf()} that NOT throws exception.
     *
     * @return current client interface.
     */
    NetworkInterface tryClientIntf();

    /**
     * @return current client interface.
     * @throws ServerNotConnectedException if client is not connected to a server.
     */
    NetworkInterface getClientIntf() throws ServerNotConnectedException;

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
    Integer tryServerRealPort();

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


    // Standard behaviours

    /**
     * @return the HeartBeat object associated to current client.
     */
    HeartBeatConfigs getHeartBeatConfigs();


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

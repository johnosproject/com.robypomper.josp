package com.robypomper.communication.client;

import java.net.InetAddress;


/**
 * Network client interface.
 */
public interface Client {

    // Client getter

    /**
     * This method return always the server address, differently to
     * {@link #getServerInfo()} method that return null until first connection.
     *
     * @return the server address.
     */
    InetAddress getServerAddr();

    /**
     * This method return always the server port, differently to
     * {@link #getServerInfo()} method that return null until first connection.
     *
     * @return the server port.
     */
    int getServerPort();

    /**
     * Return the server's info.
     *
     * This method return null until the client connects for first time to the
     * server. Then it return current connected server's info, or, if it's
     * disconnected, latest connected server's info.
     *
     * @return server info, or null if current client was never connected to a
     * server.
     */
    ServerInfo getServerInfo();

    /**
     * This method return the client address or null if it's not connected.
     *
     * @return the client address.
     */
    InetAddress getClientAddr();

    /**
     * This method return always the client port or <code>-1</code> until first
     * connection.
     *
     * @return the client port.
     */
    int getClientPort();

    /**
     * @return the client id.
     */
    String getClientId();


    // Client connection methods

    /**
     * Return the status of the client.
     *
     * @return <code>true</code> if the client is connected, <code>false</code>
     * otherwise.
     */
    boolean isConnected();

    /**
     * Method to connect current client to the server.
     */
    void connect() throws ConnectionException;

    /**
     * Method to disconnect current client to the server.
     */
    void disconnect();


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
    }

}

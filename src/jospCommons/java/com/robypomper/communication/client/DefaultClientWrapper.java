package com.robypomper.communication.client;

import java.net.InetAddress;
import java.util.Date;

/**
 * Super class that wraps all Client methods to an internal client instance.
 * <p>
 * To set the wrapped client in a sub class, use the {@link #setWrappedClient(Client)}
 * method.
 */
public class DefaultClientWrapper implements Client {

    protected final String wrappedClientId;
    protected Client wrappedClient;
    // Other
    private Date lastConnection;
    private Date lastDisconnection;


    // Constructor

    public DefaultClientWrapper(String clientId) {
        wrappedClientId = clientId;
    }

    protected void setWrappedClient(Client wrappedClient) {
        this.wrappedClient = wrappedClient;
    }

    protected Client getWrappedClient() {
        return this.wrappedClient;
    }

    protected void resetWrappedClient() {
        lastConnection = wrappedClient.getLastConnection();
        lastDisconnection = wrappedClient.getLastDisconnection();
        this.wrappedClient = null;
    }


    // Client's wrapping methods - Client getter

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getServerAddr() {
        return wrappedClient != null ? wrappedClient.getServerAddr() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServerPort() {
        return wrappedClient != null ? wrappedClient.getServerPort() : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerInfo getServerInfo() {
        return wrappedClient != null ? wrappedClient.getServerInfo() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getClientAddr() {
        return wrappedClient != null ? wrappedClient.getClientAddr() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getClientPort() {
        return wrappedClient != null ? wrappedClient.getClientPort() : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientId() {
        return wrappedClient != null ? wrappedClient.getClientId() : wrappedClientId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastConnection() {
        return wrappedClient != null ? (wrappedClient.getLastConnection() != null ? wrappedClient.getLastConnection() : lastConnection) : lastConnection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastDisconnection() {
        return wrappedClient != null ? (wrappedClient.getLastDisconnection() != null ? wrappedClient.getLastDisconnection() : lastDisconnection) : lastDisconnection;
    }


    // Client's wrapping methods - Client connection methods

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return wrappedClient != null && wrappedClient.isConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws ConnectionException {
        wrappedClient.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        wrappedClient.disconnect();
    }


    // Client's wrapping methods - Messages methods

    /**
     * {@inheritDoc}
     * <p>
     * If client was not set, it throw a {@link com.robypomper.communication.client.Client.ServerNotConnectedException}.
     */
    @Override
    public void sendData(byte[] data) throws ServerNotConnectedException {
        if (wrappedClient == null) throw new ServerNotConnectedException(getClientId());
        wrappedClient.sendData(data);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If client was not set, it throw a {@link com.robypomper.communication.client.Client.ServerNotConnectedException}.
     */
    @Override
    public void sendData(String msg) throws ServerNotConnectedException {
        if (wrappedClient == null) throw new ServerNotConnectedException(getClientId());
        wrappedClient.sendData(msg);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If client was not set, it return false.
     */
    @Override
    public boolean isSrvByeMsg(byte[] data) {
        if (wrappedClient == null) return false;
        return wrappedClient.isSrvByeMsg(data);
    }

}

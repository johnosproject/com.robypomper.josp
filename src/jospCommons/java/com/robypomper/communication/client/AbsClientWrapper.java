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

import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.states.StateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Super class that wraps all Client methods to an internal client instance.
 * <p>
 * To set/get the wrapped client in a sub class, use the {@link #setWrappedClient(Client)}
 * or {@link #getWrappedClient()} methods.
 */
@SuppressWarnings({"unused", "StatementWithEmptyBody"})
public abstract class AbsClientWrapper implements Client {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    // Configs
    protected final String wrappedClientId;
    protected Client wrappedClient = null;
    // Last connections
    private Date lastConnection;
    private Date lastDisconnection;
    // Listeners
    private final List<ClientListener> wrapperListeners = new ArrayList<>();


    // Constructor

    /**
     * Instantiate new client wrapper with his final client id.
     *
     * @param clientId wrapper client's id.
     */
    public AbsClientWrapper(String clientId) {
        wrappedClientId = clientId;
    }


    // Wrapping methods

    /**
     * Set current wrapper's client.
     * <p>
     * All {@link Client} calls will be redirected to given param.
     * <p>
     * If given param is null, then current wrapper will be reset.
     *
     * @param wrappedClient the new wrapped client.
     */
    protected void setWrappedClient(Client wrappedClient) {
        // De-register wrapped client
        if (wrappedClient != null) {
            wrappedClient.removeListener(internalWrapperListener);
            if (isGreater(wrappedClient.getLastConnection(), lastConnection))
                lastConnection = wrappedClient.getLastConnection();
            if (isGreater(wrappedClient.getLastDisconnection(), lastDisconnection))
                lastDisconnection = wrappedClient.getLastDisconnection();
        }

        // Switch wrapped client
        this.wrappedClient = wrappedClient;

        // Register wrapped client
        if (wrappedClient != null) {
            if (isGreater(wrappedClient.getLastConnection(), lastConnection))
                lastConnection = wrappedClient.getLastConnection();
            if (isGreater(wrappedClient.getLastDisconnection(), lastDisconnection))
                lastDisconnection = wrappedClient.getLastDisconnection();
            wrappedClient.addListener(internalWrapperListener);
        }
    }

    /**
     * @return wrapped client.
     */
    protected Client getWrappedClient() {
        return this.wrappedClient;
    }

    /**
     * Reset current wrapper.
     * <p>
     * Clean wrapped client reference, de-register all listener, etc...
     */
    protected void resetWrappedClient() {
        setWrappedClient(null);
    }


    // Getter state

    /**
     * {@inheritDoc}
     * <p>
     * If no clients is wrapped, then returns
     * {@link com.robypomper.communication.client.Client.State#DISCONNECTED}.
     */
    @Override
    public State getState() {
        if (wrappedClient == null)
            return State.DISCONNECTED;
        return wrappedClient.getState();
    }

    /**
     * {@inheritDoc}
     * <p>
     * If no clients is wrapped, then returns false.
     */
    @Override
    public boolean isConnected() {
        return wrappedClient != null && wrappedClient.isConnected();
    }

    /**
     * {@inheritDoc}
     * <p>
     * If no clients is wrapped, then returns false.
     */
    @Override
    public boolean isConnecting() {
        return wrappedClient != null && wrappedClient.isConnecting();
    }

    /**
     * {@inheritDoc}
     * <p>
     * If no clients is wrapped, then returns false.
     */
    @Override
    public boolean isDisconnecting() {
        return wrappedClient == null || wrappedClient.isDisconnecting();
    }

    /**
     * {@inheritDoc}
     * <p>
     * When reset, wrapper class store lastConnection value from removing
     * client. Then, when queried, it return the greatest value between stored
     * lastConnection and (if present) wrapped client lastConnection value.
     */
    @Override
    public Date getLastConnection() {
        if (wrappedClient == null)
            return lastDisconnection;
        return isGreater(wrappedClient.getLastConnection(), lastConnection) ? wrappedClient.getLastConnection() : lastConnection;
    }

    /**
     * {@inheritDoc}
     * <p>
     * When reset, wrapper class store lastDisconnection value from removing
     * client. Then, when queried, it return the greatest value between stored
     * lastDisconnection and (if present) wrapped client lastDisconnection value.
     */
    @Override
    public Date getLastDisconnection() {
        if (wrappedClient == null)
            return lastDisconnection;
        return isGreater(wrappedClient.getLastDisconnection(), lastDisconnection) ? wrappedClient.getLastDisconnection() : lastDisconnection;
    }


    // Getter configs

    /**
     * {@inheritDoc}
     * <p>
     * Return the wrapper id, to get current wrapped client id use
     * {@link #getWrappedClientId()} or {@link #tryWrappedClientId()}.
     */
    @Override
    public String getClientId() {
        return wrappedClientId;
    }

    public String tryWrappedClientId() {
        try {
            return getWrappedClientId();
        } catch (Throwable ignore) {
            return null;
        }
    }

    public String getWrappedClientId() {
        return wrappedClient.getClientId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String tryClientHostname() {
        try {
            return getClientHostname();
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientHostname() throws WrappingException, ServerNotConnectedException {
        if (wrappedClient == null)
            throw new WrappingException(this, "ClientHostname");
        return wrappedClient.getClientHostname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String tryClientAddr() {
        try {
            return getClientAddr();
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientAddr() throws WrappingException, ServerNotConnectedException {
        if (wrappedClient == null)
            throw new WrappingException(this, "ClientAddr");
        return wrappedClient.getClientAddr();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int tryClientPort() {
        try {
            return getClientPort();
        } catch (Throwable ignore) {
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getClientPort() throws WrappingException, ServerNotConnectedException {
        if (wrappedClient == null)
            throw new WrappingException(this, "ClientPort");
        return wrappedClient.getClientPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String tryServerId() {
        try {
            return getServerId();
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerId() throws WrappingException, ServerNotConnectedException {
        if (wrappedClient == null)
            throw new WrappingException(this, "ServerId");
        return wrappedClient.getServerId();
    }

    public String tryServerUrl() {
        try {
            return getServerUrl();
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerUrl() throws WrappingException {
        if (wrappedClient == null)
            throw new WrappingException(this, "ServerUrl");
        return wrappedClient.getServerUrl();
    }

    public String tryServerHostname() {
        try {
            return getServerHostname();
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerHostname() throws WrappingException {
        if (wrappedClient == null)
            throw new WrappingException(this, "ServerHostname");
        return wrappedClient.getServerHostname();
    }

    public String tryServerAddr() {
        try {
            return getServerAddr();
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerAddr() throws WrappingException {
        if (wrappedClient == null)
            throw new WrappingException(this, "ServerAddr");
        return wrappedClient.getServerAddr();
    }

    public int tryServerPort() {
        try {
            return getServerPort();
        } catch (Throwable ignore) {
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServerPort() throws WrappingException {
        if (wrappedClient == null)
            throw new WrappingException(this, "ServerPort");
        return wrappedClient.getServerPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String tryServerRealAddr() {
        try {
            return getServerRealAddr();
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerRealAddr() throws WrappingException, ServerNotConnectedException {
        if (wrappedClient == null)
            throw new WrappingException(this, "ServerAddr");
        return wrappedClient.getServerRealAddr();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int tryServerRealPort() {
        try {
            return getServerRealPort();
        } catch (Throwable ignore) {
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServerRealPort() throws WrappingException, ServerNotConnectedException {
        if (wrappedClient == null)
            throw new WrappingException(this, "ServerPort");
        return wrappedClient.getServerRealPort();
    }


    // Client connection methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws IOException, AAAException, StateException, WrappingException {
        if (wrappedClient == null)
            throw new WrappingException(this);
        wrappedClient.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() throws StateException, WrappingException {
        if (wrappedClient == null)
            throw new WrappingException(this);
        wrappedClient.disconnect();
    }


    // Connection listeners

    /**
     * {@inheritDoc}
     * <p>
     * If client was not set, it return false.
     */
    @Override
    public void addListener(ClientListener listener) {
        wrapperListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If client was not set, it return false.
     */
    @Override
    public void removeListener(ClientListener listener) {
        wrapperListeners.remove(listener);
    }

    /**
     * Called when a connection was stabilised successfully with a server.
     */
    protected void emit_ClientConnected() {
        updateLastConnection();
        for (ClientListener l : wrapperListeners)
            l.onConnected(this);
    }

    /**
     * Called when an error occurs on opening communication channel.
     */
    protected void emit_ClientConnectionIOException(IOException ioException) {
        for (ClientListener l : wrapperListeners)
            l.onConnectionIOException(this, ioException);
    }

    /**
     * Called when an error occurs on AAA processes of connection.
     */
    protected void emit_ClientConnectionAAAException(AAAException aaaException) {
        for (ClientListener l : wrapperListeners)
            l.onConnectionAAAException(this, aaaException);
    }

    /**
     * Called when a connection was closed (because required or an erro occured).
     */
    protected void emit_ClientDisconnected() {
        updateLastDisconnection();
        for (ClientListener l : wrapperListeners)
            l.onDisconnected(this);
    }


    private final ClientListener internalWrapperListener = new ClientListener() {

        @Override
        public void onConnected(Client client) {
            emit_ClientConnected();
        }

        @Override
        public void onConnectionIOException(Client client, IOException ioException) {
            emit_ClientConnectionIOException(ioException);
        }

        @Override
        public void onConnectionAAAException(Client client, AAAException aaaException) {
            emit_ClientConnectionAAAException(aaaException);
        }

        @Override
        public void onDisconnected(Client client) {
            emit_ClientDisconnected();
        }

    };


    // Messages methods

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


    // Subclassing customization

    /**
     * Called when a connection was stabilised successfully with a server.
     */
    private void updateLastConnection() {
        lastConnection = JOSPProtocol.getNowDate();
    }

    /**
     * Called when a connection was closed (because required or because an error).
     */
    private void updateLastDisconnection() {
        lastDisconnection = JOSPProtocol.getNowDate();
    }


    // Exceptions

    /**
     * Exception throwed when try to access a wrapped client fields, but no
     * wrapped client is set.
     */
    public static class WrappingException extends RuntimeException {

        private final AbsClientWrapper w;

        public WrappingException(AbsClientWrapper w) {
            super("Can't connect because wrapped client not set.");
            this.w = w;
        }

        public WrappingException(AbsClientWrapper w, String field) {
            super(String.format("Can't get '%s' field because wrapped client not set.", field));
            this.w = w;
        }

        public AbsClientWrapper getWrapper() {
            return w;
        }

    }

    /**
     * Utils method to compare two Date.
     */
    private static boolean isGreater(Date first, Date second) {
        if (first == null)
            return false;

        if (second == null)
            return true;

        return first.compareTo(second) > 0;
    }

}

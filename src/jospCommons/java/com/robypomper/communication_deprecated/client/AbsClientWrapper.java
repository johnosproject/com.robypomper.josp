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
import com.robypomper.communication_deprecated.peer.HeartBeatWrapper;
import com.robypomper.java.JavaDate;
import com.robypomper.josp.states.StateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Date;

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
    private Date lastHeartbeat;
    private Date lastHeartbeatFailed;
    private Date lastDataReceived;
    private Date lastDataSend;
    private String lastClientAddr;
    private Integer lastClientPort;
    private NetworkInterface lastClientIntf;
    // Heartbeat
    private final HeartBeatWrapper heartBeat;


    // Constructor

    /**
     * Instantiate new client wrapper with his final client id.
     *
     * @param clientId wrapper client's id.
     */
    public AbsClientWrapper(String clientId) {
        wrappedClientId = clientId;
        heartBeat = new HeartBeatWrapper();
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
            if (isGreater(wrappedClient.getLastConnection(), lastConnection))
                lastConnection = wrappedClient.getLastConnection();
            if (isGreater(wrappedClient.getLastDisconnection(), lastDisconnection))
                lastDisconnection = wrappedClient.getLastDisconnection();
            if (isGreater(wrappedClient.getLastHeartbeat(), lastHeartbeat))
                lastHeartbeat = wrappedClient.getLastHeartbeat();
            if (isGreater(wrappedClient.getLastHeartbeatFailed(), lastHeartbeatFailed))
                lastHeartbeatFailed = wrappedClient.getLastHeartbeatFailed();
            if (isGreater(wrappedClient.getLastDataReceived(), lastDataReceived))
                lastDataReceived = wrappedClient.getLastDataReceived();
            if (isGreater(wrappedClient.getLastDataSend(), lastDataSend))
                lastDataSend = wrappedClient.getLastDataSend();
            lastClientAddr = wrappedClient.tryClientAddr();
            lastClientPort = wrappedClient.tryClientPort();
            lastClientIntf = wrappedClient.tryClientIntf();
            heartBeat.resetWrapped();
        }

        // Switch wrapped client
        this.wrappedClient = wrappedClient;

        // Register wrapped client
        if (wrappedClient != null) {
            if (isGreater(wrappedClient.getLastConnection(), lastConnection))
                lastConnection = wrappedClient.getLastConnection();
            if (isGreater(wrappedClient.getLastDisconnection(), lastDisconnection))
                lastDisconnection = wrappedClient.getLastDisconnection();
            if (isGreater(wrappedClient.getLastHeartbeat(), lastHeartbeat))
                lastHeartbeat = wrappedClient.getLastHeartbeat();
            if (isGreater(wrappedClient.getLastHeartbeatFailed(), lastHeartbeatFailed))
                lastHeartbeatFailed = wrappedClient.getLastHeartbeatFailed();
            if (isGreater(wrappedClient.getLastDataReceived(), lastDataReceived))
                lastDataReceived = wrappedClient.getLastDataReceived();
            if (isGreater(wrappedClient.getLastDataSend(), lastDataSend))
                lastDataSend = wrappedClient.getLastDataSend();
            if (wrappedClient.tryClientAddr() != null) lastClientAddr = wrappedClient.tryClientAddr();
            if (wrappedClient.tryClientPort() != null) lastClientPort = wrappedClient.tryClientPort();
            if (wrappedClient.tryClientIntf() != null) lastClientIntf = wrappedClient.tryClientIntf();
            heartBeat.setWrapped(wrappedClient.getHeartBeatConfigs());
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
     * {@link com.robypomper.communication_deprecated.client.Client.State#DISCONNECTED}.
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
     * If no clients is wrapped, then returns true.
     */
    @Override
    public boolean isDisconnected() {
        return wrappedClient == null || wrappedClient.isDisconnected();
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
            return lastConnection;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastHeartbeat() {
        if (wrappedClient == null)
            return lastHeartbeat;
        return isGreater(wrappedClient.getLastHeartbeat(), lastHeartbeat) ? wrappedClient.getLastHeartbeat() : lastHeartbeat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastHeartbeatFailed() {
        if (wrappedClient == null)
            return lastHeartbeatFailed;
        return isGreater(wrappedClient.getLastHeartbeatFailed(), lastHeartbeatFailed) ? wrappedClient.getLastHeartbeatFailed() : lastHeartbeatFailed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastDataReceived() {
        if (wrappedClient == null)
            return lastDataReceived;
        return isGreater(wrappedClient.getLastDataReceived(), lastDataReceived) ? wrappedClient.getLastDataReceived() : lastDataReceived;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastDataSend() {
        if (wrappedClient == null)
            return lastDataSend;
        return isGreater(wrappedClient.getLastDataSend(), lastDataSend) ? wrappedClient.getLastDataSend() : lastDataSend;
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
            String addr = tryClientAddr();
            Integer port = tryClientPort();
            return (addr != null && port != null) ? addr + ":" + port : null;
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
        if (wrappedClient == null)
            return lastClientAddr;
        return wrappedClient.tryClientAddr();
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
    public Integer tryClientPort() {
        if (wrappedClient == null)
            return lastClientPort;
        return wrappedClient.tryClientPort();
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
    public NetworkInterface tryClientIntf() {
        if (wrappedClient == null)
            return lastClientIntf;
        return wrappedClient.tryClientIntf();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NetworkInterface getClientIntf() throws WrappingException, ServerNotConnectedException {
        if (wrappedClient == null)
            throw new WrappingException(this, "ClientPort");
        return wrappedClient.getClientIntf();
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
    public Integer tryServerRealPort() {
        try {
            return getServerRealPort();
        } catch (Throwable ignore) {
            return null;
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


    // Messages methods

    /**
     * {@inheritDoc}
     * <p>
     * If client was not set, it throw a {@link com.robypomper.communication_deprecated.client.Client.ServerNotConnectedException}.
     */
    @Override
    public void sendData(byte[] data) throws ServerNotConnectedException {
        if (wrappedClient == null) throw new ServerNotConnectedException(getClientId());
        wrappedClient.sendData(data);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If client was not set, it throw a {@link com.robypomper.communication_deprecated.client.Client.ServerNotConnectedException}.
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
        lastConnection = JavaDate.getNowDate();
    }

    /**
     * Called when a connection was closed (because required or because an error).
     */
    private void updateLastDisconnection() {
        lastDisconnection = JavaDate.getNowDate();
    }


    // HeartBeat

    /**
     * {@inheritDoc}
     */
    @Override
    public HeartBeatConfigs getHeartBeatConfigs() {
        return heartBeat;
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

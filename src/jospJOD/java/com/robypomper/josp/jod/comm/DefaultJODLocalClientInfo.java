package com.robypomper.josp.jod.comm;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.josp.protocol.JOSPProtocol_Service;

import java.net.InetAddress;


/**
 * Default implementation of {@link JODLocalClientInfo} interface.
 */
public class DefaultJODLocalClientInfo implements JODLocalClientInfo {

    // Local vars

    private final ClientInfo client;
    private final String fullSrvId;
    private final String srvId;
    private final String usrId;
    private final String instId;


    // Constructor

    /**
     * Default constructor that split the client's id in JSL's ids (service, user
     * and instance).
     *
     * @param client the communication level's client's info.
     */
    public DefaultJODLocalClientInfo(ClientInfo client) {
        this.client = client;

        fullSrvId = client.getClientId();
        this.srvId = JOSPProtocol_Service.fullSrvIdToSrvId(fullSrvId);
        this.usrId = JOSPProtocol_Service.fullSrvIdToUsrId(fullSrvId);
        this.instId = JOSPProtocol_Service.fullSrvIdToInstId(fullSrvId);
    }


    // Service info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullSrvId() {
        return fullSrvId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSrvId() {
        return srvId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsrId() {
        return usrId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInstanceId() {
        return instId;
    }


    // Connection info

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo getClient() {
        return client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientId() {
        return client.getClientId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getClientAddress() {
        return client.getPeerAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getClientPort() {
        return client.getPeerPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientFullAddress() {
        return client.getPeerFullAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalFullAddress() {
        return client.getLocalFullAddress();
    }


    // Connection mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return client.isConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectLocal() {
        client.closeConnection();
    }

}

package com.robypomper.josp.jod.comm;

import com.robypomper.communication.server.ClientInfo;

import java.net.InetAddress;


/**
 * Default implementation of {@link JODLocalClientInfo} interface.
 */
public class DefaultJODLocalClientInfo implements JODLocalClientInfo {

    // Local vars

    private final ClientInfo client;
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

        String[] ids = client.getClientId().split("/");
        this.srvId = ids[0];
        this.usrId = ids[1];
        this.instId = ids[2];
    }


    // Service info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInstanceId() {
        return instId;
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
    public String getSrvId() {
        return srvId;
    }


    // Connection info

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

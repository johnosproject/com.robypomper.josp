package com.robypomper.josp.jod.comm;

import com.robypomper.josp.jod.systems.JODCommunication;

import java.net.InetAddress;


/**
 * Interface that represent and provide local client (JSL) info.
 */
public interface JODLocalClientInfo {

    // Service info

    /**
     * Th unique ID per instance.
     * <p>
     * This id must be unique across all other srv/usr instances. That means
     * if two differents clients from same service and user are connected, they
     * must have different instance id. To the other side if the two clients
     * are from same instance, also the instance id will be the same.
     *
     * @return the represented client's instance id.
     */
    String getInstanceId();

    /**
     * The user id of the user logged in the client's service.
     *
     * @return the represented client's user id.
     */
    String getUsrId();

    /**
     * The service id.
     *
     * @return the represented client's service id.
     */
    String getSrvId();


    // Connection info

    /**
     * The client id is generated and managed by the communication level below.
     * <p>
     * The client id is composed by the service, user and instance id.
     *
     * @return the represented client's id.
     */
    String getClientId();

    /**
     * @return the represented client's address.
     */
    InetAddress getClientAddress();

    /**
     * @return the represented client's port.
     */
    int getClientPort();

    /**
     * A string containing the local server (JOD) address and port that the
     * represented client is connected with.
     *
     * @return the string containing the local server address and port.
     */
    String getLocalFullAddress();


    // Connection mngm

    /**
     * @return <code>true</code> it current local connection is connected.
     */
    boolean isConnected();

    /**
     * Close connection and disconnect corresponding JSL service.
     */
    void disconnectLocal() throws JODCommunication.LocalCommunicationException;

}

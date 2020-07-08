package com.robypomper.communication.server.events;


import com.robypomper.communication.server.ClientInfo;

/**
 * Server tx and rx events.
 */
public interface ServerMessagingEvents extends ServerEvents {

    // Data send events

    /**
     * This method is called when data are transmitted to the client.
     *
     * @param writtenData data transmitted to the client.
     */
    void onDataSend(ClientInfo client, byte[] writtenData);

    /**
     * This method is called when data are transmitted to the client.
     *
     * @param writtenData data transmitted to the client.
     */
    void onDataSend(ClientInfo client, String writtenData);


    // Data received events

    /**
     * This method is called when data are received from the client.
     *
     * @param readData received from the client.
     */
    boolean onDataReceived(ClientInfo client, byte[] readData) throws Throwable;

    /**
     * This method is called when data are received from the client.
     *
     * @param readData received from the client.
     */
    boolean onDataReceived(ClientInfo client, String readData) throws Throwable;

}

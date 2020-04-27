package com.robypomper.communication.client.events;


/**
 * Client tx and rx events.
 */
public interface ClientMessagingEvents extends ClientEvents {

    // Data send events

    /**
     * This method is called when data are transmitted to the server.
     *
     * @param writtenData data transmitted to the server.
     */
    void onDataSend(byte[] writtenData);

    /**
     * This method is called when data are transmitted to the server.
     *
     * @param writtenData data transmitted to the server.
     */
    void onDataSend(String writtenData);


    // Data received events

    /**
     * This method is called when data are received from the server.
     *
     * @param readData received from the server.
     */
    boolean onDataReceived(byte[] readData) throws Throwable;

    /**
     * This method is called when data are received from the server.
     *
     * @param readData received from the server.
     */
    boolean onDataReceived(String readData) throws Throwable;

}

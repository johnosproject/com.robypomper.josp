package com.robypomper.communication.server.events;

import com.robypomper.communication.peer.PeerInfo;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.log.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Echo implementation of the {@link ServerMessagingEvents}.
 * <p>
 * The echo implementation simply response same data received from the client.
 */
public class EchoServerMessagingEventsListener extends DefaultServerEvent implements ServerMessagingEvents {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();


    // Data send events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Markers#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onDataSend(ClientInfo client, byte[] writtenData) {
        log.debug(Markers.COMM_SRV_IMPL, String.format("%s.onDataSend(%s, byte[] %s)", getServer().getServerId(), client.getClientId(), new String(writtenData, PeerInfo.CHARSET)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Markers#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onDataSend(ClientInfo client, String writtenData) {
        log.debug(Markers.COMM_SRV_IMPL, String.format("%s.onDataSend(%s, String %s)", getServer().getServerId(), client.getClientId(), writtenData));
    }


    // Data received events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Markers#COMM_SRV_IMPL} marker.
     */
    @Override
    public boolean onDataReceived(ClientInfo client, byte[] readData) {
        log.debug(Markers.COMM_SRV_IMPL, String.format("%s.onDataReceived(%s, byte[] %s)", getServer().getServerId(), client.getClientId(), new String(readData, PeerInfo.CHARSET)));
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Process (echo) received data and log the event.
     * <p>
     * This method receive all bytes send by the client and send them back.
     */
    @Override
    public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
        log.debug(Markers.COMM_SRV_IMPL, String.format("%s.onDataReceived(%s, String %s)", getServer().getServerId(), client.getClientId(), readData));
        getServer().sendData(client, readData);
        return true;
    }
}

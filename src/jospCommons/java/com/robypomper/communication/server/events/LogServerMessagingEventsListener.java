package com.robypomper.communication.server.events;

import com.robypomper.communication.peer.PeerInfo;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Log implementation of the {@link ServerMessagingEvents}.
 * <p>
 * The log implementations log all events with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
 */
public class LogServerMessagingEventsListener extends DefaultServerEvent implements ServerMessagingEvents {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();


    // Data send events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onDataSend(ClientInfo client, byte[] writtenData) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onDataSend(%s, byte[] %s)", getServer().getServerId(), client.getClientId(), new String(writtenData, PeerInfo.CHARSET)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onDataSend(ClientInfo client, String writtenData) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onDataSend(%s, String %s)", getServer().getServerId(), client.getClientId(), writtenData));
    }


    // Data received events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public boolean onDataReceived(ClientInfo client, byte[] readData) throws Throwable {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onDataReceived(%s, byte[] %s)", getServer().getServerId(), client.getClientId(), new String(readData, PeerInfo.CHARSET)));
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onDataReceived(%s, String %s)", getServer().getServerId(), client.getClientId(), readData));
        return true;
    }
}

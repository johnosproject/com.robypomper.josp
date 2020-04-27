package com.robypomper.communication.client.events;

import com.robypomper.communication.peer.PeerInfo;
import com.robypomper.log.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Log implementation of the {@link ClientMessagingEvents}.
 * <p>
 * The log implementations log all events with {@link Markers#COMM_CL_IMPL} marker.
 */
public class LogClientMessagingEventsListener extends DefaultClientEvents implements ClientMessagingEvents {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();


    // Data send events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSend(byte[] writtenData) {
        log.info(Markers.COMM_CL_IMPL, String.format("%s.onDataSend( byte[] %s)", getClient().getClientId(), new String(writtenData, PeerInfo.CHARSET)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSend(String writtenData) {
        log.info(Markers.COMM_CL_IMPL, String.format("%s.onDataSend(byte[] %s)", getClient().getClientId(), writtenData));
    }


    // Data received events

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onDataReceived(byte[] readData) throws Throwable {
        log.info(Markers.COMM_CL_IMPL, String.format("%s.onDataReceived(byte[] %s)", getClient().getClientId(), new String(readData, PeerInfo.CHARSET)));
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onDataReceived(String readData) throws Throwable {
        log.info(Markers.COMM_CL_IMPL, String.format("%s.onDataReceived(String %s)", getClient().getClientId(), readData));
        return true;
    }

}

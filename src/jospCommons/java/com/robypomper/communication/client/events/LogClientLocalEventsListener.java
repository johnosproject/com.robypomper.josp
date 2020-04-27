package com.robypomper.communication.client.events;

import com.robypomper.log.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Log implementation of the {@link ClientLocalEvents}.
 * <p>
 * The log implementations log all events with {@link Markers#COMM_CL_IMPL} marker.
 */
public class LogClientLocalEventsListener extends DefaultClientEvents implements ClientLocalEvents {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();


    // Client connect events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnected() {
        log.info(Markers.COMM_CL_IMPL, String.format("%s.onConnected()", getClient().getClientId()));
    }


    // Client disconnect events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisconnected() {
        log.info(Markers.COMM_CL_IMPL, String.format("%s.onDisconnected()", getClient().getClientId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisconnectionError(Exception e) {
        log.info(Markers.COMM_CL_IMPL, String.format("%s.onDisconnectionError([%s] %s)", getClient().getClientId(), e.getClass(), e.getMessage()));
    }

}

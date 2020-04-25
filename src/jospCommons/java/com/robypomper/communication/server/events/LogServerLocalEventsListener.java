package com.robypomper.communication.server.events;

import com.robypomper.log.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Log implementation of the {@link ServerLocalEvents}.
 * <p>
 * The log implementations log all events with {@link Markers#COMM_SRV_IMPL} marker.
 */
public class LogServerLocalEventsListener extends DefaultServerEvent implements ServerLocalEvents {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();


    // Server start events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Markers#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onStarted() {
        log.info(Markers.COMM_SRV_IMPL, String.format("%s.onStarted()", getServer().getServerId()));
    }


    // Server stop events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Markers#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onStopped() {
        log.info(Markers.COMM_SRV_IMPL, String.format("%s.onStopped()", getServer().getServerId()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Markers#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onStopError(Exception e) {
        log.info(Markers.COMM_SRV_IMPL, String.format("%s.onStopError(%s)", getServer().getServerId(), e.getMessage()));
    }


    // Server error events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Markers#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onServerError(Exception e) {
        log.info(Markers.COMM_SRV_IMPL, String.format("%s.onServerError(%s)", getServer().getServerId(), e.getMessage()));
    }

}

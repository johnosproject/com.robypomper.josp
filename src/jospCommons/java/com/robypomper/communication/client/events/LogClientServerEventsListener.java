package com.robypomper.communication.client.events;

import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Log implementation of the {@link ClientServerEvents}.
 * <p>
 * The log implementations log all events with {@link Mrk_Commons#COMM_CL_IMPL} marker.
 */
public class LogClientServerEventsListener extends DefaultClientEvents implements ClientServerEvents {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();


    // Client connection events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerConnection() {
        log.info(Mrk_Commons.COMM_CL_IMPL, String.format("%s.onServerConnection()", getClient().getClientId()));
    }


    // Server disconnection events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerDisconnection() {
        log.info(Mrk_Commons.COMM_CL_IMPL, String.format("%s.onServerDisconnection()", getClient().getClientId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerClientDisconnected() {
        log.info(Mrk_Commons.COMM_CL_IMPL, String.format("%s.onServerClientDisconnected()", getClient().getClientId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerGoodbye() {
        log.info(Mrk_Commons.COMM_CL_IMPL, String.format("%s.onServerGoodbye()", getClient().getClientId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerTerminated() {
        log.info(Mrk_Commons.COMM_CL_IMPL, String.format("%s.onServerTerminated()", getClient().getClientId()));
    }


    // Server errors events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerError(Throwable e) {
        log.info(Mrk_Commons.COMM_CL_IMPL, String.format("%s.onClientError(%s)", getClient().getClientId(), e.getMessage()));
    }

}

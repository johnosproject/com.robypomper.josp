package com.robypomper.josp.jod.history;

import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODStateUpdate;

import java.io.IOException;

public interface JODHistory {


    // Register new status

    void register(JODComponent comp, JODStateUpdate update);


    // Mngm methods

    /**
     * Start syncing events to the cloud.
     * <p>
     * When started, Events system uploads all buffered events to the cloud,
     * then each time a new event is registered it's also immediately sync to
     * the cloud.
     * <p>
     * Until it's stopped.
     * <p>
     * If the cloud is not available, then Events system register to the
     * {@link com.robypomper.josp.core.jcpclient.JCPClient2} connection
     * listener. When the connection become available, it uploads all buffered
     * events to the cloud.
     */
    void startCloudSync();

    /**
     * Stop syncing events to the cloud.
     * <p>
     * When stopped, the Events system stop to sync registered events to the
     * cloud. It store latest sync event id for next {@link #startCloudSync()}
     * call.
     */
    void stopCloudSync();

    /**
     * Store all events on file and empty the buffer.
     */
    void storeCache() throws IOException;

}

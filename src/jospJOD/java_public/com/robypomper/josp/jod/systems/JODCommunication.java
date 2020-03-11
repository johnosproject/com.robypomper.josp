package com.robypomper.josp.jod.systems;

import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;

/**
 * Interface for Object's communication system.
 *
 * The communication system provide channels for local and cloud communication.
 *
 * The local communication include a tcp server, his publication on the ZeroConf
 * protocol and local client management and security.
 *
 * Cloud communication provide a channel to remote service via the JCP. When
 * activate, it start a tcp/ws socket to the JOSP GW on the JCP cloud. Then
 * the JOSP GW act as gateway from current object and remote services. Even
 * if the JOSP GW check service/user permissions, current object perform security
 * checks.
 */
public interface JODCommunication {

    // JOD Component's interaction methods (from structure)

    /**
     * Send <code>component</code> <code>update</code> to connected and allowed
     * services.
     *
     * This method is required by {@link JODState} when receive an update from
     * his {@link com.robypomper.josp.jod.executor.JODPuller} or
     * {@link com.robypomper.josp.jod.executor.JODListener} object.
     *
     * @param component the object's component that updated his state.
     * @param update the status update info.
     */
    void sendUpdate(JODState component, JODStateUpdate update);


    // Mngm methods

    /**
     * Start local Object's server and publish it.
     */
    void startLocalComm();

    /**
     * Stop local Object's server and de-publish it, then close all opened connections.
     */
    void stopLocalComm();

    /**
     * Start JOSP Gw O2S Client.
     */
    void startCloudComm();

    /**
     * Stop JOSP Gw O2S Client.
     */
    void stopCloudComm();


    /**
     * Set the {@link JODStructure} reference to the current object.
     *
     * This cross-system reference is required by the Action Execution Flow.
     *
     * @param structure the {@link JODStructure} reference.
     */
    void setStructure(JODStructure structure);
}

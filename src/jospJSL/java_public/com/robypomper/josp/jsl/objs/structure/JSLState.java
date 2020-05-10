package com.robypomper.josp.jsl.objs.structure;

import com.robypomper.josp.protocol.JOSPProtocol;

/**
 * State component representation.
 * <p>
 * State component receive status changes from correlated object and emit
 * corresponding events.
 */
public interface JSLState extends JSLComponent {

    // Status upd flow (struct)

    /**
     * Called by {@link com.robypomper.josp.jsl.objs.JSLRemoteObject} that own
     * current state, this method process the update and then trigger the update
     * event to all registered listeners.
     *
     * @param statusUpd the status to process.
     */
    boolean updateStatus(JOSPProtocol.StatusUpd statusUpd);

}

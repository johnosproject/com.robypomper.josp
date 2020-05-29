package com.robypomper.josp.jcp.apis.params.jospgws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Messaging class to transmit JSL service access info to required JOSP GW S2O.
 */
public class S2OAccessRequest {

    // Params

    public final String instanceId;
    public final byte[] srvCertificate;


    // Constructor

    @JsonCreator
    public S2OAccessRequest(@JsonProperty("instanceId") String instanceId,
                            @JsonProperty("srvCertificate") byte[] srvCertificate) {
        this.instanceId = instanceId;
        this.srvCertificate = srvCertificate;
    }
}

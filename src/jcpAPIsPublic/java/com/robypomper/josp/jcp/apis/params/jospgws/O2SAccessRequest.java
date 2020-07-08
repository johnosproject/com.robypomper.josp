package com.robypomper.josp.jcp.apis.params.jospgws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Messaging class to transmit JOSP GW S2O access info to requiring JSL services.
 */
public class O2SAccessRequest {

    // Params

    public final String instanceId;
    public final byte[] objCertificate;


    // Constructor

    @JsonCreator
    public O2SAccessRequest(@JsonProperty("instanceId") String instanceId,
                            @JsonProperty("objCertificate") byte[] objCertificate) {
        this.instanceId = instanceId;
        this.objCertificate = objCertificate;
    }
}

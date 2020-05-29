package com.robypomper.josp.jcp.apis.params.jospgws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.InetAddress;


/**
 * Messaging class to transmit JOD objects access info to required JOSP GW O2S.
 */
public class S2OAccessInfo {

    // Params

    public final InetAddress gwAddress;

    public final int gwPort;

    public final byte[] gwCertificate;


    // Constructor

    @JsonCreator
    public S2OAccessInfo(@JsonProperty("gwAddress") InetAddress gwAddress,
                         @JsonProperty("gwPort") int gwPort,
                         @JsonProperty("gwCertificate") byte[] gwCertificate) {
        this.gwAddress = gwAddress;
        this.gwPort = gwPort;
        this.gwCertificate = gwCertificate;
    }

}

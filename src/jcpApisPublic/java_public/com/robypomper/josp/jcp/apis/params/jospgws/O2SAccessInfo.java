package com.robypomper.josp.jcp.apis.params.jospgws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.InetAddress;


/**
 * Messaging class to transmit JOSP GW O2S access info to requiring JOD objects.
 */
public class O2SAccessInfo {

    // Params

    public final InetAddress gwAddress;

    public final int gwPort;

    public final byte[] gwCertificate;


    // Constructor

    @JsonCreator
    public O2SAccessInfo(@JsonProperty("gwAddress") InetAddress gwAddress,
                         @JsonProperty("gwPort") int gwPort,
                         @JsonProperty("gwCertificate") byte[] gwCertificate) {
        this.gwAddress = gwAddress;
        this.gwPort = gwPort;
        this.gwCertificate = gwCertificate;
    }

}

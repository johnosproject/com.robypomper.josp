package com.robypomper.josp.jcp.apis.params.srvs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Messaging class to transmit user's username and id.
 */
@Data
public class SrvName {

    // Params

    public final String srvId;

    public final String srvName;


    // Constructor

    @JsonCreator
    public SrvName(@JsonProperty("srvId") String srvId,
                   @JsonProperty("srvName") String srvName) {
        this.srvId = srvId;
        this.srvName = srvName;
    }

}
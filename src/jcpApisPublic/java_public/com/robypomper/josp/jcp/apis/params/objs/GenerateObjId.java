package com.robypomper.josp.jcp.apis.params.objs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


/**
 * Messaging class to transmit object's hardware id and owner id.
 */
@Data
public class GenerateObjId {

    // Params

    public final String objIdHw;

    public final String ownerId;


    // Constructor

    @JsonCreator
    public GenerateObjId(@JsonProperty("objIdHw") String objIdHw,
                         @JsonProperty("usrId") String ownerId) {
        this.objIdHw = objIdHw;
        this.ownerId = ownerId;
    }

}
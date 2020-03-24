package com.robypomper.josp.jcp.apis.params.usrs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Messaging class to transmit user's username and id.
 */
@Data
public class UsrName {

    // Params

    public final String usrId;

    public final String username;


    // Constructor

    @JsonCreator
    public UsrName(@JsonProperty("usrId") String usrId,
                   @JsonProperty("username") String username) {
        this.usrId = usrId;
        this.username = username;
    }

}
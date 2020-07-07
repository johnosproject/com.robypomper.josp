package com.robypomper.josp.jcp.apis.params.objs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


/**
 * Messaging class to transmit object's id, name and details.
 */
@Data
public class RegisterObj {

    // Params

    private final String name;

    private final String version;

    private final String ownerId;

    private String model;

    private String brand;

    private String longDescr;

    private String structure;


    // Constructor

    @JsonCreator
    public RegisterObj(@JsonProperty("name") String name,
                       @JsonProperty("version") String version,
                       @JsonProperty("ownerId") String ownerId) {
        this.name = name;
        this.version = version;
        this.ownerId = ownerId;
    }

}
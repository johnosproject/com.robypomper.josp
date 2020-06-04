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

    public final String name;

    public final String ownerId;

    public String model;

    public String brand;

    public String longDescr;

    public final String structure;


    // Constructor

    @JsonCreator
    public RegisterObj(@JsonProperty("name") String name,
                       @JsonProperty("ownerId") String ownerId,
                       @JsonProperty("structure") String structure) {
        this.name = name;
        this.ownerId = ownerId;
        this.structure = structure;
    }

}
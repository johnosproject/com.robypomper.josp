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

    public String model;

    public String brand;

    public String longDescr;

    public final String structure;


    // Constructor

    @JsonCreator
    public RegisterObj(@JsonProperty("objIdHw") String name,
                       @JsonProperty("structure") String structure) {
        this.name = name;
        this.structure = structure;
    }

}
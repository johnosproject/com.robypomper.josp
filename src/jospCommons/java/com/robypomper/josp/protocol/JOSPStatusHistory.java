package com.robypomper.josp.protocol;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Messaging types to use in Events messaging classes.
 */
public class JOSPStatusHistory {

    public final long id;
    public final String compPath;
    public final String compType;
    public final Date updatedAt;
    public final String payload;

    @JsonCreator
    public JOSPStatusHistory(@JsonProperty("id") long id,
                             @JsonProperty("compPath") String compPath,
                             @JsonProperty("compType") String compType,
                             @JsonProperty("updatedAt") Date updatedAt,
                             @JsonProperty("payload") String payload) {
        this.id = id;
        this.compPath = compPath;
        this.compType = compType;
        this.updatedAt = updatedAt;
        this.payload = payload;
    }

}

package com.robypomper.josp.jcp.apis.params.permissions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;


/**
 * Messaging class to transmit permission details.
 */
public class ObjPermission {

    // Params

    public Long id;

    public final String objId;

    public final String usrId;

    public final String srvId;

    public final PermissionsTypes.Connection connection;

    public final PermissionsTypes.Type type;

    public final Date updatedAt;


    // Constructor

    @JsonCreator
    public ObjPermission(@JsonProperty("objId") String objId,
                         @JsonProperty("usrId") String usrId,
                         @JsonProperty("srvId") String srvId,
                         @JsonProperty("connection") PermissionsTypes.Connection connection,
                         @JsonProperty("type") PermissionsTypes.Type type,
                         @JsonProperty("updatedAt") Date updatedAt) {
        this.objId = objId;
        this.usrId = usrId;
        this.srvId = srvId;
        this.connection = connection;
        this.type = type;
        this.updatedAt = updatedAt;
    }

}

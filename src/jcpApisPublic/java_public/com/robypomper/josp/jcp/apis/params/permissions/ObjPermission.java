package com.robypomper.josp.jcp.apis.params.permissions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.robypomper.josp.protocol.JOSPPermissions;

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

    public final JOSPPermissions.Connection connection;

    public final JOSPPermissions.Type type;

    public final Date updatedAt;


    // Constructor

    @JsonCreator
    public ObjPermission(@JsonProperty("objId") String objId,
                         @JsonProperty("usrId") String usrId,
                         @JsonProperty("srvId") String srvId,
                         @JsonProperty("connection") JOSPPermissions.Connection connection,
                         @JsonProperty("type") JOSPPermissions.Type type,
                         @JsonProperty("updatedAt") Date updatedAt) {
        this.objId = objId;
        this.usrId = usrId;
        this.srvId = srvId;
        this.connection = connection;
        this.type = type;
        this.updatedAt = updatedAt;
    }

}

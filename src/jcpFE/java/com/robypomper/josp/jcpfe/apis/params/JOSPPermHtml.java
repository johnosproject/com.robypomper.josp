package com.robypomper.josp.jcpfe.apis.params;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEPermissions;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.Date;

@JsonAutoDetect
public class JOSPPermHtml {

    public final String id;
    public final String objId;
    public final String srvId;
    public final String usrId;
    public final JOSPPerm.Type type;
    public final JOSPPerm.Connection connection;
    public final Date lastUpdate;
    public final String pathUpd;
    public final String pathDel;
    public final String pathDup;

    public JOSPPermHtml(JOSPPerm perm) {
        this.id = perm.getId();
        this.objId = perm.getObjId();
        this.srvId = perm.getSrvId();
        this.usrId = perm.getUsrId();
        this.type = perm.getPermType();
        this.connection = perm.getConnType();
        this.lastUpdate = perm.getUpdatedAt();
        this.pathUpd = APIJCPFEPermissions.FULL_PATH_UPD.replace("{obj_id}", objId).replace("{perm_id}", id);
        this.pathDel = APIJCPFEPermissions.FULL_PATH_DEL.replace("{obj_id}", objId).replace("{perm_id}", id);
        this.pathDup = APIJCPFEPermissions.FULL_PATH_DUP.replace("{obj_id}", objId).replace("{perm_id}", id);
    }

}

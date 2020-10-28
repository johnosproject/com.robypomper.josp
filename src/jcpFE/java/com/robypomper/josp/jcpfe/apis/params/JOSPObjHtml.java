package com.robypomper.josp.jcpfe.apis.params;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEObjs;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEPermissions;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEStructure;
import com.robypomper.josp.jcpfe.jsl.JSLSpringService;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;

@JsonAutoDetect
public class JOSPObjHtml {

    public final String id;
    public final String name;
    public final String owner;
    public final boolean isConnected;
    public final boolean isCloudConnected;
    public final boolean isLocalConnected;
    public final String jodVersion;
    public final String pathSingle;
    public final String pathStruct;
    public final String pathPerms;
    public final String pathPermsAdd;
    public final String pathSetOwner;
    public final String pathSetName;
    public final String permission;

    public JOSPObjHtml(JSLRemoteObject obj) {
        this.id = obj.getId();
        this.name = obj.getName();
        this.owner = obj.getInfo().getOwnerId();
        this.isConnected = obj.getComm().isConnected();
        this.isCloudConnected = obj.getComm().isCloudConnected();
        this.isLocalConnected = obj.getComm().isLocalConnected();
        this.jodVersion = obj.getInfo().getJODVersion();
        this.pathSingle = APIJCPFEObjs.FULL_PATH_DETAILS.replace("{obj_id}", id);
        this.pathStruct = APIJCPFEStructure.FULL_PATH_STRUCT.replace("{obj_id}", id);
        this.pathPerms = APIJCPFEPermissions.FULL_PATH_LIST.replace("{obj_id}", id);
        this.pathPermsAdd = APIJCPFEPermissions.FULL_PATH_ADD.replace("{obj_id}", id);
        this.pathSetOwner = APIJCPFEObjs.FULL_PATH_OWNER.replace("{obj_id}", id);
        this.pathSetName = APIJCPFEObjs.FULL_PATH_NAME.replace("{obj_id}", id);
        this.permission = JSLSpringService.getObjPerm(obj).toString();
    }

}

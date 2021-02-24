package com.robypomper.josp.jcp.gws.db;

import com.robypomper.java.JavaAssertions;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.db.apis.entities.Object;
import com.robypomper.josp.jcp.db.apis.entities.ObjectStatus;
import com.robypomper.josp.jcp.db.apis.entities.Permission;
import com.robypomper.josp.jcp.gws.broker.BrokerClientObjDB;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;

import java.util.List;

public class ObjDB implements BrokerClientObjDB {

    // Internal vars

    private Object objDBInternal;
    private final PermissionsDBService permissionsDBService;
    private boolean isPaused = true;


    // Constructors

    public ObjDB(Object obj, PermissionsDBService permissionsDBService) {
        this.objDBInternal = obj;
        this.permissionsDBService = permissionsDBService;
    }


    // Getters

    @Override
    public String getId() {
        return objDBInternal.getObjId();
    }


    // Getters object

    @Override
    public String getOwner() {
        return objDBInternal.getOwner().getOwnerId();
    }

    @Override
    public ObjDB getObjDB() {
        return this;
    }

    public void setObjDB(Object obj) {
        objDBInternal = obj;
    }

    public ObjectStatus getStatus() {
        return objDBInternal.getStatus();
    }


    // Getters messages

    @Override
    public String getMsgOBJ_INFO() {
        return JOSPProtocol_ObjectToService.createObjectInfoMsg(getId(), objDBInternal.getName(), objDBInternal.getVersion(), getOwner(),
                objDBInternal.getInfo().getModel(), objDBInternal.getInfo().getBrand(), objDBInternal.getInfo().getLongDescr(),
                false);
    }

    @Override
    public String getMsgOBJ_STRUCT() {
        String structStr = objDBInternal.getStatus().getStructure();
        return JOSPProtocol_ObjectToService.createObjectStructMsg(getId(), structStr);
    }

    @Override
    public String getMsgOBJ_PERM() {
        List<Permission> permsDB = permissionsDBService.findByObj(getId());
        List<JOSPPerm> permsJOSP = PermissionsDBService.dbPermsToJOSPPerms(permsDB);
        String permJOSPStr = JOSPPerm.toString(permsJOSP);
        return JOSPProtocol_ObjectToService.createObjectPermsMsg(getId(), permJOSPStr);
    }

    @Override
    public String getMsgOBJ_DISCONNECTED() {
        return JOSPProtocol_ObjectToService.createObjectDisconnectMsg(getId());
    }


    // Messages methods

    @Override
    public void send(String data) {
        if (!isPaused)
            JavaAssertions.makeWarning_Failed("ObjDB implementation can NOT send data to object, because it's a virtual representation.");
        else
            JavaAssertions.makeAssertion_Failed("ObjDB implementation can NOT send data to object, because it's a virtual representation.\nIt's in pause state and should not send any message");
    }


    // Pause/Resume methods

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void resume() {
        isPaused = false;
    }

}

package com.robypomper.josp.jcp.gws.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.java.JavaDate;
import com.robypomper.java.JavaStructures.Pair;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.db.apis.entities.Object;
import com.robypomper.josp.jcp.db.apis.entities.*;
import com.robypomper.josp.jcp.gws.broker.GWBroker;
import com.robypomper.josp.jcp.gws.exceptions.JODObjectIdNotEqualException;
import com.robypomper.josp.jcp.gws.exceptions.JODObjectNotInDBException;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GWClientJOD extends GWClientAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWClientJOD.class);
    private final ObjectDBService objectDBService;
    private final PermissionsDBService permissionsDBService;
    private boolean isRegistered = false;
    private String msgOBJ_INFO;
    private String msgOBJ_PERM;


    // Constructors

    public GWClientJOD(ServerClient client, GWBroker gwBroker, ObjectDBService objectDBService, PermissionsDBService permissionsDBService) {
        super(client, gwBroker);
        this.objectDBService = objectDBService;
        this.permissionsDBService = permissionsDBService;
    }


    // Getters

    public String getOwner() throws JODObjectNotInDBException {
        Object objDB = getObjDB(getId());
        return objDB.getOwner().getOwnerId();
    }


    // Connection mngm

    private void onRegisteredToBroker() {
        synchronized (this) {
            Object objDB;
            try {
                objDB = getObjDB(getId());
            } catch (JODObjectNotInDBException e) {
                log.warn(String.format("Error set object '%s' online because object not stored in DB", getId()));
                try {
                    forceDisconnection();
                } catch (PeerDisconnectionException peerDisconnectionException) {
                    log.warn(String.format("Error on Object '%s' forced disconnection", getId()));
                }
                return;
            }

            objDB.getStatus().setOnline(true);
            objDB.getStatus().setLastConnectionAt(JavaDate.getNowDate());

            saveObjDBStatus(objDB);
        }
    }

    @Override
    protected void onDisconnected() {
        getBroker().deregisterObject(this);
        isRegistered = false;

        synchronized (this) {
            Object objDB;
            try {
                objDB = getObjDB(getId());
            } catch (JODObjectNotInDBException e) {
                log.warn(String.format("Error set object '%s' offline because object not stored in DB", getId()));
                return;
            }

            objDB.getStatus().setOnline(false);
            objDB.getStatus().setLastDisconnectionAt(JavaDate.getNowDate());

            saveObjDBStatus(objDB);
        }
    }


    // GWServer client's Messages methods

    public boolean processFromObjectMsg(String data) {
        String msgType = "UNKNOWN";

        try {
            if (JOSPProtocol_ObjectToService.isObjectInfoMsg(data)) {
                msgType = JOSPProtocol_ObjectToService.OBJ_INF_REQ_NAME;
                processObjectInfoMsg(data);
            } else if (JOSPProtocol_ObjectToService.isObjectStructMsg(data)) {
                msgType = JOSPProtocol_ObjectToService.OBJ_STRUCT_REQ_NAME;
                processObjectStructMsg(data);
            } else if (JOSPProtocol_ObjectToService.isObjectPermsMsg(data)) {
                msgType = JOSPProtocol_ObjectToService.OBJ_PERMS_REQ_NAME;
                processObjectPermsMsg(data);
            } else if (JOSPProtocol_ObjectToService.isObjectStateUpdMsg(data)) {
                msgType = JOSPProtocol_ObjectToService.UPD_MSG_NAME;
                processUpdateMsg(data);
            } else {
                log.warn(String.format("Error unrecognized data from object '%s'", getId()));
            }

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(String.format("Error on parsing data '%s' from object '%s'", msgType, getId()), e);

        } catch (JODObjectIdNotEqualException e) {
            log.warn(String.format("Error on processing data '%s' from object '%s' because data contain wrong object id '%s'", msgType, getId(), e.getMsgObjId()), e);

        } catch (JODObjectNotInDBException e) {
            log.warn(String.format("Error on processing data '%s' from object '%s' because object not stored in DB", msgType, getId()), e);

        } catch (Throwable e) {
            log.warn(String.format("Error on processing data '%s' from object '%s'", msgType, getId()), e);
        }
        return true;
    }

    private void processObjectInfoMsg(String data) throws JOSPProtocol.ParsingException, JODObjectIdNotEqualException {
        msgOBJ_INFO = data;

        String objId = JOSPProtocol_ObjectToService.getObjId(data);
        if (!objId.equals(getId()))
            throw new JODObjectIdNotEqualException(objId, getId());
        String name = JOSPProtocol_ObjectToService.getObjectInfoMsg_Name(data);
        String ownerId = JOSPProtocol_ObjectToService.getObjectInfoMsg_OwnerId(data);
        String jodVers = JOSPProtocol_ObjectToService.getObjectInfoMsg_JODVersion(data);
        String model = JOSPProtocol_ObjectToService.getObjectInfoMsg_Model(data);
        String brand = JOSPProtocol_ObjectToService.getObjectInfoMsg_Brand(data);
        String longDescr = JOSPProtocol_ObjectToService.getObjectInfoMsg_LongDescr(data);

        updateInfo(objId, name, ownerId, jodVers, model, brand, longDescr);

        if (!isRegistered) {
            getBroker().registerObject(this);
            onRegisteredToBroker();
            isRegistered = true;
        }

        getBroker().send(this, data, JOSPPerm.Type.Status);
    }

    private void processObjectStructMsg(String data) throws JOSPProtocol.ParsingException, JODObjectIdNotEqualException, JODObjectNotInDBException {
        String objId = JOSPProtocol_ObjectToService.getObjId(data);
        if (!objId.equals(getId()))
            throw new JODObjectIdNotEqualException(objId, getId());
        String struct = JOSPProtocol_ObjectToService.getObjectStructMsg_Struct(data);

        updateStructure(objId, struct);

        getBroker().send(this, data, JOSPPerm.Type.Status);
    }

    private void processObjectPermsMsg(String data) throws JOSPProtocol.ParsingException, JODObjectIdNotEqualException {
        msgOBJ_PERM = data;

        String objId = JOSPProtocol_ObjectToService.getObjId(data);
        if (!objId.equals(getId()))
            throw new JODObjectIdNotEqualException(objId, getId());
        List<JOSPPerm> perms = JOSPProtocol_ObjectToService.getObjectPermsMsg_Perms(data);

        Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> oldAllowedServices = getBroker().getAllowedServices(this);

        updatePerms(objId, perms);

        Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> newAllowedServices = getBroker().getAllowedServices(this);

        // added (send presentations)
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> newService : newAllowedServices.entrySet())
            if (!oldAllowedServices.containsKey(newService.getKey())) {
                try {
                    getBroker().send(this, newService.getKey(), getMsgOBJ_INFO(), JOSPPerm.Type.Status);
                    getBroker().send(this, newService.getKey(), getMsgOBJ_STRUCT(), JOSPPerm.Type.Status);

                } catch (JODObjectNotInDBException e) {
                    log.warn(String.format("Error sending Object '%s' presentation messages to Service '%s' because object not store in DB", getId(), newService.getKey()));
                }
            }

        getBroker().send(this, data, JOSPPerm.Type.CoOwner);

        // added
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> newService : newAllowedServices.entrySet())
            if (!oldAllowedServices.containsKey(newService.getKey())) {
                String srvPermMsg = JOSPProtocol_ObjectToService.createServicePermMsg(objId, newService.getValue().getFirst(), newService.getValue().getSecond());
                getBroker().send(this, newService.getKey(), srvPermMsg, JOSPPerm.Type.Status);
            }

        // to update
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> newService : newAllowedServices.entrySet())
            if (oldAllowedServices.containsKey(newService.getKey())
                    && !oldAllowedServices.get(newService.getKey()).equals(newService.getValue())) {
                String srvPermMsg = JOSPProtocol_ObjectToService.createServicePermMsg(objId, newService.getValue().getFirst(), newService.getValue().getSecond());
                getBroker().send(this, newService.getKey(), srvPermMsg, JOSPPerm.Type.Status);
            }

        // to remove
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> oldService : oldAllowedServices.entrySet())
            if (!newAllowedServices.containsKey(oldService.getKey())) {
                String srvPermMsg = JOSPProtocol_ObjectToService.createServicePermMsg(objId, JOSPPerm.Type.None, oldService.getValue().getSecond());
                getBroker().send(this, oldService.getKey(), srvPermMsg, JOSPPerm.Type.None);
            }
    }

    private void processUpdateMsg(String data) throws JOSPProtocol.ParsingException, JODObjectIdNotEqualException, JODObjectNotInDBException {
        String objId = JOSPProtocol_ObjectToService.getObjId(data);
        if (!objId.equals(getId()))
            throw new JODObjectIdNotEqualException(objId, getId());
        JOSPProtocol.StatusUpd upd = JOSPProtocol.extractStatusUpdFromMsg(data);

        updateStructureStatus(objId, upd);
        getBroker().send(this, data, JOSPPerm.Type.Status);
    }


    // Update methods

    private void updateInfo(String objId, String name, String ownerId, String jodVers, String model, String brand, String longDescr) {
        synchronized (this) {
            Object objDB = getOrCreateObjDB(objId);

            objDB.getOwner().setObjId(objId);
            objDB.getOwner().setOwnerId(ownerId);

            objDB.setObjId(objId);
            objDB.setName(name);
            objDB.setActive(true);
            objDB.setVersion(jodVers);

            objDB.getInfo().setObjId(objId);
            objDB.getInfo().setModel(model);
            objDB.getInfo().setBrand(brand);
            objDB.getInfo().setLongDescr(longDescr);

            objDB.getStatus().setObjId(objId);
            objDB.getStatus().setOnline(true);
            objDB.getStatus().setLastConnectionAt(JavaDate.getNowDate());

            saveObjDB(objDB);
        }
    }

    private void updateStructure(String objId, String struct) throws JODObjectNotInDBException {
        synchronized (this) {
            Object objDB = getObjDB(objId);

            if (objDB.getStatus().getStructure() != null
                    && objDB.getStatus().getStructure().equals(struct))
                return;

            objDB.getStatus().setStructure(struct);
            objDB.getStatus().setLastStructUpdateAt(JavaDate.getNowDate());

            saveObjDBStatus(objDB);
        }
    }

    private void updatePerms(String objId, List<JOSPPerm> perms) {
        List<Permission> oldPermissions = permissionsDBService.findByObj(objId);
        List<Permission> newPermissions = updatePerms_jospPermsToDBPerms(perms);

        synchronized (permissionsDBService) {       // this should be sync on all access to permissionSBServices also from GWBroker class
            permissionsDBService.removeAll(oldPermissions);
            permissionsDBService.addAll(newPermissions);
        }
    }

    private List<Permission> updatePerms_jospPermsToDBPerms(List<JOSPPerm> permsJOSP) {
        List<Permission> permsDB = new ArrayList<>();
        for (JOSPPerm p : permsJOSP) {
            Permission perm = new Permission();
            perm.setId(p.getId());
            perm.setObjId(p.getObjId());
            perm.setSrvId(p.getSrvId());
            perm.setUsrId(p.getUsrId());
            perm.setType(p.getPermType());
            perm.setConnection(p.getConnType());
            perm.setPermissionUpdatedAt(p.getUpdatedAt());
            permsDB.add(perm);
        }
        return permsDB;
    }

    private void updateStructureStatus(String objId, JOSPProtocol.StatusUpd state) throws JODObjectNotInDBException {
        String errUpdatingMsg = String.format("Error updating object '%s' status on DB because ", objId);
        ObjectMapper mapper = new ObjectMapper();

        synchronized (this) {
            Object objDB = getObjDB(objId);

            String stateStr = updateStructureStatus_extractStateValue(state);
            if (stateStr == null) {
                log.warn(errUpdatingMsg + "unknown state type.");
                return;
            }

            String struct = objDB.getStatus().getStructure();
            if (struct == null) {
                log.warn(errUpdatingMsg + "object's structure not set.");
                return;
            }

            Map<String, java.lang.Object> structMap;
            try {
                structMap = mapper.readValue(struct, Map.class);
            } catch (JsonProcessingException e) {
                log.warn(errUpdatingMsg + "can't parse structure stored on DB.", e);
                return;
            }

            ArrayList<Map<String, java.lang.Object>> allComps;
            if (!(structMap.get("components") instanceof ArrayList)) {
                log.warn(errUpdatingMsg + "can't parse structure stored on DB ('components' field not found or is not ArrayList).");
                return;
            }
            allComps = (ArrayList<Map<String, java.lang.Object>>) structMap.get("components");

            Map<String, java.lang.Object> updatableComp = updateStructureStatus_extractComponent(state, allComps);
            if (updatableComp == null) {
                log.warn(errUpdatingMsg + "updating state not found in structure stored on DB.");
                return;
            }

            updatableComp.put("state", stateStr);

            String structUpdated;
            try {
                structUpdated = mapper.writeValueAsString(structMap);
            } catch (JsonProcessingException e) {
                log.warn(errUpdatingMsg + "can't serialize updated structure.", e);
                return;
            }

            objDB.getStatus().setStructure(structUpdated);
            objDB.getStatus().setLastStatusUpdAt(JavaDate.getNowDate());

            saveObjDBStatus(objDB);
        }
    }

    private String updateStructureStatus_extractStateValue(JOSPProtocol.StatusUpd state) {
        JOSPProtocol.JOSPStateUpdateStr updStr = (JOSPProtocol.JOSPStateUpdateStr) state.getUpdate();

        if (JSLBooleanState.JOSPBoolean.class.getSimpleName().compareToIgnoreCase(updStr.getType()) == 0)
            return Boolean.toString(new JSLBooleanState.JOSPBoolean(updStr.encode()).newState);

        if (JSLRangeState.JOSPRange.class.getSimpleName().compareToIgnoreCase(updStr.getType()) == 0)
            return Double.toString(new JSLRangeState.JOSPRange(updStr.encode()).newState);

        return null;
    }

    private Map<String, java.lang.Object> updateStructureStatus_extractComponent(JOSPProtocol.StatusUpd state, ArrayList<Map<String, java.lang.Object>> allComps) {
        for (String compName : state.getComponentPath().split(">"))
            for (Map<String, java.lang.Object> subComp : allComps)
                if (subComp.get("name").equals(compName)) {
                    allComps = (ArrayList<Map<String, java.lang.Object>>) subComp.get("components");
                    if (allComps == null)
                        return subComp;
                    break;
                }
        return null;
    }


    // DB methods

    private Object getOrCreateObjDB(String objId) {
        Optional<Object> optObj = objectDBService.find(objId);
        if (optObj.isPresent())
            return optObj.get();

        Object objDB = new Object();
        objDB.setOwner(new ObjectOwner());
        objDB.setInfo(new ObjectInfo());
        objDB.setStatus(new ObjectStatus());
        return objDB;
    }

    private Object getObjDB(String objId) throws JODObjectNotInDBException {
        Optional<Object> optObj = objectDBService.find(objId);
        if (optObj.isPresent())
            return optObj.get();

        throw new JODObjectNotInDBException(objId);
    }

    private void saveObjDB(Object objDB) {
        objectDBService.save(objDB);
    }

    private void saveObjDBStatus(Object objDB) {
        objectDBService.save(objDB.getStatus());
    }


    // Object presentation messages

    public String getMsgOBJ_INFO() {
        return msgOBJ_INFO;
    }

    public String getMsgOBJ_STRUCT() throws JODObjectNotInDBException {
        Object objDB = getObjDB(getId());
        return JOSPProtocol_ObjectToService.createObjectStructMsg(getId(), objDB.getStatus().getStructure());
    }

    public String getMsgOBJ_PERM() {
        return msgOBJ_PERM;
    }
}

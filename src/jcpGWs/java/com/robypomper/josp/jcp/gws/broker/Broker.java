package com.robypomper.josp.jcp.gws.broker;

import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaStructures.Pair;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.db.apis.entities.Permission;
import com.robypomper.josp.jcp.gws.exceptions.JSLServiceMissingPermissionException;
import com.robypomper.josp.protocol.JOSPPerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@SuppressWarnings("RedundantIfStatement")
public class Broker implements BrokerJOD, BrokerJSL, BrokerObjDB {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Broker.class);
    private final Map<String, BrokerClientJOD> registeredObjs = new HashMap<>();
    private final Map<String, BrokerClientJSL> registeredSrvs = new HashMap<>();
    private final Map<String, BrokerClientObjDB> registeredObjsDB = new HashMap<>();
    private final PermissionsDBService permissionsDBService;


    // Constructors

    public Broker(PermissionsDBService permissionsDBService) {
        this.permissionsDBService = permissionsDBService;
    }


    // Register methods

    @Override
    public void registerObject(BrokerClientObjDB gwObject) {
        synchronized (registeredObjsDB) {
            if (registeredObjsDB.containsKey(gwObject.getId())) {
                log.warn(String.format("Error registering Object (DB) '%s' to GW Broker, object already registered", gwObject.getId()));
                return;
            }

            registeredObjsDB.put(gwObject.getId(), gwObject);
        }
    }

    @Override
    public void deregisterObject(BrokerClientObjDB gwObject) {
        synchronized (registeredObjsDB) {
            if (!registeredObjsDB.containsKey(gwObject.getId())) {
                log.warn(String.format("Error registering Object (DB) '%s' to GW Broker, object NOT registered", gwObject.getId()));
                return;
            }

            registeredObjsDB.remove(gwObject.getId());
        }
    }

    @Override
    public void registerObject(BrokerClientJOD gwObject) {
        synchronized (registeredObjs) {
            if (registeredObjs.containsKey(gwObject.getId())) {
                log.warn(String.format("Error registering Object '%s' to GW Broker, object already registered", gwObject.getId()));
                return;
            }

            registeredObjs.put(gwObject.getId(), gwObject);
        }

        if (registeredObjsDB.containsKey(gwObject.getId()))
            registeredObjsDB.get(gwObject.getId()).pause();
    }

    @Override
    public void deregisterObject(BrokerClientJOD gwObject) {
        synchronized (registeredObjs) {
            if (!registeredObjs.containsKey(gwObject.getId())) {
                log.warn(String.format("Error registering Object '%s' to GW Broker, object NOT registered", gwObject.getId()));
                return;
            }

            registeredObjs.remove(gwObject.getId());
        }

        send(gwObject, gwObject.getMsgOBJ_DISCONNECTED(), JOSPPerm.Type.Status);

        if (!registeredObjsDB.containsKey(gwObject.getId())) {
            if (gwObject.getObjDB() != null)
                registeredObjsDB.put(gwObject.getId(), gwObject.getObjDB());
            else {
                JavaAssertions.makeAssertion_Failed("Error deregistering Object '%s' because objDB not available");
                return;
            }
        }

        registeredObjsDB.get(gwObject.getId()).resume();
    }

    @Override
    public void registerService(BrokerClientJSL gwService) {
        synchronized (registeredSrvs) {
            if (registeredSrvs.containsKey(gwService.getId())) {
                log.warn(String.format("Error registering Service '%s' to GW Broker, service already registered", gwService.getId()));
                return;
            }

            registeredSrvs.put(gwService.getId(), gwService);
        }

        List<String> allowedObjects = new ArrayList<>();
        for (BrokerClientJOD obj : registeredObjs.values())
            if (checkServicePermissionOnObject(gwService.getId(), obj.getId(), JOSPPerm.Type.Status)) {
                allowedObjects.add(obj.getId());
                send(obj, gwService.getId(), obj.getMsgOBJ_INFO(), JOSPPerm.Type.Status);
                send(obj, gwService.getId(), obj.getMsgOBJ_STRUCT(), JOSPPerm.Type.Status);
                send(obj, gwService.getId(), obj.getMsgOBJ_PERM(), JOSPPerm.Type.CoOwner);
            }
        for (BrokerClientObjDB obj : registeredObjsDB.values())
            if (!registeredObjs.containsKey(obj.getId()))
                if (checkServicePermissionOnObject(gwService.getId(), obj.getId(), JOSPPerm.Type.Status)) {
                    allowedObjects.add(obj.getId());
                    send(obj, gwService.getId(), obj.getMsgOBJ_INFO(), JOSPPerm.Type.Status);
                    send(obj, gwService.getId(), obj.getMsgOBJ_STRUCT(), JOSPPerm.Type.Status);
                    send(obj, gwService.getId(), obj.getMsgOBJ_PERM(), JOSPPerm.Type.CoOwner);
                    send(obj, gwService.getId(), obj.getMsgOBJ_DISCONNECTED(), JOSPPerm.Type.Status);
                }

        log.info(String.format("Registered service '%s' to broker", gwService.getId()));
        log.trace(String.format("          available objects      [%s]", String.join(", ", registeredObjs.keySet())));
        log.trace(String.format("          available objects (DB) [%s]", String.join(", ", registeredObjsDB.keySet())));
        log.trace(String.format("          allowed objects        [%s]", String.join(", ", allowedObjects)));
    }

    @Override
    public void deregisterService(BrokerClientJSL gwService) {
        synchronized (registeredSrvs) {
            if (!registeredSrvs.containsKey(gwService.getId())) {
                log.warn(String.format("Error registering Service '%s' to GW Broker, service NOT registered", gwService.getId()));
                return;
            }

            registeredSrvs.remove(gwService.getId());
        }
    }


    // Dispatch Message methods

    @Override
    public void send(BrokerClientJOD gwClientJOD, String data, JOSPPerm.Type minPerm) {
        // from JOD to JSL
        // send to all service with at least 'minPerm' on gwClientJOD
        // send errors (PeerStreamException, PeerNotConnectedException) are only logged

        Set<String> srvIds = getAllowedServices_Cloud(gwClientJOD, minPerm).keySet();
        for (String srvId : srvIds) {
            BrokerClientJSL srv = registeredSrvs.get(srvId);
            if (srv == null) {
                log.warn(String.format("Error forward data from Object '%s' to Service '%s', service not registered on broker", gwClientJOD.getId(), srvId));
                continue;
            }

            try {
                srv.send(data);

            } catch (PeerStreamException | PeerNotConnectedException e) {
                log.warn(String.format("Error forward data from Object '%s' to Service '%s'", gwClientJOD.getId(), srvId), e);
            }
        }

        log.trace(String.format("Redirect from Object '%s' msg '%s", gwClientJOD.getId(), data.substring(0, 30)));
        log.trace(String.format("          available services [%s]", String.join(", ", registeredSrvs.keySet())));
        log.trace(String.format("          allowed services   [%s]", String.join(", ", srvIds)));
    }

    @Override
    public void send(BrokerClientJOD gwClientJOD, String srvId, String data, JOSPPerm.Type minPerm) {
        // from JOD to JSL
        // send to 'gwClientJSL' service only if it has at least 'minPerm' on gwClientJOD
        // send errors (JSLServiceMissingPermissionException, PeerStreamException, PeerNotConnectedException) are only logged

        if (!getAllowedServices_Cloud(gwClientJOD, minPerm).containsKey(srvId)) {
            log.warn(String.format("Error send data from Object '%s' to Service '%s', service missing permission to object ", gwClientJOD.getId(), srvId));
            return;
        }

        BrokerClientJSL srv = registeredSrvs.get(srvId);
        if (srv == null) {
            log.warn(String.format("Error send data from Object '%s' to Service '%s', service not registered on broker", gwClientJOD.getId(), srvId));
            return;
        }

        try {
            srv.send(data);

        } catch (PeerStreamException | PeerNotConnectedException e) {
            log.warn(String.format("Error send data from Object '%s' to Service '%s'", gwClientJOD.getId(), srvId), e);
        }
    }

    @Override
    public void send(BrokerClientJSL gwClientJSL, String objId, String data, JOSPPerm.Type minPerm) throws JSLServiceMissingPermissionException, PeerStreamException, PeerNotConnectedException {
        // from JSL to JOD
        // send to 'objId' object only if 'gwClientJSL' has at least 'minPerm' on destination object
        // send errors (PeerStreamException, PeerNotConnectedException) are throw to caller methods

        BrokerClientJOD obj = registeredObjs.get(objId);
        if (obj == null) {
            if (registeredObjsDB.get(objId) == null)
                log.warn(String.format("Error forward data from Service '%s' to Object '%s', object not registered on broker", gwClientJSL.getId(), objId));
            else
                log.warn(String.format("Error forward data from Service '%s' to Object '%s', object not connected to broker", gwClientJSL.getId(), objId));
            return;
        }

        if (!obj.getOwner().equals(JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString()) && !getAllowedServices_Cloud(obj, minPerm).containsKey(gwClientJSL.getId()))
            throw new JSLServiceMissingPermissionException(gwClientJSL.getSrvId(), objId, minPerm, null);

        obj.send(data);
    }


    // Permissions methods

    @Override
    public boolean checkServicePermissionOnObject(String srvId, String objId, JOSPPerm.Type minPerm) {
        BrokerClientJOD obj = registeredObjs.get(objId);
        if (obj == null)
            obj = registeredObjsDB.get(objId);
        if (obj == null) {
            log.warn(String.format("Error on check permission for Service '%s' on Object '%s', object not registered on broker", srvId, objId));
            return false;
        }

        return getAllowedServices_Cloud(obj, minPerm).containsKey(srvId);
    }

    @Override
    public Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> getAllowedServices(BrokerClientJOD gwClientJOD) {
        Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> allowedServices = new HashMap<>();
        List<Permission> objPermissions = permissionsDBService.findByObj(gwClientJOD.getId());

        // each obj's permission
        //     for each registers service
        //         if service allowed for access to obj
        //             add service (keep major permission)
        for (Permission objPerm : objPermissions)
            for (BrokerClientJSL srv : registeredSrvs.values())
                if (isAllowed(objPerm, gwClientJOD.getOwner(), srv.getSrvId(), srv.getUsrId()))
                    addGreaterServicePerm(allowedServices, srv.getId(), objPerm);

        return allowedServices;
    }

    private Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> getAllowedServices_Cloud(BrokerClientJOD gwClientJOD, JOSPPerm.Type minReqPerm) {
        Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> allowedServices = getAllowedServices(gwClientJOD);

        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> allowed : allowedServices.entrySet()) {
            if (allowed.getValue().getSecond() != JOSPPerm.Connection.LocalAndCloud)
                allowedServices.remove(allowed.getKey());
            else if (allowed.getValue().getFirst().lowerThan(minReqPerm))
                allowedServices.remove(allowed.getKey());
        }

        return allowedServices;
    }

    private boolean isAllowed(Permission objPerm, String objOwner, String srvSrvId, String srvUsrId) {
        String permSrvId = objPerm.getSrvId();
        String permUsrId = objPerm.getUsrId();

        if (!permSrvId.equals(JOSPPerm.WildCards.SRV_ALL.toString()))
            if (!permSrvId.equals(srvSrvId))
                return false;

        if (!permUsrId.equals(JOSPPerm.WildCards.USR_ALL.toString()))
            if (!(permUsrId.equals(JOSPPerm.WildCards.USR_OWNER.toString())
                    && objOwner.equals(srvUsrId))
                    && !objOwner.equals(JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString()))
                if (!permUsrId.equals(srvUsrId))
                    return false;

        return true;
    }

    private void addGreaterServicePerm(Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> allowedServices, String srvId, Permission objPerm) {
        if (allowedServices.containsKey(srvId)) {
            Pair<JOSPPerm.Type, JOSPPerm.Connection> existing = allowedServices.get(srvId);
            if (greaterThan_preferConnection(existing, objPerm))
                return;
        }

        allowedServices.put(srvId, new Pair<>(objPerm.getType(), objPerm.getConnection()));
    }

    @SuppressWarnings("unused")
    private boolean greaterThan_preferConnection(Pair<JOSPPerm.Type, JOSPPerm.Connection> existing, Permission objPerm) {
        // existing.connection > perm.connection
        if (existing.getSecond().greaterThan(objPerm.getConnection()))
            return true;

        // existing.connection < perm.connection
        if (existing.getSecond().lowerThan(objPerm.getConnection()))
            return false;

        // existing.connection = perm.connection
        // existing.type > perm.type
        if (existing.getFirst().greaterThan(objPerm.getType()))
            return true;

        // existing.connection = perm.connection
        // existing.type <= perm.type
        return false;
    }

    @SuppressWarnings("unused")
    private boolean greaterThan_preferType(Pair<JOSPPerm.Type, JOSPPerm.Connection> existing, Permission objPerm) {
        // existing.type > perm.type
        if (existing.getFirst().greaterThan(objPerm.getType()))
            return true;

        // existing.type < perm.type
        if (existing.getFirst().lowerThan(objPerm.getType()))
            return false;

        // existing.type = perm.type
        // existing.connection > perm.connection
        if (existing.getSecond().greaterThan(objPerm.getConnection()))
            return true;

        // existing.type = perm.type
        // existing.connection <= perm.connection
        return false;
    }

}

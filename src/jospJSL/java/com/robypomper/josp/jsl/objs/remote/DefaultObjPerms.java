package com.robypomper.josp.jsl.objs.remote;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceToObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultObjPerms extends ObjBase implements ObjPerms {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private List<JOSPPerm> perms = new ArrayList<>();
    private final Map<JOSPPerm.Connection, JOSPPerm.Type> permTypes = new HashMap<>();
    private final List<RemoteObjectPermsListener> listenersInfo = new ArrayList<>();


    // Constructor

    public DefaultObjPerms(JSLRemoteObject remoteObject, JSLServiceInfo serviceInfo) {
        super(remoteObject, serviceInfo);
        permTypes.put(JOSPPerm.Connection.OnlyLocal, JOSPPerm.Type.None);
        permTypes.put(JOSPPerm.Connection.LocalAndCloud, JOSPPerm.Type.None);
    }


    // Getters / Setters

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JOSPPerm> getPerms() {
        return perms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<JOSPPerm.Connection, JOSPPerm.Type> getPermTypes() {
        return permTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JOSPPerm.Type getServicePerm(JOSPPerm.Connection connType) {
        return permTypes.get(connType);
    }

    @Override
    public void addPerm(String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectAddPermMsg(getServiceInfo().getFullId(), getRemote().getId(), srvId, usrId, permType, connType));
    }

    @Override
    public void updPerm(String permId, String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectUpdPermMsg(getServiceInfo().getFullId(), getRemote().getId(), permId, srvId, usrId, permType, connType));
    }

    @Override
    public void remPerm(String permId) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectRemPermMsg(getServiceInfo().getFullId(), getRemote().getId(), permId));
    }


    // Processing

    public boolean processObjectPermsMsg(String msg) throws Throwable {
        List<JOSPPerm> oldPerms = perms;
        perms = JOSPProtocol_ObjectToService.getObjectPermsMsg_Perms(msg);
        emitInfo_PermissionsChanged(perms, oldPerms);
        return true;
    }

    public boolean processServicePermMsg(String msg) throws Throwable {
        JOSPPerm.Connection connType = JOSPProtocol_ObjectToService.getServicePermsMsg_ConnType(msg);
        JOSPPerm.Type oldPermType = permTypes.get(connType);
        permTypes.put(connType, JOSPProtocol_ObjectToService.getServicePermsMsg_PermType(msg));
        emitInfo_ServicePermChanged(connType, permTypes.get(connType), oldPermType);
        return true;
    }


    // Listeners

    @Override
    public void addListener(RemoteObjectPermsListener listener) {
        if (listenersInfo.contains(listener))
            return;

        listenersInfo.add(listener);
    }

    @Override
    public void removeListener(RemoteObjectPermsListener listener) {
        if (!listenersInfo.contains(listener))
            return;

        listenersInfo.remove(listener);
    }

    private void emitInfo_PermissionsChanged(List<JOSPPerm> perms, List<JOSPPerm> oldPerms) {
        for (RemoteObjectPermsListener l : listenersInfo)
            l.onPermissionsChanged(getRemote(), perms, oldPerms);
    }

    private void emitInfo_ServicePermChanged(JOSPPerm.Connection connType, JOSPPerm.Type type, JOSPPerm.Type oldPermType) {
        for (RemoteObjectPermsListener l : listenersInfo)
            l.onServicePermChanged(getRemote(), connType, type, oldPermType);
    }

}
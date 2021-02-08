package com.robypomper.josp.jsl.objs.remote;

import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceToObject;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for all object's interfaces ({@link ObjInfo}, {@link ObjStruct},
 * {@link ObjComm}...).
 * <p>
 * This class provide a default constructor and protected method callable only
 * from object's interfaces.
 */
public class ObjBase {

    // Internal vars

    private static final Logger log = LogManager.getLogger();

    private final JSLRemoteObject remoteObject;
    private final JSLServiceInfo serviceInfo;


    // Constructor

    public ObjBase(JSLRemoteObject remoteObject, JSLServiceInfo serviceInfo) {
        this.remoteObject = remoteObject;
        this.serviceInfo = serviceInfo;
    }


    // Getters

    protected JSLRemoteObject getRemote() {
        return remoteObject;
    }

    protected JSLServiceInfo getServiceInfo() {
        return serviceInfo;
    }


    // Send message to object

    protected void sendToObject(String msg) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (!getRemote().getComm().isConnected())
            throw new JSLRemoteObject.ObjectNotConnected(getRemote());

        JOSPPerm.Type minReqPerm = JOSPPerm.Type.None;
        if (JOSPProtocol_ServiceToObject.isObjectSetNameMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectSetOwnerIdMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectAddPermMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectUpdPermMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectRemPermMsg(msg))
            minReqPerm = JOSPPerm.Type.CoOwner;

        if (JOSPProtocol_ServiceToObject.isObjectActionCmdMsg(msg))
            minReqPerm = JOSPPerm.Type.Actions;

        // Send via local communication
        if (getRemote().getComm().isLocalConnected()) {
            JOSPPerm.Type permType = getRemote().getPerms().getPermTypes().get(JOSPPerm.Connection.OnlyLocal);
            if (permType.compareTo(minReqPerm) < 0)
                throw new JSLRemoteObject.MissingPermission(getRemote(), JOSPPerm.Connection.OnlyLocal, permType, minReqPerm, msg);

            try {
                ((DefaultObjComm) getRemote().getComm()).getConnectedLocalClient().sendData(msg);
                return;

            } catch (PeerNotConnectedException | PeerStreamException e) {
                log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("Error on sending message '%s' to object (via local) because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            }
        }

        // Send via cloud communication
        if (getRemote().getComm().isCloudConnected()) {
            JOSPPerm.Type permType = getRemote().getPerms().getPermTypes().get(JOSPPerm.Connection.LocalAndCloud);
            if (permType.compareTo(minReqPerm) < 0)
                throw new JSLRemoteObject.MissingPermission(getRemote(), JOSPPerm.Connection.LocalAndCloud, permType, minReqPerm, msg);

            try {
                ((DefaultObjComm) getRemote().getComm()).getCloudConnection().sendData(msg);

            } catch (PeerNotConnectedException | PeerStreamException e) {
                log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("Error on sending message '%s' to object (via cloud) because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            }
        }
    }

}

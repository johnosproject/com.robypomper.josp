package com.robypomper.josp.jsl.objs.history;

import com.robypomper.communication.client.Client;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.DefaultObjComm;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class HistoryBase {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLRemoteObject remoteObject;
    private final JSLServiceInfo srvInfo;


    // Constructor

    protected HistoryBase(JSLRemoteObject remoteObject, JSLServiceInfo srvInfo) {
        this.remoteObject = remoteObject;
        this.srvInfo = srvInfo;
    }


    // Getters

    protected JSLRemoteObject getRemote() {
        return remoteObject;
    }

    protected JSLServiceInfo getServiceInfo() {
        return srvInfo;
    }


    // Send message to object

    protected void sendToObjectLocally(JOSPPerm.Type minReqPerm, String msg) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (!getRemote().getComm().isLocalConnected())
            throw new JSLRemoteObject.ObjectNotConnected(getRemote());

        // Send via local communication
        if (getRemote().getComm().isLocalConnected()) {
            JOSPPerm.Type permType = getRemote().getPerms().getPermTypes().get(JOSPPerm.Connection.OnlyLocal);
            if (permType.compareTo(minReqPerm) < 0)
                throw new JSLRemoteObject.MissingPermission(getRemote(), JOSPPerm.Connection.OnlyLocal, permType, minReqPerm, msg);

            try {
                ((DefaultObjComm) getRemote().getComm()).getConnectedLocalClient().sendData(msg);
                return;

            } catch (Client.ServerNotConnectedException e) {
                log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("Error on sending message '%s' to object (via local) because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            }
        }
    }

    protected void sendToObjectCloudly(JOSPPerm.Type minReqPerm, String msg) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (!getRemote().getComm().isCloudConnected())
            throw new JSLRemoteObject.ObjectNotConnected(getRemote());

        // Send via cloud communication
        if (getRemote().getComm().isCloudConnected()) {
            JOSPPerm.Type permType = getRemote().getPerms().getPermTypes().get(JOSPPerm.Connection.LocalAndCloud);
            if (permType.compareTo(minReqPerm) < 0)
                throw new JSLRemoteObject.MissingPermission(getRemote(), JOSPPerm.Connection.LocalAndCloud, permType, minReqPerm, msg);

            try {
                ((DefaultObjComm) getRemote().getComm()).getCloudConnection().sendData(msg);

            } catch (Client.ServerNotConnectedException e) {
                log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("Error on sending message '%s' to object (via cloud) because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            }
        }
    }

}

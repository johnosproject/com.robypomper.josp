package com.robypomper.josp.jsl.objs.remote;

import com.robypomper.communication.client.Client;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLGwS2OClient;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.states.StateException;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultObjComm extends ObjBase implements ObjComm {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLCommunication communication;
    private boolean isCloudConnected = false;
    private final List<JSLLocalClient> localConnections = new ArrayList<>();
    private final List<RemoteObjectConnListener> listenersConn = new ArrayList<>();


    // Constructor

    public DefaultObjComm(JSLRemoteObject remoteObject, JSLServiceInfo serviceInfo, JSLCommunication communication) {
        super(remoteObject, serviceInfo);
        this.communication = communication;
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLCommunication getCommunication() {
        return communication;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return isCloudConnected() || isLocalConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCloudConnected() {
        return communication.isCloudConnected() && isCloudConnected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLocalConnected() {
        return getConnectedLocalClient() != null;
    }


    /**
     * {@inheritDoc}
     */
    public void setCloudConnected(boolean connected) {
        if (isCloudConnected==connected) return;

        isCloudConnected = connected;

        if (isCloudConnected)
            emitConn_CloudConnected();
        else
            emitConn_CloudDisconnected();
    }

    /**
     * Add given client as communication channel between this JSL object representation
     * and corresponding JOD object.
     * <p>
     * This method checks if it's the first client set for current object
     * representation then send object's presentation requests (objectInfo and
     * objectStructure).
     * <p>
     * If there is already a connected client, then this method disconnect given
     * one. Ther must be at least one connected client, others are used as
     * backups.
     *
     * @param localClient the client connected with corresponding JOD object.
     */
    public void addLocalClient(JSLLocalClient localClient) {
        if (!isLocalConnected() && !localClient.isConnected()) {
            log.trace(Mrk_JSL.JSL_OBJS_SUB, String.format("Object '%s' not connected, connect local connection", localClient.getObjId()));
            try {
                localClient.connect();
            } catch (IOException | Client.AAAException | StateException ignore) {
            }
        }
        if (isLocalConnected() && localClient.isConnected()) {
            log.trace(Mrk_JSL.JSL_OBJS_SUB, String.format("Object '%s' already connected", localClient.getObjId()));
            // Force switch thread, to allow starting client's thread
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            try {
                localClient.disconnect();
            } catch (StateException ignore) {
            }
        }

        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Checking object '%s' connection from '%s' service already known", localClient.getServerId(), getServiceInfo().getSrvId()));
        JSLLocalClient toUpdate = null;
        for (JSLLocalClient cl : localConnections)
            if (cl.getServerAddr().equals(localClient.getServerAddr())
                    && cl.getServerPort() == localClient.getServerPort()
                //&& cl.getClientAddr().equals(locConn.getClientAddr()) // client's address and port vary on socket disconnection
                //&& cl.getClientPort() == locConn.getClientPort()
            ) {
                log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Connection already known to '%s' object on server '%s:%d' from '%s' service's '%s:%d' client", getRemote().getInfo().getName(), localClient.getServerAddr(), localClient.getServerPort(), getServiceInfo().getSrvId(), localClient.getClientAddr(), localClient.getClientPort()));
                if (!cl.isConnected()) {
                    toUpdate = cl;
                    break;
                }
                log.trace(Mrk_JSL.JSL_OBJS_SUB, String.format("Disconnect new connection to '%s' object on server '%s:%d' from '%s' service's '%s:%d' client", getRemote().getInfo().getName(), localClient.getServerAddr(), localClient.getServerPort(), getServiceInfo().getSrvId(), localClient.getClientAddr(), localClient.getClientPort()));
                try {
                    localClient.disconnect();
                } catch (StateException ignore) {
                }
                return;
            }
        if (toUpdate == null)
            log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Connection NOT known to '%s' object on server '%s:%d' from '%s' service's '%s:%d' client", getRemote().getInfo().getName(), localClient.getServerAddr(), localClient.getServerPort(), getServiceInfo().getSrvId(), localClient.getClientAddr(), localClient.getClientPort()));

        if (toUpdate == null) {
            log.trace(Mrk_JSL.JSL_OBJS_SUB, String.format("Adding new connection for '%s' object on '%s' service", localClient.getObjId(), getServiceInfo().getSrvId()));
        } else {
            log.trace(Mrk_JSL.JSL_OBJS_SUB, String.format("Updating existing connection for '%s' object on '%s' service", localClient.getObjId(), getServiceInfo().getSrvId()));
            localConnections.remove(toUpdate);
        }
        localConnections.add(localClient);
        localClient.setRemoteObject(getRemote());
        if (localClient.isConnected())
            emitConn_LocalConnected(localClient);
    }

    /**
     * {@inheritDoc}
     */
    public void removeLocalClient(JSLLocalClient localClient) {
        localConnections.remove(localClient);
        emitConn_LocalDisconnected(localClient);
    }

    /**
     * @return an array containing all available local connections to the object.
     * if the object is disconnected or works only via cloud, then the
     * returned array will be empty.
     */
    public List<JSLLocalClient> getLocalClients() {
        return localConnections;
    }

    /**
     * @return the client connected (if any) to the corresponding JOD object.
     */
    public JSLLocalClient getConnectedLocalClient() {
        for (JSLLocalClient client : localConnections) {
            if (client.isConnected())
                return client;
        }
        return null;
    }

    public JSLGwS2OClient getCloudConnection() {
        return getCommunication().getCloudConnection();
    }


    // Processing

    public boolean processObjectDisconnectMsg(String msg, JOSPPerm.Connection connType) throws Throwable {
        if (connType == JOSPPerm.Connection.LocalAndCloud && isCloudConnected) {
            isCloudConnected = false;
            emitConn_CloudDisconnected();
        }
        return true;
    }


    // Listeners

    @Override
    public void addListener(RemoteObjectConnListener listener) {
        if (listenersConn.contains(listener))
            return;

        listenersConn.add(listener);
    }

    @Override
    public void removeListener(RemoteObjectConnListener listener) {
        if (!listenersConn.contains(listener))
            return;

        listenersConn.remove(listener);
    }

    private void emitConn_LocalConnected(JSLLocalClient localClient) {
        for (RemoteObjectConnListener l : listenersConn)
            l.onLocalConnected(getRemote(), localClient);
    }

    private void emitConn_LocalDisconnected(JSLLocalClient localClient) {
        for (RemoteObjectConnListener l : listenersConn)
            l.onLocalDisconnected(getRemote(), localClient);

    }

    private void emitConn_CloudConnected() {
        for (RemoteObjectConnListener l : listenersConn)
            l.onCloudConnected(getRemote());
    }

    private void emitConn_CloudDisconnected() {
        for (RemoteObjectConnListener l : listenersConn)
            l.onCloudDisconnected(getRemote());
    }

}

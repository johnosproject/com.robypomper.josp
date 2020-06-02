package com.robypomper.josp.jsl.objs;

import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLGwS2OClient;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Implementation of the {@link JSLObjsMngr} interface.
 */
public class JSLObjsMngr_002 implements JSLObjsMngr {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLSettings_002 locSettings;
    private final JSLServiceInfo srvInfo;
    private final List<JSLRemoteObject> objs = new ArrayList<>();
    private JSLGwS2OClient cloudConnection = null;


    // Constructor

    /**
     * Default objects manager constructor.
     *
     * @param settings the JSL settings.
     * @param srvInfo  the service's info.
     */
    public JSLObjsMngr_002(JSLSettings_002 settings, JSLServiceInfo srvInfo) {
        this.locSettings = settings;
        this.srvInfo = srvInfo;

        log.info(Mrk_JSL.JSL_OBJS, "Initialized JSLObjsMngr");
    }


    // Object's access

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLRemoteObject> getAllObjects() {
        return Collections.unmodifiableList(objs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLRemoteObject> getAllConnectedObjects() {
        List<JSLRemoteObject> connObjs = new ArrayList<>();
        for (JSLRemoteObject obj : objs)
            if (obj.isConnected())
                connObjs.add(obj);

        return Collections.unmodifiableList(connObjs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLRemoteObject getById(String objId) {
        for (JSLRemoteObject obj : objs)
            if (objId.equalsIgnoreCase(obj.getId()))
                return obj;

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLRemoteObject getByConnection(JSLLocalClient client) {
        for (JSLRemoteObject obj : objs)
            if (obj.getLocalClients().contains(client))
                return obj;

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLRemoteObject> searchObjects(JSLObjectSearchPattern pattern) {
        log.warn(Mrk_JSL.JSL_OBJS, "Method searchObjects(...) not implemented, return empty objects list");
        return new ArrayList<>();
    }


    // Connections mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewConnection(JSLLocalClient serverConnection) {
        String locConnObjId = serverConnection.getObjId();
        String serverAddr = String.format("%s:%d", serverConnection.getServerAddr(), serverConnection.getServerPort());
        String clientAddr = String.format("%s:%d", serverConnection.getClientAddr(), serverConnection.getClientPort());

        JSLRemoteObject remObj = getById(locConnObjId);
        boolean toRegObj = remObj == null;
        if (toRegObj) {
            log.info(Mrk_JSL.JSL_OBJS, String.format("Register new object '%s' and add connection ('%s' > '%s) to '%s' service", locConnObjId, clientAddr, serverAddr, srvInfo.getSrvId()));
            remObj = new DefaultJSLRemoteObject(srvInfo, locConnObjId, serverConnection);
            objs.add(remObj);

        } else {
            log.info(Mrk_JSL.JSL_OBJS, String.format("Add object '%s' connection ('%s' > '%s) to '%s' service", locConnObjId, clientAddr, serverAddr, srvInfo.getSrvId()));
            remObj.addLocalClient(serverConnection);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeConnection(JSLLocalClient client) {
        log.warn(Mrk_JSL.JSL_OBJS, "Method removeConnection(...) not implemented, return false");
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCloudConnection(JSLGwS2OClient cloudConnection) {
        this.cloudConnection = cloudConnection;
    }

}

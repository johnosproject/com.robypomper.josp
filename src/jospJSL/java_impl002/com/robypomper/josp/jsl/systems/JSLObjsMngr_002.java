package com.robypomper.josp.jsl.systems;

import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.comm.JSLGwS2OClient;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.DefaultJSLRemoteObject;
import com.robypomper.josp.jsl.objs.JSLObjectSearchPattern;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Implementation of the {@link JSLObjsMngr} interface.
 */
public class JSLObjsMngr_002 implements JSLObjsMngr {

    // Internal vars

    private final JSL_002.Settings locSettings;
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
    public JSLObjsMngr_002(JSL_002.Settings settings, JSLServiceInfo srvInfo) {
        this.locSettings = settings;
        this.srvInfo = srvInfo;
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
        System.out.println("DEB: JSLObjMngr_002::searchObjects()");
        return new ArrayList<>();
    }


    // Connections mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewConnection(JSLLocalClient localConnection) {
        String locConnObjId = localConnection.getObjId();
        JSLRemoteObject remObj = getById(locConnObjId);

        if (remObj == null) {
            remObj = new DefaultJSLRemoteObject(srvInfo, locConnObjId, localConnection);
            System.out.println(String.format("INF: registered object %s and add connection", locConnObjId));
            objs.add(remObj);

        } else {
            System.out.println(String.format("INF: add connection to already registered object '%s'", locConnObjId));
            remObj.addLocalClient(localConnection);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeConnection(JSLLocalClient client) {
        System.out.println("DEB: JSLObjMngr_002::removeConnection()");
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

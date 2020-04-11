package com.robypomper.josp.jsl.systems;

import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.comm.JSLCloudConnection;
import com.robypomper.josp.jsl.comm.JSLLocalConnection;
import com.robypomper.josp.jsl.objs.JSLObjectSearchPattern;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSLObjsMngr_002 implements JSLObjsMngr {

    // Internal vars

    private final JSL_002.Settings locSettings;
    private final List<JSLRemoteObject> objs = new ArrayList<>();
    private JSLCloudConnection cloudConnection = null;


    // Constructor

    public JSLObjsMngr_002(JSL_002.Settings settings) {
        this.locSettings = settings;
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
    public JSLRemoteObject getByConnection(JSLLocalConnection localConnection) {
        for (JSLRemoteObject obj : objs)
            if (obj.getLocalConnections().contains(localConnection))
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
    public void addNewConnection(JSLLocalConnection localConnection) {
        System.out.println("DEB: JSLObjMngr_002::addNewConnection()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeConnection(JSLLocalConnection localConnection) {
        System.out.println("DEB: JSLObjMngr_002::removeConnection()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCloudConnection(JSLCloudConnection cloudConnection) {
        this.cloudConnection = cloudConnection;
    }

}

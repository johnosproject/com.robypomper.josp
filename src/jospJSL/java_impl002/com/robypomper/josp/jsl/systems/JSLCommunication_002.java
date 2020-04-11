package com.robypomper.josp.jsl.systems;

import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.comm.JSLCloudConnection;
import com.robypomper.josp.jsl.comm.JSLLocalConnection;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSLCommunication_002 implements JSLCommunication {

    private final JSL_002.Settings settings;
    private final JSLServiceInfo srvInfo;
    private final JSLUserMngr usr;
    private final JSLObjsMngr objs;
    private final JCPClient_Service jcpClient;
    private final List<JSLLocalConnection> localConnections = new ArrayList<>();
    private JSLCloudConnection cloudConnection;


    // Constructor

    public JSLCommunication_002(JSL_002.Settings settings, JSLServiceInfo srvInfo, JSLUserMngr usr, JSLObjsMngr objs, JCPClient_Service jcpClient) {
        this.settings = settings;
        this.srvInfo = srvInfo;
        this.usr = usr;
        this.objs = objs;
        this.jcpClient = jcpClient;
    }


    // Connections access

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLCloudConnection getCloudConnection() {
        return cloudConnection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLLocalConnection> getAllLocalConnection() {
        return Collections.unmodifiableList(localConnections);
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLocalSearchActive() {
        System.out.println("DEB: JSLCommunication::isLocalSearchActive()");
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startSearchLocalObjects() {
        System.out.println("DEB: JSLCommunication::startSearchLocalObjects()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopSearchLocalObjects() {
        System.out.println("DEB: JSLCommunication::stopSearchLocalObjects()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCloudConnected() {
        System.out.println("DEB: JSLCommunication::isCloudConnected()");
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectCloud() {
        System.out.println("DEB: JSLCommunication::connectCloud()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectCloud() {
        System.out.println("DEB: JSLCommunication::disconnectCloud()");
    }

}

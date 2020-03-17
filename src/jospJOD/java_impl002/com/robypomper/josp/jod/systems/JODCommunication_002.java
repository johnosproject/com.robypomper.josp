package com.robypomper.josp.jod.systems;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jod.JOD;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;

/**
 * ToDo: doc JODCommunication_002
 */
public class JODCommunication_002 implements JODCommunication {

    public JODCommunication_002(JOD.Settings settings, JODObjectInfo objInfo, JCPClient jcpClient, JODPermissions permissions) {}

    @Override
    public void dispatchUpdate(JODState component, JODStateUpdate update) {
        System.out.println("DEB: JDCommunication_002::dispatchUpdate()");
    }

    @Override
    public void startLocalComm() {
        System.out.println("DEB: JDCommunication_002::startLocalComm()");
    }

    @Override
    public void stopLocalComm() {
        System.out.println("DEB: JDCommunication_002::stopLocalComm()");
    }

    @Override
    public void startCloudComm() {
        System.out.println("DEB: JDCommunication_002::startCloudComm()");
    }

    @Override
    public void stopCloudComm() {
        System.out.println("DEB: JDCommunication_002::stopCloudComm()");
    }

    @Override
    public void setStructure(JODStructure structure) {
        System.out.println("DEB: JDCommunication_002::setStructure()");
    }
}

package com.robypomper.josp.jcp.gws.broker;

import com.robypomper.java.JavaStructures;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.Map;

public interface BrokerObjDB {

    void registerObject(BrokerClientObjDB gwObject);

    void deregisterObject(BrokerClientObjDB gwObject);


    // Communication

    void send(BrokerClient gwClientJOD, String data, JOSPPerm.Type minPerm);

    void send(BrokerClient gwClientJOD, String srvId, String data, JOSPPerm.Type minPerm);


    // Permission

    Map<String, JavaStructures.Pair<JOSPPerm.Type, JOSPPerm.Connection>> getObjectCloudAllowedServices(String objId);

}

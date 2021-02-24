package com.robypomper.josp.jcp.gws.broker;

import com.robypomper.java.JavaStructures;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.Map;

public interface BrokerJOD {

    // Registration

    void registerObject(BrokerClientJOD gwObject);

    void deregisterObject(BrokerClientJOD gwObject);


    // Communication

    void send(BrokerClientJOD gwClientJOD, String data, JOSPPerm.Type minPerm);

    void send(BrokerClientJOD gwClientJOD, String srvId, String data, JOSPPerm.Type minPerm);


    // Permission

    Map<String, JavaStructures.Pair<JOSPPerm.Type, JOSPPerm.Connection>> getAllowedServices(BrokerClientJOD gwClientJOD);

}

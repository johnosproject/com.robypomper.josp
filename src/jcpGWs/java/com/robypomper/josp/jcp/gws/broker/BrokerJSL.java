package com.robypomper.josp.jcp.gws.broker;

import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.josp.jcp.gws.exceptions.JSLServiceMissingPermissionException;
import com.robypomper.josp.protocol.JOSPPerm;

public interface BrokerJSL {

    // Registration

    void registerService(BrokerClientJSL gwService);

    void deregisterService(BrokerClientJSL gwService);


    // Communication

    void send(BrokerClientJSL gwClientJSL, String objId, String data, JOSPPerm.Type minPerm) throws JSLServiceMissingPermissionException, PeerStreamException, PeerNotConnectedException;


    // Permissions

    boolean checkServicePermissionOnObject(String srvId, String objId, JOSPPerm.Type minPerm);

}

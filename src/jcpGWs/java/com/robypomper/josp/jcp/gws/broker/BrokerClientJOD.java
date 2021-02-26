package com.robypomper.josp.jcp.gws.broker;

import com.robypomper.josp.protocol.JOSPPerm;

public interface BrokerClientJOD extends BrokerClient {

    // Getters object

    String getOwner();

    BrokerClientObjDB getObjDB();


    // Getters messages

    String getMsgOBJ_INFO();

    String getMsgOBJ_STRUCT();

    String getMsgOBJ_PERM();

    String getMsgSERVICE_PERM(JOSPPerm.Type type, JOSPPerm.Connection conn);

    String getMsgOBJ_DISCONNECTED();

}

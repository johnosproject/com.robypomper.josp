package com.robypomper.josp.jcp.gws.broker;

public interface BrokerClientJOD extends BrokerClient {

    // Getters object

    String getOwner();

    BrokerClientObjDB getObjDB();


    // Getters messages

    String getMsgOBJ_INFO();

    String getMsgOBJ_STRUCT();

    String getMsgOBJ_PERM();

    String getMsgOBJ_DISCONNECTED();

}

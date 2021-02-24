package com.robypomper.josp.jcp.gws.broker;

public interface BrokerObjDB {

    void registerObject(BrokerClientObjDB gwObject);

    void deregisterObject(BrokerClientObjDB gwObject);

}

package com.robypomper.comm.connection;

import java.util.Date;

public interface ConnectionStats {

    // Last connection

    Date getLastConnection();

    Date getLastDisconnection();


    // Last HeartBeat

    Date getLastHeartBeat();

    Date getLastHeartBeatFailed();

    long getHeartBeatReceived();


    // Last Data

    Date getLastDataRx();

    long getBytesRx();

    Date getLastDataTx();

    long getBytesTx();

}

package com.robypomper.comm.behaviours;

import com.robypomper.comm.exception.PeerException;

public interface HeartBeatImpl extends HeartBeatConfigs {

    // Class constants

    String MSG_HEARTBEAT_REQ = "hb_rq";
    String MSG_HEARTBEAT_RES = "hb_rs";


    // Sender

    void sendHeartBeatReq() throws PeerException;

    boolean isWaiting();

    boolean isTimeoutExpired();


    // Processor

    boolean processHeartBeatMsg(byte[] data) throws PeerException;

}

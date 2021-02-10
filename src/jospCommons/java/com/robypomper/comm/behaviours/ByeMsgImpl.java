package com.robypomper.comm.behaviours;

import com.robypomper.comm.exception.PeerException;

public interface ByeMsgImpl extends ByeMsgConfigs {

    // Messages methods

    boolean processByeMsg(byte[] data);

    void sendByeMsg() throws PeerException;

}

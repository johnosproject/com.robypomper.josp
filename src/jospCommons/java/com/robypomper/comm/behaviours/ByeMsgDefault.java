package com.robypomper.comm.behaviours;

import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.exception.PeerException;
import com.robypomper.comm.peer.PeerAbs;

import java.util.Arrays;

public class ByeMsgDefault extends ByeMsgConfigsDefault implements ByeMsgImpl {

    // Class constants

    public static final String BYE_MSG = "bye";


    // Internal vars

    // peer configs
    private final PeerAbs peer;


    // Constructors

    public ByeMsgDefault(PeerAbs peer, DataEncodingConfigs dataEncoding) {
        this(peer, dataEncoding, ENABLE, BYE_MSG);
    }

    public ByeMsgDefault(PeerAbs peer, DataEncodingConfigs dataEncoding, byte[] byeMsg) {
        this(peer, dataEncoding, ENABLE, byeMsg);
    }

    public ByeMsgDefault(PeerAbs peer, DataEncodingConfigs dataEncoding, String byeMsg) {
        this(peer, dataEncoding, ENABLE, byeMsg);
    }

    public ByeMsgDefault(PeerAbs peer, DataEncodingConfigs dataEncoding, boolean enable, byte[] byeMsg) {
        super(dataEncoding, enable, byeMsg);
        this.peer = peer;
    }

    public ByeMsgDefault(PeerAbs peer, DataEncodingConfigs dataEncoding, boolean enable, String byeMsg) {
        super(dataEncoding, enable, byeMsg);
        this.peer = peer;
    }


    // Messages methods

    @Override
    public boolean processByeMsg(byte[] data) {
        if (!Arrays.equals(BYE_MSG.getBytes(peer.getDataEncodingConfigs().getCharset()), data))
            return false;

        emitOnBye(peer, this);

        return true;
    }

    @Override
    public void sendByeMsg() throws PeerException {
        peer.sendData(BYE_MSG.getBytes(peer.getDataEncodingConfigs().getCharset()));
    }

}

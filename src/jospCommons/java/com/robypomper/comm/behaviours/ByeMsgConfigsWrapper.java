package com.robypomper.comm.behaviours;

import com.robypomper.comm.client.ClientWrapper;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.peer.Peer;

public class ByeMsgConfigsWrapper extends ByeMsgConfigsDefault {

    // Internal vars

    // server
    private final ClientWrapper clientWrapper;


    // Constructors

    public ByeMsgConfigsWrapper(ClientWrapper clientWrapper, DataEncodingConfigs dataEncoding) {
        this(clientWrapper, dataEncoding, ENABLE, BYE_MSG);
    }

    public ByeMsgConfigsWrapper(ClientWrapper clientWrapper, DataEncodingConfigs dataEncoding, byte[] byeMsg) {
        this(clientWrapper, dataEncoding, ENABLE, byeMsg);
    }

    public ByeMsgConfigsWrapper(ClientWrapper clientWrapper, DataEncodingConfigs dataEncoding, String byeMsg) {
        this(clientWrapper, dataEncoding, ENABLE, byeMsg);
    }

    public ByeMsgConfigsWrapper(ClientWrapper clientWrapper, DataEncodingConfigs dataEncoding, boolean enable, byte[] byeMsg) {
        super(dataEncoding, enable, byeMsg);

        this.clientWrapper = clientWrapper;
    }

    public ByeMsgConfigsWrapper(ClientWrapper clientWrapper, DataEncodingConfigs dataEncoding, boolean enable, String byeMsg) {
        super(dataEncoding, enable, byeMsg);

        this.clientWrapper = clientWrapper;
    }

    private final ByeMsgListener clientByeMsgListener = new ByeMsgListener() {

        @Override
        public void onBye(Peer peer, ByeMsgImpl byeMsg) {
            emitOnBye(peer, byeMsg);
        }

    };


    // Getter/setters

    @Override
    public void enable(boolean enable) {
        if (clientWrapper != null)
            if (clientWrapper.getWrapper() != null) {
                clientWrapper.getWrapper().getByeConfigs().enable(enable);
                if (isEnable())
                    clientWrapper.getWrapper().getByeConfigs().addListener(clientByeMsgListener);
                else
                    clientWrapper.getWrapper().getByeConfigs().removeListener(clientByeMsgListener);
            }

        super.enable(enable);
    }

    @Override
    public void setByeMsg(byte[] byeMsg) {
        if (clientWrapper != null)
            if (clientWrapper.getWrapper() != null)
                clientWrapper.getWrapper().getByeConfigs().setByeMsg(byeMsg);

        super.setByeMsg(byeMsg);
    }

    @Override
    public void setByeMsg(String byeMsg) {
        if (clientWrapper != null)
            if (clientWrapper.getWrapper() != null)
                clientWrapper.getWrapper().getByeConfigs().setByeMsg(byeMsg);

        super.setByeMsg(byeMsg);
    }

}

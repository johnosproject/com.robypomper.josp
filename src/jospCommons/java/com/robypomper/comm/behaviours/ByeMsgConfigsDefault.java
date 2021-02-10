package com.robypomper.comm.behaviours;

import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.peer.Peer;
import com.robypomper.java.JavaListeners;

import java.util.ArrayList;
import java.util.List;

public class ByeMsgConfigsDefault implements ByeMsgConfigs {

    // Internal vars

    // bye msg's configs
    private final DataEncodingConfigs dataEncoding;
    private boolean enable;
    private byte[] byeMsg = null;
    private String byeMsgStr = null;
    // heartbeat's Listeners
    private final List<ByeMsgListener> listeners = new ArrayList<>();


    // Constructors

    public ByeMsgConfigsDefault(DataEncodingConfigs dataEncoding) {
        this(dataEncoding, ENABLE, BYE_MSG);
    }

    public ByeMsgConfigsDefault(DataEncodingConfigs dataEncoding, byte[] byeMsg) {
        this(dataEncoding, ENABLE, byeMsg);
    }

    public ByeMsgConfigsDefault(DataEncodingConfigs dataEncoding, String byeMsg) {
        this(dataEncoding, ENABLE, byeMsg);
    }

    public ByeMsgConfigsDefault(DataEncodingConfigs dataEncoding, boolean enable, byte[] byeMsg) {
        this.dataEncoding = dataEncoding;
        enable(enable);
        setByeMsg(byeMsg);
    }

    public ByeMsgConfigsDefault(DataEncodingConfigs dataEncoding, boolean enable, String byeMsg) {
        this.dataEncoding = dataEncoding;
        enable(enable);
        setByeMsg(byeMsg);
    }


    // Getter/setters

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void enable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public byte[] getByeMsg() {
        if (byeMsg != null) return byeMsg;
        return byeMsgStr.getBytes(dataEncoding.getCharset());
    }

    @Override
    public String getByeMsgString() {
        if (byeMsg != null) return new String(byeMsg, dataEncoding.getCharset());
        return byeMsgStr;
    }

    @Override
    public void setByeMsg(byte[] byeMsg) {
        this.byeMsg = byeMsg;
        if (byeMsg != null)
            this.byeMsgStr = null;
    }

    @Override
    public void setByeMsg(String byeMsg) {
        if (byeMsgStr != null)
            this.byeMsg = null;
        this.byeMsgStr = byeMsg;
    }


    // Listeners

    @Override
    public void addListener(ByeMsgListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ByeMsgListener listener) {
        listeners.remove(listener);
    }

    protected void emitOnBye(Peer peer, ByeMsgImpl byeMsg) {
        // Log already printed by PeerAbs::emitOnDataRx
        //log.trace(String.format("Peer '%s' received BYE message", peer.getLocalId()));

        JavaListeners.emitter(this, listeners, "onBye", new JavaListeners.ListenerMapper<ByeMsgListener>() {
            @Override
            public void map(ByeMsgListener l) {
                l.onBye(peer, byeMsg);
            }
        });
    }

}

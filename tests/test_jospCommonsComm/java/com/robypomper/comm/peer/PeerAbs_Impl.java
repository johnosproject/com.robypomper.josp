package com.robypomper.comm.peer;

import com.robypomper.comm.behaviours.HeartBeatImpl;
import com.robypomper.comm.exception.PeerDisconnectionException;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class PeerAbs_Impl extends PeerAbs {

    public static final byte[] AVOID_PROCESSING = "DontProcessThisData".getBytes();

    private final Socket socket;
    private byte[] lastProcessedData;

    protected PeerAbs_Impl(String localId, String remoteId, String protoName, Socket socket) {
        super(localId, remoteId, protoName);
        this.socket = socket;

        if (getSocket() != null && getSocket().isConnected() && !getSocket().isClosed())
            startupConnection();
    }

    protected PeerAbs_Impl(String localId, String remoteId, String protoName, Socket socket, PeerConnectionListener listenerConnection) {
        super(localId, remoteId, protoName, listenerConnection);
        this.socket = socket;

        if (getSocket() != null && getSocket().isConnected() && !getSocket().isClosed())
            startupConnection();
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public HeartBeatImpl getHeartBeatConfigs() {
        return (HeartBeatImpl) super.getHeartBeatConfigs();
    }

    @Override
    protected void closeSocket() throws PeerDisconnectionException {
        try {
            getSocket().close();

        } catch (IOException e) {
            throw new PeerDisconnectionException(this, getSocket(), getConnectionInfo().getRemoteInfo().getAddr(), getConnectionInfo().getRemoteInfo().getPort(), e);
        }
    }

    @Override
    protected boolean processData(byte[] data) {
        System.out.println(new String(data, getDataEncodingConfigs().getCharset()));
        if (Arrays.equals(AVOID_PROCESSING, data))
            return false;

        lastProcessedData = data;
        return true;
    }

    @Override
    protected boolean processData(String data) {
        return false;
    }

    public byte[] getLastProcessedData() {
        return lastProcessedData;
    }

}

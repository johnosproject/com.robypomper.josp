package com.robypomper.comm.peer;

import com.robypomper.comm.behaviours.ByeMsgConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigs_Mock;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.connection.ConnectionInfo;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerDisconnectionException;

import java.net.Socket;

public class Peer_Mock implements Peer {

    private final Socket socket;

    public Peer_Mock(Socket socket) {
        this.socket = socket;
    }

    @Override
    public ConnectionState getState() {
        return null;
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        return null;
    }

    @Override
    public DisconnectionReason getDisconnectionReason() {
        return null;
    }

    @Override
    public String getLocalId() {
        return null;
    }

    @Override
    public String getRemoteId() {
        return null;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void disconnect() throws PeerDisconnectionException {
    }

    @Override
    public void sendData(byte[] data) {
    }

    @Override
    public void sendData(String data) {
    }

    @Override
    public DataEncodingConfigs getDataEncodingConfigs() {
        return null;
    }

    @Override
    public ByeMsgConfigs getByeConfigs() {
        return null;
    }

    @Override
    public HeartBeatConfigs getHeartBeatConfigs() {
        return new HeartBeatConfigs_Mock();
    }

    @Override
    public void addListener(PeerConnectionListener listener) {
    }

    @Override
    public void removeListener(PeerConnectionListener listener) {
    }

    @Override
    public void addListener(PeerDataListener listener) {
    }

    @Override
    public void removeListener(PeerDataListener listener) {
    }

}

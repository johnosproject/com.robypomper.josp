package com.robypomper.comm.peer;

import com.robypomper.comm.behaviours.ByeMsgConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.connection.ConnectionInfo;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;

import java.net.Socket;

public interface Peer {

    // Getters

    ConnectionState getState();

    ConnectionInfo getConnectionInfo();

    DisconnectionReason getDisconnectionReason();

    String getLocalId();    //peer.getConnectionInfo().getLocalInfo().getId()

    String getRemoteId();   //peer.getConnectionInfo().getRemoteInfo().getId()

    Socket getSocket();


    // Connection methods

    void disconnect() throws PeerDisconnectionException;


    // Messages methods

    void sendData(byte[] data) throws PeerNotConnectedException, PeerStreamException;

    void sendData(String data) throws PeerNotConnectedException, PeerStreamException;


    // Behaviours configs

    DataEncodingConfigs getDataEncodingConfigs();

    ByeMsgConfigs getByeConfigs();

    HeartBeatConfigs getHeartBeatConfigs();


    // Listeners

    void addListener(PeerConnectionListener listener);

    void removeListener(PeerConnectionListener listener);

    void addListener(PeerDataListener listener);

    void removeListener(PeerDataListener listener);

}

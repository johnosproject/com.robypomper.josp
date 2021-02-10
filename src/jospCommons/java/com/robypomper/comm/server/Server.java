package com.robypomper.comm.server;

import com.robypomper.comm.behaviours.ByeMsgConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.exception.ServerShutdownException;
import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.peer.PeerInfoLocal;

import java.util.List;

public interface Server {

    // Getters

    ServerState getState();

    PeerInfoLocal getServerPeerInfo();

    String getLocalId();    //getLocalInfo().getId()

    String getProtocolName();

    List<ServerClient> getClients();


    // Server startup methods

    void startup() throws ServerStartupException;

    void shutdown() throws ServerShutdownException;


    // Behaviours configs

    DataEncodingConfigs getDataEncodingConfigs();

    ByeMsgConfigs getByeConfigs();

    HeartBeatConfigs getHeartBeatConfigs();


    // Listeners

    void addListener(ServerStateListener listener);

    void removeListener(ServerStateListener listener);

    void addListener(ServerClientsListener listener);

    void removeListener(ServerClientsListener listener);

    void addListener(ServerDataListener listener);

    void removeListener(ServerDataListener listener);

}

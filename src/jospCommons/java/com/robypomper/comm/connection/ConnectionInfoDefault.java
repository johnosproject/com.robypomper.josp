package com.robypomper.comm.connection;

import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.behaviours.HeartBeatListener;
import com.robypomper.comm.exception.PeerInfoSocketNotConnectedException;
import com.robypomper.comm.peer.*;
import com.robypomper.java.JavaAssertions;

public class ConnectionInfoDefault implements ConnectionInfo {

    // Internal vars

    private final Peer peer;
    private ConnectionState state = ConnectionState.DISCONNECTED;
    private final ConnectionStatsDefault stats;
    private final PeerInfoLocalDefault local;
    private final PeerInfoRemoteDefault remote;
    private final String protoName;


    // Constructors

    public ConnectionInfoDefault(Peer peer, String localId, String remoteId, String protoName) {
        this.peer = peer;
        boolean connected = peer.getSocket() != null && peer.getSocket().isConnected() && !peer.getSocket().isClosed();

        this.stats = new ConnectionStatsDefault();
        this.local = new PeerInfoLocalDefault(localId, protoName);
        this.remote = new PeerInfoRemoteDefault(remoteId, protoName);
        if (connected) {
            updateOnConnected();
        }

        this.protoName = protoName;

        //peer.addListener(connectionListener);
        peer.addListener(dataListener);
        peer.getHeartBeatConfigs().addListener(hbListener);
    }


    // toString()

    @Override
    public String toString() {
        String connState;
        //@formatter:off
        switch (state) {
            case CONNECTING:        connState = "-| "; break;
            case WAITING_SERVER:    connState = "..."; break;
            case CONNECTED:         connState = "---"; break;
            case DISCONNECTING:     connState = " |-"; break;
            case DISCONNECTED:      connState = " | "; break;
            default:                connState = "   ";
        }
        //@formatter:on
        return String.format("%s <%s> %s", local, connState, remote);
    }


    // Getters

    @Override
    public ConnectionState getState() {
        return state;
    }

    @Override
    public ConnectionStats getStats() {
        return stats;
    }

    @Override
    public PeerInfoLocal getLocalInfo() {
        return local;
    }

    @Override
    public PeerInfoRemote getRemoteInfo() {
        return remote;
    }

    @Override
    public String getProtocolName() {
        return protoName;
    }


    // Update on peer's events - Connection

    public void updateOnConnecting() {
        state = ConnectionState.CONNECTING;
    }

    public void updateOnConnecting_Waiting() {
        state = ConnectionState.WAITING_SERVER;
    }

    public void updateOnConnected() {
        try {
            local.updateOnConnected(peer.getSocket());
            remote.updateOnConnected(peer.getSocket());

        } catch (PeerInfoSocketNotConnectedException e) {
            JavaAssertions.makeAssertion_Failed(e, String.format("Method ConnectionInfoDefault.updateOnConnected() called when internal socket is NOT connected. [%s] %s", e.getClass().getSimpleName(), e.getMessage()));
        }
        state = ConnectionState.CONNECTED;
        stats.updateOnConnected();
        //System.out.print(String.format("#### updateOnConnected (%s) %s\n#### ", this.hashCode(), this));
        //System.out.println(JavaThreads.currentStackTraceToString().replace("\n","\n#### ") + "\n");
    }

    public void updateOnDisconnecting() {
        state = ConnectionState.DISCONNECTING;
    }

    public void updateOnDisconnected() {
        state = ConnectionState.DISCONNECTED;
        stats.updateOnDisconnected();
        local.updateOnDisconnected();
        remote.updateOnDisconnected();
        //System.out.println(String.format("#### updateOnDisconnected (%s) %s", this.hashCode(), this));
        //System.out.println(JavaThreads.currentStackTraceToString().replace("\n","\n#### "));
    }


    // Update on peer's events - Heartbeat

    public void updateOnHeartBeatSuccess() {
        stats.updateOnHeartBeatSuccess();
    }

    public void updateOnHeartBeatFail() {
        stats.updateOnHeartBeatFail();
    }


    // Update on peer's events - Data Rx/Tx

    public void updateOnDataRx(byte[] data) {
        stats.updateOnDataRx(data);
    }

    public void updateOnDataTx(byte[] data) {
        stats.updateOnDataTx(data);
    }


    // Updates listeners

    /*
     * The 'connectionListener' listener is the best way to keep current
     * ConnectionInfoDefault updated. But it miss onConnecting,
     * onConnecting_Waiting and onDisconnecting events, required to update
     * all states of current ConnectionInfoDefault.
     *
     * Because of that, the 'connectionListener' instance is not actually used.
     * Instead, as workaround, the ConnectionInfoDefault updateOnXXX() methods
     * are called by PeerAbs class and his emitOnXXX() methods.
     */
    private PeerConnectionListener connectionListener = new PeerConnectionListener() {

        @Override
        public void onConnecting(Peer peer) {
            updateOnConnecting();
        }

        @Override
        public void onWaiting(Peer peer) {
            updateOnConnecting_Waiting();
        }

        @Override
        public void onConnect(Peer peer) {
            updateOnConnected();
        }

        @Override
        public void onDisconnecting(Peer peer) {
            updateOnDisconnecting();
        }

        @Override
        public void onDisconnect(Peer peer) {
            updateOnDisconnected();
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable exception) {
        }

    };

    private PeerDataListener dataListener = new PeerDataListener() {

        @Override
        public void onDataRx(Peer peer, byte[] data) {
            updateOnDataRx(data);
        }

        @Override
        public void onDataTx(Peer peer, byte[] data) {
            updateOnDataTx(data);
        }

    };

    private HeartBeatListener hbListener = new HeartBeatListener() {

        @Override
        public void onSend(Peer peer, HeartBeatConfigs hb) {
        }

        @Override
        public void onSuccess(Peer peer, HeartBeatConfigs hb) {
            updateOnHeartBeatSuccess();
        }

        @Override
        public void onFail(Peer peer, HeartBeatConfigs hb) {
            updateOnHeartBeatFail();
        }

    };

}

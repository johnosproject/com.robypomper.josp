package com.robypomper.comm.peer;

import com.robypomper.comm.exception.PeerInfoSocketNotConnectedException;
import com.robypomper.java.JavaAssertions;

import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class PeerInfoLocalDefault extends PeerInfoDefault implements PeerInfoLocal {

    // Internal vars

    private NetworkInterface localIntf = null;


    // Constructors

    public PeerInfoLocalDefault(String localId, String protoName) {
        super(localId, protoName, true);
    }


    // Getters

    @Override
    public NetworkInterface getIntf() {
        return localIntf;
    }


    // Updaters

    public void updateOnConnected(Socket socket) throws PeerInfoSocketNotConnectedException {
        super.updateOnConnected(socket);
        try {
            localIntf = NetworkInterface.getByInetAddress(getAddr());
        } catch (SocketException e) {
            JavaAssertions.makeAssertion_Failed(e, "Method NetworkInterface.getByInetAddress(InetAddress) is called from PeerInfoDefault.updateOnConnected(Socket) method, so it should NOT throw any SocketException");
        }
    }

    public void updateOnConnected(ServerSocket socket) throws PeerInfoSocketNotConnectedException {
        super.updateOnConnected(socket);
        try {
            localIntf = NetworkInterface.getByInetAddress(getAddr());
        } catch (SocketException e) {
            JavaAssertions.makeAssertion_Failed(e, "Method NetworkInterface.getByInetAddress(InetAddress) is called from PeerInfoDefault.updateOnConnected(ServerSocket) method, so it should NOT throw any SocketException");
        }
    }

}

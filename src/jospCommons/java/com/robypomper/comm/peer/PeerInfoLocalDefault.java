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
            JavaAssertions.makeAssertionFailed(String.format("Method 'NetworkInterface.getByInetAddress(InetAddress)' can't throw SocketException because given address is the socket's address. [%s] %s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    public void updateOnConnected(ServerSocket socket) throws PeerInfoSocketNotConnectedException {
        super.updateOnConnected(socket);
        try {
            localIntf = NetworkInterface.getByInetAddress(getAddr());
        } catch (SocketException e) {
            JavaAssertions.makeAssertionFailed(String.format("Method 'NetworkInterface.getByInetAddress(InetAddress)' can't throw SocketException because given address is the socket's address. [%s] %s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

}

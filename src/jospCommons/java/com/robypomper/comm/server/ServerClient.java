package com.robypomper.comm.server;

import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.peer.PeerAbs;

import java.net.Socket;
import java.nio.charset.Charset;

public class ServerClient extends PeerAbs {

    // Internal vars

    private final ServerAbs server;
    private final Socket socket;


    // Constructors

    public ServerClient(ServerAbs server, String localId, String remoteId, String protoName, Socket socket,
                        Charset charset, byte[] delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, byte[] byeMsg) {
        super(localId, remoteId, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);

        this.server = server;
        this.socket = socket;

        if (getSocket() != null && getSocket().isConnected() && !getSocket().isClosed())
            startupConnection();
    }


    // Getters

    public Server getServer() {
        return server;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    protected void closeSocket() throws PeerDisconnectionException {
        server.closeSocket(this, socket);
    }

    @Override
    protected boolean processData(byte[] data) {
        return server.processData(this, data);
    }

    @Override
    protected boolean processData(String data) {
        return server.processData(this, data);
    }

}

package com.robypomper.comm.client;

import com.robypomper.comm.behaviours.ByeMsgConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.configs.AutoReConnectConfigs;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerUnknownHostException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public abstract class ClientAbsTCP extends ClientAbs {

    // Constructors

    public ClientAbsTCP(String localId, String remoteId, String remoteAddr, int remotePort, String protoName) throws PeerUnknownHostException {
        this(localId, remoteId, str2Inet_onClientConstructor(localId, remoteAddr), remotePort, protoName);
    }

    public ClientAbsTCP(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName) {
        this(localId, remoteId, remoteAddr, remotePort, protoName,
                DataEncodingConfigs.CHARSET, DataEncodingConfigs.DELIMITER,
                HeartBeatConfigs.TIMEOUT_MS, HeartBeatConfigs.TIMEOUT_HB_MS, HeartBeatConfigs.ENABLE_HB_RES,
                ByeMsgConfigs.ENABLE, ByeMsgConfigs.BYE_MSG,
                AutoReConnectConfigs.ENABLE, AutoReConnectConfigs.DELAY);
    }

    public ClientAbsTCP(String localId, String remoteId, String remoteAddr, int remotePort, String protoName,
                        Charset charset, byte[] delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, byte[] byeMsg,
                        Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        this(localId, remoteId, str2Inet_onClientConstructor(localId, remoteAddr), remotePort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
    }

    public ClientAbsTCP(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName,
                        Charset charset, byte[] delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, byte[] byeMsg,
                        Boolean enableReConnect, int reConnectDelayMs) {
        super(localId, remoteId, remoteAddr, remotePort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
    }

    public ClientAbsTCP(String localId, String remoteId, String remoteAddr, int remotePort, String protoName,
                        Charset charset, String delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, String byeMsg,
                        Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        this(localId, remoteId, str2Inet_onClientConstructor(localId, remoteAddr), remotePort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
    }

    public ClientAbsTCP(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName,
                        Charset charset, String delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, String byeMsg,
                        Boolean enableReConnect, int reConnectDelayMs) {
        super(localId, remoteId, remoteAddr, remotePort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
    }


    // Connection methods

    @Override
    protected Socket generateConnectedSocket(InetAddress remoteAddr, int remotePort) throws PeerConnectionException {
        try {
            return new Socket(remoteAddr, remotePort);

        } catch (IOException e) {
            throw new PeerConnectionException(this, getSocket(), remoteAddr, remotePort, e);
        }
    }

    @Override
    protected void closeSocket() throws PeerDisconnectionException {
        try {
            getSocket().close();

        } catch (IOException e) {
            throw new PeerDisconnectionException(this, getSocket(), getConnectionInfo().getRemoteInfo().getAddr(), getConnectionInfo().getRemoteInfo().getPort(), e);
        }
    }

}

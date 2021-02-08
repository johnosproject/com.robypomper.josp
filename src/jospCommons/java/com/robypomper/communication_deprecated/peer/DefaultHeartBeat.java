package com.robypomper.communication_deprecated.peer;

import com.robypomper.communication_deprecated.CommunicationBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DefaultHeartBeat implements HeartBeatConfigs {

    // Class constants

    public static final String TH_HB_NAME_FORMAT = "%s_HEARTBEAT_WAIT";
    public static final String MSG_HEARTBEAT_SRV_STR = "hbsrv";
    public static final byte[] MSG_HEARTBEAT_SRV = MSG_HEARTBEAT_SRV_STR.getBytes(PeerInfo.CHARSET);
    public static final String MSG_HEARTBEAT_CLI_STR = "hbcli";
    public static final byte[] MSG_HEARTBEAT_CLI = MSG_HEARTBEAT_CLI_STR.getBytes(PeerInfo.CHARSET);
    public static final int HB_TIMEOUT_MS = 30 * 1000;
    public static final int HB_RESPONSE_TIMEOUT_MS = 5 * 1000;

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final String localId;
    private final SideType localSide;
    private final HeartBeatListener listener;
    private int hbTimeout = DefaultHeartBeat.HB_TIMEOUT_MS;
    private int hbResponseTimeout = DefaultHeartBeat.HB_RESPONSE_TIMEOUT_MS;
    private boolean hbEchoEnabled = true;
    private PeerInfo peerInfo = null;
    private String peerId = null;
    private CountDownLatch hbResponseLatch = null;
    private boolean hbTimedout = false;


    // Constructors

    public DefaultHeartBeat(String localId, SideType localSide, HeartBeatListener listener) {
        this(localId, localSide, listener, HB_TIMEOUT_MS, HB_RESPONSE_TIMEOUT_MS, true);
    }

    public DefaultHeartBeat(String localId, SideType localSide, HeartBeatListener listener,
                            int hbTimeout, int hbResponseTimeout, boolean hbEchoEnabled) {
        this(localId, localSide, null, null, listener, hbTimeout, hbResponseTimeout, hbEchoEnabled);
    }

    public DefaultHeartBeat(String localId, SideType localSide, String peerId, PeerInfo peerInfo,
                            HeartBeatListener listener,
                            int hbTimeout, int hbResponseTimeout, boolean hbEchoEnabled) {
        this.localId = localId;
        this.listener = listener;
        this.localSide = localSide;
        this.hbTimeout = hbTimeout;
        this.hbResponseTimeout = hbResponseTimeout;
        this.hbEchoEnabled = hbEchoEnabled;
        setPeer(peerId, peerInfo);
    }


    // Peer set/reset

    public void setPeer(String peerId, PeerInfo peerInfo) {
        this.peerId = peerId;
        this.peerInfo = peerInfo;
        if (peerInfo != null)
            peerInfo.setTCPTimeout(hbTimeout);
    }

    public void resetPeer() {
        this.peerId = null;
        this.peerInfo = null;
    }


    // Heartbeat config

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTimeout() {
        return this.hbTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimeout(int ms) {
        this.hbTimeout = ms;
        if (peerInfo != null) peerInfo.setTCPTimeout(this.hbTimeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getResponseTimeout() {
        return this.hbResponseTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResponseTimeout(int ms) {
        this.hbResponseTimeout = ms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getEchoEnabled() {
        return this.hbEchoEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEchoEnabled(boolean enabled) {
        this.hbEchoEnabled = enabled;
    }


    // Heartbeat requests

    public boolean isTimedout() {
        return hbTimedout;
    }

    public boolean sendHeartBeat(boolean delimiter) {
        if (hbResponseLatch == null) {
            hbResponseLatch = new CountDownLatch(1);

            try {
                CommunicationBase.transmitData(getOutputStream(), localSide == SideType.Server ? MSG_HEARTBEAT_SRV : MSG_HEARTBEAT_CLI, delimiter);
            } catch (IOException e) {
                hbResponseLatch = null;
                return false;
            }
            listener.onSend();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean hbResponse = false;
                hbTimedout = false;
                try {
                    hbResponse = hbResponseLatch.await(hbResponseTimeout, TimeUnit.MILLISECONDS);

                } catch (InterruptedException ignore) {
                }

                if (hbResponse)
                    listener.onSuccess();
                else {
                    hbTimedout = true;
                    listener.onError();
                }
                hbResponseLatch = null;
            }
        });
        t.setName(String.format(TH_HB_NAME_FORMAT, localId));
        t.start();

        return true;
    }

    private boolean tryProcessHeartBeatRequest(byte[] data, boolean delimiter) throws IOException {
        if (hbEchoEnabled && Arrays.equals(localSide == SideType.Server ? MSG_HEARTBEAT_CLI : MSG_HEARTBEAT_SRV, data)) {
            CommunicationBase.transmitData(getOutputStream(), data, delimiter);
            return true;
        }

        return false;
    }


    // Heartbeat response

    public boolean tryProcessHeartBeat(byte[] data, boolean delimiter) throws IOException {
        return tryProcessHeartBeatResponse(data) || tryProcessHeartBeatRequest(data, delimiter);
    }

    private boolean tryProcessHeartBeatResponse(byte[] data) {
        if (hbResponseLatch != null && Arrays.equals(localSide == SideType.Server ? MSG_HEARTBEAT_SRV : MSG_HEARTBEAT_CLI, data)) {
            hbResponseLatch.countDown();
            return true;
        }

        return false;
    }

    private DataOutputStream getOutputStream() throws IOException {
        try {
            return peerInfo.getOutStream();

        } catch (PeerInfo.PeerNotConnectedException | PeerInfo.PeerStreamsException e) {
            throw new IOException("Can't get output stream to send heartbeat message.");
        }
    }


    // Listener class

    public interface HeartBeatListener {

        /**
         * Event emitted when local side send a heartbeat request to remote side.
         */
        void onSend();

        /**
         * Event emitted when local side receive a heartbeat response from remote side
         * as 'connection is up' confirmation.
         */
        void onSuccess();

        /**
         * Event emitted when ???.
         */
        void onError();

    }

}

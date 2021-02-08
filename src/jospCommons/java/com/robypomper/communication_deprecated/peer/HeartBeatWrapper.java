package com.robypomper.communication_deprecated.peer;


public class HeartBeatWrapper implements HeartBeatConfigs {

    // Internal vars

    private HeartBeatConfigs hbWrapped;
    private int hbTimeout = DefaultHeartBeat.HB_TIMEOUT_MS;
    private int hbResponseTimeout = DefaultHeartBeat.HB_RESPONSE_TIMEOUT_MS;
    private boolean hbEchoEnabled = true;
    private PeerInfo peerInfo = null;
    private String peerId = null;


    // Wrapper

    public void setWrapped(HeartBeatConfigs hb) {
        this.hbWrapped = hb;
        hb.setTimeout(hbTimeout);
        hb.setResponseTimeout(hbResponseTimeout);
        hb.setEchoEnabled(hbEchoEnabled);
    }

    public void resetWrapped() {
        this.hbWrapped = null;
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
        if (hbWrapped != null)
            hbWrapped.setTimeout(ms);
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
        if (hbWrapped != null)
            hbWrapped.setResponseTimeout(ms);
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
        if (hbWrapped != null)
            hbWrapped.setEchoEnabled(enabled);
    }

}

package com.robypomper.communication_deprecated.peer;


import java.util.ArrayList;
import java.util.List;

public class HeartBeatWrapperList implements HeartBeatConfigs {

    // Internal vars

    private List<HeartBeatConfigs> hbWrappedList = new ArrayList<>();
    private int hbTimeout = DefaultHeartBeat.HB_TIMEOUT_MS;
    private int hbResponseTimeout = DefaultHeartBeat.HB_RESPONSE_TIMEOUT_MS;
    private boolean hbEchoEnabled = true;


    // Wrapper

    public void addWrapped(HeartBeatConfigs hb) {
        hbWrappedList.add(hb);
        hb.setTimeout(hbTimeout);
        hb.setResponseTimeout(hbResponseTimeout);
        hb.setEchoEnabled(hbEchoEnabled);
    }

    public void removeWrapped(HeartBeatConfigs hb) {
        hbWrappedList.remove(hb);
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
        for (HeartBeatConfigs hbWrapped : hbWrappedList)
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
        for (HeartBeatConfigs hbWrapped : hbWrappedList)
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
        for (HeartBeatConfigs hbWrapped : hbWrappedList)
            hbWrapped.setEchoEnabled(enabled);
    }

}

package com.robypomper.comm.behaviours;

import com.robypomper.comm.client.ClientWrapper;

public class HeartBeatConfigsWrapper extends HeartBeatConfigsDefault {

    // Internal vars

    // wrapper
    private final ClientWrapper clientWrapper;


    // Constructors

    public HeartBeatConfigsWrapper(ClientWrapper clientWrapper) {
        this(clientWrapper, TIMEOUT_MS);
    }

    public HeartBeatConfigsWrapper(ClientWrapper clientWrapper, int timeoutMs) {
        this(clientWrapper, timeoutMs, TIMEOUT_HB_MS);
    }

    public HeartBeatConfigsWrapper(ClientWrapper clientWrapper, int timeoutMs, int timeoutHBMs) {
        this(clientWrapper, timeoutMs, timeoutHBMs, HeartBeatConfigs.ENABLE_HB_RES);
    }

    public HeartBeatConfigsWrapper(ClientWrapper clientWrapper, int timeoutMs, int timeoutHBMs, boolean enableHBRes) {
        this.clientWrapper = clientWrapper;

        setTimeout(timeoutMs);
        setHBTimeout(timeoutHBMs);
        enableHBResponse(enableHBRes);
    }


    // Getter/setters

    @Override
    public void setTimeout(int ms) {
        if (clientWrapper != null)
            if (clientWrapper.getWrapper() != null)
                clientWrapper.getWrapper().getHeartBeatConfigs().setTimeout(ms);

        super.setTimeout(ms);
    }

    @Override
    public void setHBTimeout(int ms) {
        if (clientWrapper != null)
            if (clientWrapper.getWrapper() != null)
                clientWrapper.getWrapper().getHeartBeatConfigs().setHBTimeout(ms);

        super.setHBTimeout(ms);
    }

    @Override
    public void enableHBResponse(boolean enabled) {
        if (clientWrapper != null)
            if (clientWrapper.getWrapper() != null)
                clientWrapper.getWrapper().getHeartBeatConfigs().enableHBResponse(enabled);

        super.enableHBResponse(enabled);
    }

}

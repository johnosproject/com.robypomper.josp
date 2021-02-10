package com.robypomper.comm.configs;

import com.robypomper.comm.client.ClientWrapper;

public class AutoReConnectConfigsWrapper implements AutoReConnectConfigs {

    // Internal vars

    // wrapper
    private final ClientWrapper clientWrapper;
    // auto re-connect configs
    private boolean enable;
    private int delay;


    // Constructors

    public AutoReConnectConfigsWrapper(ClientWrapper clientWrapper) {
        this(clientWrapper, ENABLE, DELAY);
    }

    public AutoReConnectConfigsWrapper(ClientWrapper clientWrapper, boolean enable, int delayMs) {
        this.clientWrapper = clientWrapper;

        enable(enable);
        setDelay(delayMs);
    }


    // Getter/setters

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void enable(boolean enable) {
        if (clientWrapper.getWrapper() != null)
            clientWrapper.getWrapper().getAutoReConnectConfigs().enable(enable);

        this.enable = enable;
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public void setDelay(int ms) {
        if (clientWrapper.getWrapper() != null)
            clientWrapper.getWrapper().getAutoReConnectConfigs().setDelay(ms);

        this.delay = ms;
    }

}

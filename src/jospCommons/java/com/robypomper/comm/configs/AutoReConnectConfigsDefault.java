package com.robypomper.comm.configs;

public class AutoReConnectConfigsDefault implements AutoReConnectConfigs {

    // Internal vars

    // auto re-connect configs
    private boolean enable;
    private int delay;


    // Constructors

    public AutoReConnectConfigsDefault() {
        this(ENABLE, DELAY);
    }

    public AutoReConnectConfigsDefault(boolean enable, int delay) {
        enable(enable);
        setDelay(delay);
    }


    // Getter/setters

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void enable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public void setDelay(int ms) {
        this.delay = ms;
    }

}

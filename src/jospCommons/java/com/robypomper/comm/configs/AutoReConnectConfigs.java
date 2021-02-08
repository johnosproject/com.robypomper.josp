package com.robypomper.comm.configs;

public interface AutoReConnectConfigs {

    // Class constants

    boolean ENABLE = false;
    int DELAY = 30 * 1000;


    // Getter/setters

    boolean isEnable();

    void enable(boolean enable);

    int getDelay();

    void setDelay(int delayMs);

}

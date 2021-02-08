package com.robypomper.comm.behaviours;

public interface HeartBeatConfigs {

    // Class constants

    int TIMEOUT_MS = 30 * 1000;
    int TIMEOUT_HB_MS = 30 * 1000;
    boolean ENABLE_HB_RES = true;


    // Getter/setters

    int getTimeout();

    void setTimeout(int ms);

    int getHBTimeout();

    void setHBTimeout(int ms);

    boolean isHBResponseEnabled();

    void enableHBResponse(boolean enabled);


    // Listener

    void addListener(HeartBeatListener listener);

    void removeListener(HeartBeatListener listener);

}

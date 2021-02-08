package com.robypomper.communication_deprecated.peer;

public interface HeartBeatConfigs {

    enum SideType {
        Server,
        Client
    }


    // Heartbeat config

    /**
     * @return waiting time before sending heartbeat to server in ms.
     */
    int getTimeout();

    /**
     * Set waiting time before sending heartbeat to server in ms.
     * <p>
     * If equals 0 then, it disable the heartbeat.
     */
    void setTimeout(int ms);

    int getResponseTimeout();

    /**
     * If true, enable the echo response to HeartBeat request.
     */
    void setResponseTimeout(int ms);

    boolean getEchoEnabled();

    /**
     * When enable, local side respond to remote side's heartbeat requests.
     * <p>
     * This method is used to disable HeartBeat echo responses to
     * simulate a lost connection situation for test purposes.
     */
    void setEchoEnabled(boolean enabled);

}

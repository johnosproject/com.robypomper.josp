package com.robypomper.comm.behaviours;

public interface ByeMsgConfigs {

    // Class constants

    boolean ENABLE = true;
    String BYE_MSG = "bye";


    // Getter/setters

    boolean isEnable();

    void enable(boolean enable);

    byte[] getByeMsg();

    String getByeMsgString();

    void setByeMsg(byte[] byeMsg);

    void setByeMsg(String byeMsg);


    // Listener

    void addListener(ByeMsgListener listener);

    void removeListener(ByeMsgListener listener);

}

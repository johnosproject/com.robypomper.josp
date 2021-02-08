package com.robypomper.comm.server;

public interface ServerStateListener {

    // Events

    void onStart(Server server);

    void onStop(Server server);

    void onFail(Server server, String failMsg, Throwable exception);

}

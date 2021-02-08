package com.robypomper.comm.server;

public interface ServerClientsListener {

    // Events

    void onConnect(Server server, ServerClient client);

    void onDisconnect(Server server, ServerClient client);

    void onFail(Server server, ServerClient client, String failMsg, Throwable exception);

}

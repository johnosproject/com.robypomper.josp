package com.robypomper.josp.jsl.comm;

public interface JSLLocalConnection extends JSLConnection {

    /**
     * Close connection and disconnect current client.
     */
    void disconnect();

}

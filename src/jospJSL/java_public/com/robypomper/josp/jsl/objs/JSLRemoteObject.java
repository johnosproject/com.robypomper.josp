package com.robypomper.josp.jsl.objs;

import com.robypomper.josp.jsl.comm.JSLLocalClient;

import java.util.List;

/**
 * JOD Object representation for JSL library.
 */
public interface JSLRemoteObject {

    /**
     * @return <code>true</code> if and only if there at least is a valid and
     * open connection to the object.
     */
    boolean isConnected();

    /**
     * @return the object's id.
     */
    String getId();

    /**
     * @return the object's name.
     */
    String getName();

    /**
     * @return an array containing all available local connections to the object.
     * if the object is disconnected or works only via cloud, then the
     * returned array will be empty.
     */
    List<JSLLocalClient> getLocalClients();


    void addLocalConnection(JSLLocalClient localClient);

    void replaceLocalConnection(JSLLocalClient oldConnection, JSLLocalClient newConnection);
}

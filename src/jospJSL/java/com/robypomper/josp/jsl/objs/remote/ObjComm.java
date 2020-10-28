package com.robypomper.josp.jsl.objs.remote;

import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;

public interface ObjComm {

    // Getters

    @Deprecated
    JSLCommunication getCommunication();

    /**
     * @return <code>true</code> if the objects is reachable via JCP or direct
     * communication.
     */
    boolean isConnected();

    /**
     * @return <code>true</code> if the objects is reachable via JCP.
     */
    boolean isCloudConnected();

    /**
     * @return <code>true</code> if and only if there at least is a valid and
     * open connection to the object.
     */
    boolean isLocalConnected();


    // Listeners

    void addListener(RemoteObjectConnListener listener);

    void removeListener(RemoteObjectConnListener listener);

    interface RemoteObjectConnListener {

        void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient);

        void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient);

        void onCloudConnected(JSLRemoteObject obj);

        void onCloudDisconnected(JSLRemoteObject obj);

    }

}

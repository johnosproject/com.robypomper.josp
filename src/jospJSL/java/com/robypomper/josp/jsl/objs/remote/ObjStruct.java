package com.robypomper.josp.jsl.objs.remote;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;

public interface ObjStruct {

    // Getters

    /**
     * @return true if current service received the JOSP Structure message and
     * initialized the Remote object's structure.
     */
    boolean isInit();

    /**
     * @return the object's structure.
     */
    JSLRoot getStructure();


    // Listeners

    void addListener(RemoteObjectStructListener listener);

    void removeListener(RemoteObjectStructListener listener);

    interface RemoteObjectStructListener {

        void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot);

    }

}

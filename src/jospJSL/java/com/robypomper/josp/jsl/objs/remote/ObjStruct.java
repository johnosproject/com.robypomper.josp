package com.robypomper.josp.jsl.objs.remote;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.history.HistoryCompStatus;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.util.List;

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

    /**
     * @return the object's component corresponding to given path.
     */
    JSLComponent getComponent(String compPath);

    /**
     * @return the object's component corresponding to given path.
     */
    JSLComponent getComponent(JSLComponentPath compPath);


    // Listeners

    void addListener(RemoteObjectStructListener listener);

    void removeListener(RemoteObjectStructListener listener);

    interface RemoteObjectStructListener {

        void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot);

    }


    // Components History

    List<JOSPStatusHistory> getComponentHistory(JSLComponent component, HistoryLimits limits, int timeoutSeconds) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

    void getComponentHistory(JSLComponent component, HistoryLimits limits, HistoryCompStatus.StatusHistoryListener listener) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

}

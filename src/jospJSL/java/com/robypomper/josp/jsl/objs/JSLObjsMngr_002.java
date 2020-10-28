/* *****************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************** */

package com.robypomper.josp.jsl.objs;

import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.remote.DefaultObjComm;
import com.robypomper.josp.jsl.objs.structure.AbsJSLState;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Implementation of the {@link JSLObjsMngr} interface.
 */
public class JSLObjsMngr_002 implements JSLObjsMngr {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLSettings_002 locSettings;
    private final JSLServiceInfo srvInfo;
    private final List<JSLRemoteObject> objs = new ArrayList<>();
    private JSLCommunication communication = null;
    private final List<ObjsMngrListener> listeners = new ArrayList<>();


    // Constructor

    /**
     * Default objects manager constructor.
     *
     * @param settings the JSL settings.
     * @param srvInfo  the service's info.
     */
    public JSLObjsMngr_002(JSLSettings_002 settings, JSLServiceInfo srvInfo) {
        this.locSettings = settings;
        this.srvInfo = srvInfo;

        log.info(Mrk_JSL.JSL_OBJS, "Initialized JSLObjsMngr");

        AbsJSLState.loadAllStateClasses();
    }


    // Object's access

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLRemoteObject> getAllObjects() {
        return Collections.unmodifiableList(objs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLRemoteObject> getAllConnectedObjects() {
        List<JSLRemoteObject> connObjs = new ArrayList<>();
        for (JSLRemoteObject obj : objs)
            if (obj.getComm().isLocalConnected())
                connObjs.add(obj);

        return Collections.unmodifiableList(connObjs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLRemoteObject getById(String objId) {
        for (JSLRemoteObject obj : objs)
            if (objId.equalsIgnoreCase(obj.getId()))
                return obj;

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLRemoteObject getByConnection(JSLLocalClient client) {
        for (JSLRemoteObject obj : objs)
            if (((DefaultObjComm)obj.getComm()).getLocalClients().contains(client))
                return obj;

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLRemoteObject> searchObjects(JSLObjectSearchPattern pattern) {
        log.warn(Mrk_JSL.JSL_OBJS, "Method searchObjects(...) not implemented, return empty objects list");
        return new ArrayList<>();
    }


    // Connections mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewConnection(JSLLocalClient serverConnection) {
        String locConnObjId = serverConnection.getObjId();
        String serverAddr = String.format("%s:%d", serverConnection.getServerAddr(), serverConnection.getServerPort());
        String clientAddr = String.format("%s:%d", serverConnection.getClientAddr(), serverConnection.getClientPort());

        JSLRemoteObject remObj = getById(locConnObjId);
        boolean toRegObj = remObj == null;
        if (toRegObj) {
            log.info(Mrk_JSL.JSL_OBJS, String.format("Register new local object '%s' and add connection ('%s' > '%s) to '%s' service", locConnObjId, clientAddr, serverAddr, srvInfo.getSrvId()));
            remObj = new DefaultJSLRemoteObject(srvInfo, locConnObjId, serverConnection, communication);
            objs.add(remObj);
            emit_ObjAdded(remObj);

        } else {
            log.info(Mrk_JSL.JSL_OBJS, String.format("Add object '%s' connection ('%s' > '%s) to '%s' service", locConnObjId, clientAddr, serverAddr, srvInfo.getSrvId()));
            ((DefaultObjComm)remObj.getComm()).addLocalClient(serverConnection);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeConnection(JSLLocalClient client) {
        log.warn(Mrk_JSL.JSL_OBJS, "Method removeConnection(...) not implemented, return false");
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCommunication(JSLCommunication communication) {
        this.communication = communication;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCloudObject(String objId) {
        assert getById(objId) == null;
        log.info(Mrk_JSL.JSL_OBJS, String.format("Register new object '%s' to '%s' service", objId, srvInfo.getSrvId()));
        DefaultJSLRemoteObject remObj = new DefaultJSLRemoteObject(srvInfo, objId, communication);
        objs.add(remObj);
        emit_ObjAdded(remObj);
    }


    // Listeners connections

    public void addListener(ObjsMngrListener listener) {
        if (listeners.contains(listener))
            return;

        listeners.add(listener);
    }

    public void removeListener(ObjsMngrListener listener) {
        if (!listeners.contains(listener))
            return;

        listeners.remove(listener);
    }

    private void emit_ObjAdded(JSLRemoteObject obj) {
        for (ObjsMngrListener l : listeners)
            l.onObjAdded(obj);
    }

    private void emit_ObjRemoved(JSLRemoteObject obj) {
        // ToDo: add calls to emit_ObjRemoved()
        for (ObjsMngrListener l : listeners)
            l.onObjRemoved(obj);
    }

}

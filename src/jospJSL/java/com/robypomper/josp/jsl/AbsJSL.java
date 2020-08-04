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

package com.robypomper.josp.jsl;

import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.log.Mrk_JOD;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;


/**
 * Default {@link JSL} implementation, initialization excluded.
 * <p>
 * This class fully manage a JSL library (connect,disconnect,status...) for all
 * JSL systems. But JSL systems initialization is delegate to his sub-classes.
 * That allow to initialize multiple JSL objects using different systems
 * implementations. Helping provide new JSL versions and flavours.
 * <p>
 * The JSL and {@link AbsJSL} hierarchy is design to allow sub-classes to initialize
 * JSL systems (user mngr, objs, comm...) and delegate JSL systems orchestration
 * to AbsJSL class. AbsJSL class manage JSL system using only their interfaces,
 * that make system implementation completely interoperable (at JSL level). So
 * AbsJSL sub-classes (like {@link JSL_002} can switch to different systems
 * implementations/versions keeping full compatibility with all others JSL
 * systems.
 * <p>
 * All AbsJSL sub-classes must implement the <code>instance(...)</code> method
 * and return a self instance. <code>instance(...)</code> method can be
 * implemented using {@link JSL.Settings} param or his sub-class.
 * Returned class from {@link FactoryJSL#getJSLClass(String)} must implement a
 * <code>instance(...)</code> method with a param corresponding to class returned
 * by <code>FactoryJOD#getJODSettingsClass</code>. Both method are called using
 * same String param corresponding to JSL version.
 */
@SuppressWarnings("JavadocReference")
public abstract class AbsJSL implements JSL {

    // Private systems references

    private final JSL.Settings settings;
    private final JCPClient_Service jcpClient;
    private final JSLServiceInfo srvInfo;
    private final JSLUserMngr user;
    private final JSLObjsMngr objs;
    private final JSLCommunication comm;


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private Status status = Status.DISCONNECTED;


    // Constructor

    /**
     * Default constructor, set all private systems references.
     *
     * @param settings  object containing current JOD configs.
     * @param jcpClient instance of JCP client for services.
     * @param srvInfo   {@link JSLServiceInfo} reference.
     * @param user      {@link JSLUserMngr} reference.
     * @param objs      {@link JSLObjsMngr} reference.
     * @param comm      {@link JSLCommunication} reference.
     */
    public AbsJSL(Settings settings, JCPClient_Service jcpClient, JSLServiceInfo srvInfo, JSLUserMngr user, JSLObjsMngr objs, JSLCommunication comm) {
        this.settings = settings;
        this.jcpClient = jcpClient;
        this.srvInfo = srvInfo;
        this.user = user;
        this.objs = objs;
        this.comm = comm;

        log.info(Mrk_JSL.JSL_MAIN, String.format("Initialized AbsJSL/%s instance for '%s' ('%s') service", this.getClass().getSimpleName(), srvInfo.getSrvName(), srvInfo.getFullId()));
    }


    // JSL mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws ConnectException {
        log.info(Mrk_JSL.JSL_MAIN, String.format("Connect JSL instance for '%s' service", srvInfo.getSrvId()));
        log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL status is %s", status()));

        if (status() != Status.DISCONNECTED
                && status() != Status.RECONNECTING)
            throw new ConnectException(String.format("Can't connect JSL service '%s' because his state is '%s'.", srvInfo.getSrvId(), status()));

        if (status != Status.RECONNECTING) {
            status = Status.CONNECTING;
            log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL set status %s", Status.CONNECTING));
        }

        log.trace(Mrk_JSL.JSL_MAIN, "JSLCommunication start discovery and connect to JCP");
        try {
            boolean startLocal = ((JSLSettings_002) settings).getLocalEnabled();       // ToDo: move getLocalEnabled to JSL.Settings
            log.info(Mrk_JSL.JSL_MAIN, String.format("JSLCommunication local communication %s", startLocal ? "enabled" : "disabled"));
            if (startLocal)
                comm.startLocal();
        } catch (JSLCommunication.LocalCommunicationException e) {
            log.warn(Mrk_JSL.JSL_MAIN, String.format("Error on starting local communication of '%s' service because %s", srvInfo.getSrvId(), e.getMessage()), e);
        }
        try {
            boolean startCloud = ((JSLSettings_002) settings).getCloudEnabled();       // ToDo: move getCloudEnabled to JSL.Settings
            log.info(Mrk_JSL.JSL_MAIN, String.format("JSLCommunication cloud communication %s", startCloud ? "enabled" : "disabled"));
            if (startCloud)
                comm.connectCloud();
        } catch (JSLCommunication.CloudCommunicationException e) {
            log.warn(Mrk_JSL.JSL_MAIN, String.format("Error on connecting cloud communication of '%s' service because %s", srvInfo.getSrvId(), e.getMessage()), e);
        }

        if (status != Status.RECONNECTING) {
            if (comm.isLocalRunning() || comm.isCloudConnected())
                status = Status.CONNECTED;
            else
                status = Status.DISCONNECTED;
            log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL set status %s", status()));
        }

        log.info(Mrk_JSL.JSL_MAIN, "JSL Srv is started");
        log.info(Mrk_JSL.JSL_MAIN, String.format("    JSL Srv status           = %s", status()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("    JSL Srv version          = %s", version()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    JOSP JOD supported       = %s", Arrays.asList(versionsJOSPObject())));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    JOSP protocol supported  = %s", Arrays.asList(versionsJOSPProtocol())));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    Cloud comm.              = %s", comm.isCloudConnected()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    Local comm.              = %s", comm.isLocalRunning()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    JCP APIs supported       = %s", Arrays.asList(versionsJCPAPIs())));
        log.info(Mrk_JSL.JSL_MAIN, String.format("    JSL Srv id               = %s", srvInfo.getSrvId()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("    JSL Srv name             = %s", srvInfo.getSrvName()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() throws ConnectException {
        log.info(Mrk_JSL.JSL_MAIN, String.format("Disconnect JSL instance for '%s' service", srvInfo.getSrvId()));


        log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL status is %s", status()));
        if (status() != Status.CONNECTED
                && status() != Status.RECONNECTING)
            throw new ConnectException(String.format("Can't disconnect JSL service '%s' because his state is '%s'.", srvInfo.getSrvId(), status()));

        if (status != Status.RECONNECTING) {
            status = Status.DISCONNECTING;
            log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL set status %s", Status.DISCONNECTING));
        }

        log.trace(Mrk_JSL.JSL_MAIN, "JSLCommunication stop discovery and disconnect from JCP");
        try {
            comm.stopLocal();

        } catch (JSLCommunication.LocalCommunicationException e) {
            log.warn(Mrk_JSL.JSL_MAIN, String.format("Error on stopping local communication service '%s''s objects discovery because %s", srvInfo.getSrvId(), e.getMessage()), e);
        }
        comm.disconnectCloud();

        if (status != Status.RECONNECTING) {
            if (comm.isLocalRunning() || comm.isCloudConnected())
                status = Status.CONNECTED;
            else
                status = Status.DISCONNECTED;
            log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL set status %s", status()));
        }

        log.info(Mrk_JSL.JSL_MAIN, String.format("JSL Service '%s' disconnected", srvInfo.getSrvId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean reconnect() throws ConnectException {
        status = Status.RECONNECTING;
        log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL set status %s", Status.RECONNECTING));

        log.trace(Mrk_JSL.JSL_MAIN, "JSL disconnect for reconnecting");
        disconnect();

        log.trace(Mrk_JSL.JSL_MAIN, "JSL start for rebooting");
        connect();

        status = Status.CONNECTED;
        log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL set status %s", Status.CONNECTED));
        return status == Status.CONNECTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Status status() {
        return status;
    }


    // JSL Systems

    /**
     * {@inheritDoc}
     */
    @Override
    public JCPClient_Service getJCPClient() {
        return jcpClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLServiceInfo getServiceInfo() {
        return srvInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLUserMngr getUserMngr() {
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLObjsMngr getObjsMngr() {
        return objs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLCommunication getCommunication() {
        return comm;
    }

}

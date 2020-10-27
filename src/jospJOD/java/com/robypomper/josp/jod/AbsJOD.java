/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jod;

import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.events.Events;
import com.robypomper.josp.jod.events.JODEvents;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * Default {@link JOD} implementation, initialization excluded.
 * <p>
 * This class fully manage a JOD object (start,stop,status...) for all JOD systems.
 * But JOD systems initialization is delegate to his sub-classes. That allow to
 * initialize multiple JOD objects using different systems implementations.
 * Helping provide new JOD versions and flavours.
 * <p>
 * The JOD and {@link AbsJOD} hierarchy is design to allow sub-classes to initialize
 * JOD systems (structure, comm, executor...) and delegate JOD systems orchestration
 * to AbsJOD class. AbsJOD class manage JOD system using only their interfaces,
 * that make system implementation completely interoperable (at JOD level). So
 * AbsJOD sub-classes (like {@link JOD_002} can switch to different systems
 * implementations/versions keeping full compatibility with all others JOD
 * systems.
 * <p>
 * All AbsJOD sub-classes must implement the <code>instance(...)</code> method
 * and return a self instance. <code>instance(...)</code> method can be
 * implemented using {@link JOD.Settings} param or his sub-class.
 * Returned class from {@link FactoryJOD#getJODClass(String)} must implement a
 * <code>instance(...)</code> method with a param corresponding to class returned
 * by {@link FactoryJOD#getJODSettingsClass(String)}. Both method are called using
 * same String param corresponding to JOD version
 */
@SuppressWarnings("JavadocReference")
public abstract class AbsJOD implements JOD {

    // Private systems references

    private final JOD.Settings settings;
    private final JCPClient_Object jcpClient;
    private final JODObjectInfo objInfo;
    private final JODStructure structure;
    private final JODCommunication comm;
    private final JODExecutorMngr executor;
    private final JODPermissions permissions;
    private final JODEvents events;


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private Status status = Status.STOPPED;


    // Constructor

    /**
     * Default constructor, set all private systems references.
     *
     * @param settings    instance containing current JOD configs.
     * @param jcpClient   instance of JCP client for objects.
     * @param objInfo     {@link JODObjectInfo} reference.
     * @param structure   {@link JODStructure} reference.
     * @param comm        {@link JODCommunication} reference.
     * @param executor    {@link JODExecutorMngr} reference.
     * @param permissions {@link JODPermissions} reference.
     * @param events       {@link JODEvents} reference.
     */
    protected AbsJOD(Settings settings, JCPClient_Object jcpClient, JODObjectInfo objInfo, JODStructure structure, JODCommunication comm, JODExecutorMngr executor, JODPermissions permissions, JODEvents events) {
        this.settings = settings;
        this.jcpClient = jcpClient;
        this.objInfo = objInfo;
        this.structure = structure;
        this.comm = comm;
        this.executor = executor;
        this.permissions = permissions;
        this.events = events;

        log.info(Mrk_JOD.JOD_MAIN, String.format("Initialized AbsJOD/%s instance for '%s' ('%s') object", this.getClass().getSimpleName(), objInfo.getObjName(), objInfo.getObjId()));
    }


    // JOD mngm

    /**
     * {@inheritDoc}
     * <p>
     * Activate autoRefresh from {@link JODObjectInfo}, {@link JODPermissions}
     * and {@link JODStructure} systems. Then activate the firmware's
     * interfaces {@link com.robypomper.josp.jod.executor.JODPuller},
     * {@link com.robypomper.josp.jod.executor.JODListener} and
     * {@link com.robypomper.josp.jod.executor.JODExecutor}. Finally it start
     * local and cloud communication.
     *
     * @throws RunException throw if current JOD object is already running.
     */
    @Override
    public void start() throws RunException {
        log.info(Mrk_JOD.JOD_MAIN, String.format("Start JOD instance for '%s' object", objInfo.getObjId()));

        long start = new Date().getTime();

        log.trace(Mrk_JOD.JOD_MAIN, String.format("JOD status is %s", status()));
        if (status() != Status.STOPPED
                && status() != Status.REBOOTING)
            throw new RunException(String.format("Can't start JOD object because his state is '%s'.", status()));

        if (status != Status.REBOOTING) {
            status = Status.STARTING;
            log.trace(Mrk_JOD.JOD_MAIN, String.format("JOD set status %s", Status.STARTING));
        }

        Events.registerJODStart("Startup sub-system");

        log.trace(Mrk_JOD.JOD_MAIN, "JODEvents starting");
        events.startCloudSync();

        log.trace(Mrk_JOD.JOD_MAIN, "JODObjectInfo starting");
        objInfo.startAutoRefresh();

        log.trace(Mrk_JOD.JOD_MAIN, "JODPermissions starting");
        permissions.startAutoRefresh();

        log.trace(Mrk_JOD.JOD_MAIN, "JODStructure starting");
        structure.startAutoRefresh();

        log.trace(Mrk_JOD.JOD_MAIN, "JODExecutor enable all workers");
        executor.activateAll();

        log.trace(Mrk_JOD.JOD_MAIN, "JODCommunication start server and connect to JCP");
        try {
            boolean startLocal = ((JODSettings_002) settings).getLocalEnabled();       // ToDo: move getLocalEnabled to JOD.Settings
            log.info(Mrk_JOD.JOD_MAIN, String.format("JODCommunication local communication %s", startLocal ? "enabled" : "disabled"));
            if (startLocal)
                comm.startLocal();
        } catch (JODCommunication.LocalCommunicationException e) {
            log.warn(Mrk_JOD.JOD_MAIN, String.format("Error on starting local communication of '%s' object because %s", objInfo.getObjId(), e.getMessage()), e);
        }
        try {
            boolean startCloud = ((JODSettings_002) settings).getCloudEnabled();       // ToDo: move getCloudEnabled to JOD.Settings
            log.info(Mrk_JOD.JOD_MAIN, String.format("JODCommunication cloud communication %s", startCloud ? "enabled" : "disabled"));
            if (startCloud)
                comm.connectCloud();
        } catch (JODCommunication.CloudCommunicationException e) {
            log.warn(Mrk_JOD.JOD_MAIN, String.format("Error on connecting cloud communication of '%s' object because %s", objInfo.getObjId(), e.getMessage()), e);
        }

        if (status != Status.REBOOTING) {
            status = Status.RUNNING;
            log.trace(Mrk_JOD.JOD_MAIN, String.format("JOD set status %s", Status.RUNNING));
        }

        long time = new Date().getTime() - start;
        Events.registerJODStart("Sub-systems started successfully", time);

        log.info(Mrk_JOD.JOD_MAIN, String.format("JOD Object '%s' started", objInfo.getObjId()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    JOD Obj status           = %s", status()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    JOSP JOD version         = %s", version()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    JOSP protocol supported  = %s", Arrays.asList(versionsJOSPProtocol())));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    JCP APIs supported       = %s", Arrays.asList(versionsJCPAPIs())));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    Cloud comm.              = %s", comm.isCloudConnected()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    Local comm.              = %s", comm.isLocalRunning()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    JOD Obj id               = %s", objInfo.getObjId()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    JOD Obj name             = %s", objInfo.getObjName()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("    JOD Obj owner id         = %s", objInfo.getOwnerId()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Stop local and cloud communication. Then deactivate the firmware's
     * interfaces {@link com.robypomper.josp.jod.executor.JODPuller},
     * {@link com.robypomper.josp.jod.executor.JODListener} and
     * {@link com.robypomper.josp.jod.executor.JODExecutor}. Finally deactivate
     * autoRefresh from {@link JODObjectInfo}, {@link JODPermissions}
     * and {@link JODStructure} systems
     *
     * @throws RunException throw if current JOD object is already stopped.
     */
    @Override
    public void stop() throws RunException {
        log.info(Mrk_JOD.JOD_MAIN, String.format("Shutdown JOD instance for '%s' object", objInfo.getObjId()));

        long start = new Date().getTime();

        log.trace(Mrk_JOD.JOD_MAIN, String.format("JOD status is %s", status()));
        if (status() != Status.RUNNING
                && status() != Status.REBOOTING)
            throw new RunException(String.format("Can't stop JOD object because his state is '%s'.", status()));

        Events.registerJODStop("Stopping sub-system");

        if (status != Status.REBOOTING) {
            status = Status.SHUTDOWN;
            log.trace(Mrk_JOD.JOD_MAIN, String.format("JOD set status %s", Status.SHUTDOWN));
        }

        log.trace(Mrk_JOD.JOD_MAIN, "JODCommunication stop server and disconnect from JCP");
        try {
            comm.stopLocal();

        } catch (JODCommunication.LocalCommunicationException e) {
            log.warn(Mrk_JOD.JOD_MAIN, String.format("Error on hiding local communication object's server '%s' because %s", objInfo.getObjId(), e.getMessage()), e);
        }
        comm.disconnectCloud();

        log.trace(Mrk_JOD.JOD_MAIN, "JODExecutor disable all workers");
        executor.deactivateAll();

        log.trace(Mrk_JOD.JOD_MAIN, "JODObjectInfo stopping");
        objInfo.stopAutoRefresh();

        log.trace(Mrk_JOD.JOD_MAIN, "JODPermission stopping");
        permissions.stopAutoRefresh();

        log.trace(Mrk_JOD.JOD_MAIN, "JODStructure stopping");
        structure.stopAutoRefresh();

        long time = new Date().getTime() - start;
        Events.registerJODStop("Sub-system stopped successfully", time);
        try {
            Events.storeCache();
        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_MAIN, "Can't flush events on file");
        }
        log.trace(Mrk_JOD.JOD_MAIN, "JODEvents stopping");
        events.stopCloudSync();

        if (status != Status.REBOOTING) {
            status = Status.STOPPED;
            log.trace(Mrk_JOD.JOD_MAIN, String.format("JOD set status %s", Status.STOPPED));
        }

        log.info(Mrk_JOD.JOD_MAIN, String.format("JOD Object '%s' stopped", objInfo.getObjId()));
    }

    /**
     * {@inheritDoc}
     *
     * @throws RunException thrown if errors occurs on JOD object stop and start.
     */
    @Override
    public void restart() throws RunException {
        status = Status.REBOOTING;
        log.trace(Mrk_JOD.JOD_MAIN, String.format("JOD set status %s", Status.REBOOTING));

        log.trace(Mrk_JOD.JOD_MAIN, "JOD stop for rebooting");
        stop();

        log.trace(Mrk_JOD.JOD_MAIN, "JOD start for rebooting");
        start();

        status = Status.RUNNING;
        log.trace(Mrk_JOD.JOD_MAIN, String.format("JOD set status %s", Status.RUNNING));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Status status() {
        return status;
    }


    // JOD Systems

    @Override
    public JCPClient_Object getJCPClient() {
        return jcpClient;
    }

    @Override
    public JODObjectInfo getObjectInfo() {
        return objInfo;
    }

    @Override
    public JODStructure getObjectStructure() {
        return structure;
    }

    @Override
    public JODCommunication getCommunication() {
        return comm;
    }

    @Override
    public JODExecutorMngr getExecutor() {
        return executor;
    }

    @Override
    public JODPermissions getPermission() {
        return permissions;
    }

}

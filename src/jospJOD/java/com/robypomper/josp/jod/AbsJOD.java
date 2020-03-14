package com.robypomper.josp.jod;

import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.systems.JODCommunication;
import com.robypomper.josp.jod.systems.JODExecutorMngr;
import com.robypomper.josp.jod.systems.JODObjectInfo;
import com.robypomper.josp.jod.systems.JODPermissions;
import com.robypomper.josp.jod.systems.JODStructure;

/**
 * Default JOD implementation, initialization excluded.
 *
 * This class fully manage a JOD object (start,stop,status...) for all JOD systems.
 * But JOD systems initialization is delegate to his sub-classes. That allow to
 * implement multiple JOD objects using different systems implementations. Helping
 * develop new JOD versions and flavours.
 *
 * The JOD and {@link AbsJOD} hierarchy is design to allow sub-classes to initialize
 * JOD systems (structure, comm, executor...) and delegate JOD systems orchestration
 * to AbsJOD class. AbsJOD class manage JOD system using only their interfaces,
 * that make system implementation completely interoperable (at JOD level). So
 * AbsJOD sub-classes (like {@link JOD_002} can switch to different systems
 * implementations/versions keeping full compatibility with all others of JOD
 * systems.
 *
 * All AbsJOD sub-classes must implement the <code>instance(...)</code> method
 * and return a self instance. <code>instance(...)</code> method can be
 * implemented using {@link JOD.Settings} param or his sub-class.
 * Returned class from {@link FactoryJOD#getJODClass(String)} must implement a
 * <code>instance(...)</code> method with a param corresponding to class returned
 * by {@link FactoryJOD#getJODSettingsClass(String)}. Both method are call using
 * same String param corresponding to
 */
public abstract class AbsJOD implements JOD {

    // Private systems references

    private final JOD.Settings settings;
    private final JCPClient_Object jcpClient;
    private final JODObjectInfo objInfo;
    private final JODStructure structure;
    private final JODCommunication comm;
    private final JODExecutorMngr executor;
    private final JODPermissions permissions;


    // Internal vars

    private Status status = Status.STOPPED;


    // Constructor

    /**
     * Default constructor, set all private systems references.
     *
     * @param settings    object containing current JOD configs.
     * @param objInfo     {@link JODObjectInfo} reference.
     * @param structure   {@link JODStructure} reference.
     * @param comm        {@link JODCommunication} reference.
     * @param executor    {@link JODExecutorMngr} reference.
     * @param permissions {@link JODPermissions} reference.
     */
    protected AbsJOD(Settings settings, JCPClient_Object jcpClient, JODObjectInfo objInfo, JODStructure structure, JODCommunication comm, JODExecutorMngr executor, JODPermissions permissions) {
        this.settings = settings;
        this.jcpClient = jcpClient;
        this.objInfo = objInfo;
        this.structure = structure;
        this.comm = comm;
        this.executor = executor;
        this.permissions = permissions;
    }


    // JOD mngm

    /**
     * {@inheritDoc}
     *
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
        System.out.println("INF: JOD Obj startup...");

        if (status()!=Status.STOPPED)
            throw new RunException(String.format("Can't start JOD object because his state is '%s'.", status()));

        if (status!=Status.REBOOTING) status = Status.STARTING;

        // Info auto refresh
        objInfo.startAutoRefresh();
        permissions.startAutoRefresh();
        structure.startAutoRefresh();

        // HW interface
        executor.activateAll();

        // Service comm
        comm.startCloudComm();
        comm.startLocalComm();

        if (status != Status.REBOOTING) status = Status.RUNNING;
        System.out.println("INF: JOD Obj is running.");
        System.out.println(String.format("INF: JOD Obj version          = %s", version()));
        System.out.println(String.format("INF: JOD Obj settings version = %s", settings.version()));
        System.out.println(String.format("INF: JOD Obj id               = %s", objInfo.getObjId()));
        System.out.println(String.format("INF: JOD Obj name             = %s", objInfo.getObjName()));
        //System.out.println(String.format("INF: JOD Obj structure model = %s", structure.getRoot()...));
    }

    /**
     * {@inheritDoc}
     *
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
        System.out.println("INF: JOD Obj shutdown...");

        if (status()!=Status.RUNNING)
            throw new RunException(String.format("Can't stop JOD object because his state is '%s'.", status()));

        if (status!=Status.REBOOTING) status = Status.SHUTDOWN;

        // Service comm
        comm.stopCloudComm();
        comm.stopLocalComm();

        // HW interface
        executor.deactivateAll();

        // Info auto refresh
        objInfo.stopAutoRefresh();
        permissions.stopAutoRefresh();
        structure.stopAutoRefresh();

        if (status!=Status.REBOOTING) status = Status.STOPPED;
        System.out.println("INF: JOD Obj stopped.");
    }

    /**
     * {@inheritDoc}
     *
     * @throws RunException thrown if errors occurs on JOD object stop and start.
     */
    @Override
    public void restart() throws RunException {
        status = Status.REBOOTING;
        stop();
        start();
        status = Status.RUNNING;
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

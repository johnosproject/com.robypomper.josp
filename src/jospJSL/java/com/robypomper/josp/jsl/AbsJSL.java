package com.robypomper.josp.jsl;

import com.robypomper.josp.jsl.comm.JSLLocalConnection;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.systems.JSLCommunication;
import com.robypomper.josp.jsl.systems.JSLObjsMngr;
import com.robypomper.josp.jsl.systems.JSLServiceInfo;
import com.robypomper.josp.jsl.systems.JSLUserMngr;

import java.util.List;


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
 * by {@link FactoryJOD#getJODSettingsClass(String)}. Both method are called using
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
    }


    // JSL mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean connect() {
        status = Status.CONNECTING;

        // Start local discovery
        try {
            comm.startSearchLocalObjects();

        } catch (Exception e) {
            System.out.println(String.format("WAR: can't start local objects search because %s", e.getMessage()));
            e.printStackTrace();
        }

        // Connect Gw S2O
        try {
            comm.connectCloud();

        } catch (Exception e) {
            System.out.println(String.format("WAR: can't connect to cloud Gw S2O because %s", e.getMessage()));
            e.printStackTrace();
        }

        // Update JSL status
        if (comm.isLocalSearchActive() || comm.isCloudConnected())
            status = Status.CONNECTED;
        else
            status = Status.DISCONNECTED;

        return status == Status.CONNECTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean disconnect() {
        status = Status.DISCONNECTING;

        // Stop local discovery
        try {
            comm.stopSearchLocalObjects();

        } catch (Exception e) {
            System.out.println(String.format("WAR: can't start local objects search because %s", e.getMessage()));
            e.printStackTrace();
        }

        // Close all local connections
        List<JSLLocalConnection> locConns = comm.getAllLocalConnection();
        for (JSLLocalConnection conn : locConns) {
            conn.disconnect();
        }

        // Disconnect Gw S2O
        try {
            comm.disconnectCloud();

        } catch (Exception e) {
            System.out.println(String.format("WAR: can't connect to cloud Gw S2O because %s", e.getMessage()));
            e.printStackTrace();
        }

        // Update JSL status
        if (!comm.isLocalSearchActive() && !comm.isCloudConnected())
            status = Status.DISCONNECTED;
        else
            status = Status.CONNECTED;

        return status == Status.DISCONNECTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean reconnect() {
        disconnect();
        connect();

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

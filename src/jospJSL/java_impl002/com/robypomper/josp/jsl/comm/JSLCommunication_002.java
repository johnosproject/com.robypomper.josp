package com.robypomper.josp.jsl.comm;

import com.robypomper.communication.client.Client;
import com.robypomper.discovery.Discover;
import com.robypomper.discovery.DiscoverListener;
import com.robypomper.discovery.DiscoverySystemFactory;
import com.robypomper.discovery.impl.DiscoveryJmDNS;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.AbsJSLState;
import com.robypomper.josp.jsl.objs.structure.DefaultJSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLState;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_CloudRequests;
import com.robypomper.log.Mrk_JOD;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Implementation of the {@link JSLCommunication} interface.
 */
public class JSLCommunication_002 implements JSLCommunication, DiscoverListener {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLSettings_002 locSettings;
    private final JSLServiceInfo srvInfo;
    private final JSLUserMngr usr;
    private final JSLObjsMngr objs;
    private final JCPClient_Service jcpClient;
    private final JCPCommSrv jcpComm;
    private final String instanceId;
    private final Discover localServerDiscovery;
    private final List<JSLLocalClient> localServers = new ArrayList<>();
    private JSLGwS2OClient gwClient;


    // Constructor

    /**
     * @param settings  the JSL settings.
     * @param srvInfo   the service's info.
     * @param jcpClient the jcp service client.
     * @param usr       the {@link JSLUserMngr} instance used get user info.
     * @param objs      the {@link JSLObjsMngr} instance used to update component
     *                  status.
     */
    public JSLCommunication_002(JSLSettings_002 settings, JSLServiceInfo srvInfo, JCPClient_Service jcpClient, JSLUserMngr usr, JSLObjsMngr objs, String instanceId) throws LocalCommunicationException, CloudCommunicationException {
        this.locSettings = settings;
        this.srvInfo = srvInfo;
        this.jcpClient = jcpClient;
        this.jcpComm = new JCPCommSrv(jcpClient, settings, instanceId);
        this.usr = usr;
        this.objs = objs;
        this.instanceId = instanceId;

        // Init local service client and discovery
        String discoveryImpl = locSettings.getJSLDiscovery();
        try {
            log.debug(Mrk_JSL.JSL_COMM, String.format("Creating discovery '%s' service for local object's servers", discoveryImpl));
            // If required JmDNS implementation, start his sub-system
            if (DiscoveryJmDNS.IMPL_NAME.equalsIgnoreCase(discoveryImpl)) {
                log.trace(Mrk_JSL.JSL_COMM, "Communication initializing discovery subsystem on JmDNS");
                DiscoveryJmDNS.startJmDNSSubSystem();
            }
            localServerDiscovery = DiscoverySystemFactory.createDiscover(discoveryImpl, JOSPProtocol.DISCOVERY_TYPE);
            log.debug(Mrk_JSL.JSL_COMM, String.format("Discovery '%s' service created for local object's servers", discoveryImpl));

        } catch (Discover.DiscoveryException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on creating discovery '%s' service for local object's servers because %s", discoveryImpl, e.getMessage()), e);
            throw new LocalCommunicationException(String.format("Error on creating discovery '%s' service for local object's servers", discoveryImpl), e);
        }

        // Init cloud service client
        try {
            log.debug(Mrk_JOD.JOD_COMM, "Creating communication cloud client for Object2Service Gateway");
            gwClient = new JSLGwS2OClient(locSettings, this, srvInfo, jcpClient, jcpComm);
            log.debug(Mrk_JOD.JOD_COMM, "Communication cloud client created for Object2Service Gateway");
        } catch (CloudCommunicationException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on creating service's cloud client because %s", e.getMessage()), e);
            throw new CloudCommunicationException("Error on creating service's cloud client", e);
        }

        log.info(Mrk_JSL.JSL_COMM, String.format("Initialized JODCommunication instance for '%s' ('%s') service", srvInfo.getSrvName(), srvInfo.getSrvId()));
    }


    // Status upd flow (comm - objMng)

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean forwardUpdate(String msg) {
        if (!JOSPProtocol.isUpdMsg(msg))
            return false;

        // parse received data
        JOSPProtocol.StatusUpd upd;
        try {
            upd = JOSPProtocol.fromMsgToUpd(msg, AbsJSLState.getStateClasses());
        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on parsing update '%s...' because %s", msg.substring(0, msg.indexOf("\n")), e.getMessage()), e);
            return false;
        }

        JSLRemoteObject obj = objs.getById(upd.getObjectId());
        log.debug(Mrk_JSL.JSL_COMM, String.format("Processing update '%s...' for '%s' object", msg.substring(0, Math.min(10, msg.length())), obj.getId()));

        // search destination object/components
        JSLComponentPath compPath = new DefaultJSLComponentPath(upd.getComponentPath());
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), compPath);

        // forward update msg
        log.trace(Mrk_JSL.JSL_COMM, String.format("Processing update on '%s' component for '%s' object", compPath.getString(), obj.getId()));
        if (comp == null) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on processing update on '%s' component for '%s' object because component not found", compPath.getString(), obj.getId()));
            return false;
        }
        if (!(comp instanceof JSLState)) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on processing update on '%s' component for '%s' object because component not a status component", compPath.getString(), obj.getId()));
            return false;
        }
        JSLState stateComp = (JSLState) comp;

        // set object/component's update
        if (stateComp.updateStatus(upd)) {
            log.info(Mrk_JSL.JSL_COMM, String.format("Updated status of '%s' component for '%s' object", compPath.getString(), obj.getId()));

        } else {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on processing update on '%s' component for '%s' object", compPath.getString(), obj.getId()));
            return false;
        }

        log.debug(Mrk_JSL.JSL_COMM, String.format("Update '%s...' processed for '%s' object", msg.substring(0, Math.min(10, msg.length())), obj.getId()));
        return true;
    }


    // Cloud requests

    /**
     * {@inheritDoc}
     */
    @Override
    public String processCloudData(String msg) {
        log.trace(Mrk_JOD.JOD_COMM, "Process cloud data");

        String response = null;

        // Message 2 Remote Obj
        if (JOSPProtocol_CloudRequests.isMsgToObject(msg)) {
            String objId;
            try {
                objId = JOSPProtocol_CloudRequests.extractObjectIdFromResponse(msg);

            } catch (JOSPProtocol.ParsingException e) {
                log.warn(Mrk_JSL.JSL_COMM, String.format("Error on process cloud data because can't extract object's id (%s)", e.getMessage()), e);
                return null;        // No response (error)
            }

            if (objs.getById(objId) == null)
                objs.addCloudObject(objId);
            objs.getById(objId).processCloudData(msg);

            return null;        // No response
        }

        return response;
    }


    // Connections access

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLGwS2OClient getCloudConnection() {
        return gwClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLLocalClient> getAllLocalServers() {
        return Collections.unmodifiableList(localServers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeServer(JSLLocalClient server) {
        localServers.remove(server);
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLocalRunning() {
        return localServerDiscovery.isRunning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startLocal() throws LocalCommunicationException {
        log.info(Mrk_JSL.JSL_COMM, String.format("Start local service's discovery '%s'", srvInfo.getSrvId()));

        if (isLocalRunning())
            return;

//        String discoveryImpl = locSettings.getJSLDiscovery();
        try {
//            localServerDiscovery = DiscoverySystemFactory.createDiscover(discoveryImpl, JOSPProtocol.DISCOVERY_TYPE);
            localServerDiscovery.addListener(this);
            log.debug(Mrk_JSL.JSL_COMM, "Starting local service's discovery");
            localServerDiscovery.start();
            log.debug(Mrk_JSL.JSL_COMM, "Local service's discovery started");

        } catch (Discover.DiscoveryException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on starting local communication service's discovery '%s' because %s", srvInfo.getSrvId(), e.getMessage()), e);
            throw new LocalCommunicationException(String.format("Error on starting local communication service's discovery '%s'", srvInfo.getSrvId()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopLocal() throws LocalCommunicationException {
        log.info(Mrk_JSL.JSL_COMM, String.format("Stop local communication service's discovery '%s' and disconnect local clients", srvInfo.getSrvId()));

        if (!isLocalRunning())
            return;

        try {
            log.debug(Mrk_JSL.JSL_COMM, "Stopping local service's discovery");
            localServerDiscovery.stop();
            log.debug(Mrk_JSL.JSL_COMM, "Local service's discovery stopped");
            localServerDiscovery.removeListener(this);

        } catch (Discover.DiscoveryException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on stopping local communication service's discovery '%s' because %s", srvInfo.getSrvId(), e.getMessage()), e);
            throw new LocalCommunicationException(String.format("Error on stopping local communication service's discovery '%s' system '%s'", srvInfo.getSrvId(), locSettings.getJSLDiscovery()), e);
        }

        log.debug(Mrk_JSL.JSL_COMM, "Disconnecting local communication service's clients");
        List<JSLLocalClient> connections = new ArrayList<>(localServers);
        for (JSLLocalClient locConn : connections)
            if (locConn.isConnected())
                locConn.disconnect();
        log.debug(Mrk_JSL.JSL_COMM, "Local communication service's clients disconnected");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCloudConnected() {
        return gwClient.isConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectCloud() throws CloudCommunicationException {
        log.info(Mrk_JSL.JSL_COMM, String.format("Connect cloud communication client for '%s' service", srvInfo.getSrvId()));

        if (isCloudConnected())
            return;

        try {
            log.debug(Mrk_JSL.JSL_COMM, "Connecting cloud service's client");
            gwClient = new JSLGwS2OClient(locSettings, this, srvInfo, jcpClient, jcpComm);
            gwClient.connect();
            if (gwClient.isConnected())
                log.debug(Mrk_JSL.JSL_COMM, "Cloud service's client connected");
            else
                log.warn(Mrk_JSL.JSL_COMM, "Cloud service's client NOT connected");

        } catch (Client.ConnectionException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on connecting cloud communication client because %s", e.getMessage()), e);
            throw new CloudCommunicationException("error on cloud connection", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectCloud() {
        log.info(Mrk_JSL.JSL_COMM, String.format("Disconnect cloud communication client for '%s' service", srvInfo.getSrvId()));

        if (!isCloudConnected())
            return;

        gwClient.disconnect();
    }


    // Local communication discovery

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceDiscovered(String type, String name, InetAddress address, int port, String extraText) {
        Thread.currentThread().setName("JSLDiscovery");
        log.info(Mrk_JSL.JSL_COMM, String.format("Discover object's service '%s' at '%s:%d' by '%s' service", name, address, port, srvInfo.getSrvId()));

        if (address instanceof Inet6Address) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Object's service '%s' at '%s:%d' use IPv6 then discarded", name, address, port));
            return;
        }

        JSLLocalClient locConn;
        try {
            log.debug(Mrk_JSL.JSL_COMM, String.format("Connecting to '%s' object on server '%s:%d' from '%s' service", name, address, port, srvInfo.getSrvId()));
            String clientPubCertFile = File.createTempFile(String.format("jslCert-%s-%d", address, port), ".crt").getAbsolutePath();
            log.trace(Mrk_JSL.JSL_COMM, String.format("Local service's client use public certificate file '%s'", clientPubCertFile));
            locConn = new JSLLocalClient(this, srvInfo.getFullId(), address, port, clientPubCertFile);
            locConn.connect();
            log.debug(Mrk_JSL.JSL_COMM, String.format("Service connected to '%s' object on server '%s:%d' from '%s' service", name, address, port, srvInfo.getSrvId()));

        } catch (IOException | Client.ConnectionException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on connecting to '%s' object on server '%s:%d' from '%s' service because %s", name, address, port, srvInfo.getSrvId(), e.getMessage()), e);
            return;
        }

        localServers.add(locConn);
        objs.addNewConnection(locConn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceLost(String type, String name) {}

}

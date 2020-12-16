package com.robypomper.josp.jod.events;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jod.comm.JODGwO2SClient;
import com.robypomper.josp.jod.comm.JODLocalClientInfo;
import com.robypomper.josp.jod.comm.JODLocalServer;
import com.robypomper.josp.jod.structure.AbsJODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.protocol.JOSPEvent;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;

import java.io.IOException;
import java.util.List;

public class Events {

    private static JODEvents instance;

    public static void setInstance(JODEvents events) {
        if (instance!=null)
            throw new RuntimeException("Events's instance already set, cant' be set twice.");
        if (events==null)
            throw new IllegalArgumentException("Events's instance can't be null.");

        instance = events;
    }

    public static void storeCache() throws IOException {
        if (instance!=null)
            instance.storeCache();
    }

    public static void register(JOSPEvent.Type type, String phase, String payload) {
        if (instance!=null)
            instance.register(type,phase,payload);
    }

    public static void register(JOSPEvent.Type type, String phase, String payload, Throwable t) {
        if (instance!=null)
            instance.register(type,phase,payload,t);
    }


    // JOD_Start,
    // JOD_Stop,

    public static void registerJODStart(String phase) {
        String payload = "";
        register(JOSPEvent.Type.JOD_START,phase,payload);
    }

    public static void registerJODStart(String phase, long time) {
        String payload = String.format("{\"time\": \"%d\"}", time);
        register(JOSPEvent.Type.JOD_START,phase,payload);
    }

    public static void registerJODStart(String phase, String instanceId) {
        String payload = String.format("{\"instanceId\": \"%s\"}", instanceId);
        register(JOSPEvent.Type.JOD_START,phase,payload);
    }

    public static void registerJODStop(String phase) {
        String payload = "";
        register(JOSPEvent.Type.JOD_STOP,phase,payload);
    }

    public static void registerJODStop(String phase, long time) {
        String payload = String.format("{\"time\": \"%d\"}", time);
        register(JOSPEvent.Type.JOD_STOP,phase,payload);
    }


    // JOD_COMM_JCP_CONN,
    // JOD_COMM_JCP_DISC,
    /*
    JOD_COMM_JCP_OFFLINE,
     */

    public static void registerJCPConnection(String phase, JCPClient2 jcpClient) {
        registerJCPConnection(phase,jcpClient, null, null);
    }

    public static void registerJCPConnection(String phase, JCPClient2 jcpClient, Throwable t) {
        registerJCPConnection(phase,jcpClient, null, t);
    }

    public static void registerJCPConnection(String phase, JCPClient2 jcpClient, String flow) {
        registerJCPConnection(phase,jcpClient,flow, null);
    }

    public static void registerJCPConnection(String phase, JCPClient2 jcpClient, String flow, Throwable t) {
        String payload;
        payload = "{";
        payload += String.format("\"urlAPIs\": \"%s\"", jcpClient.getUrlAPIs());
        payload += String.format(", \"ipAPIs\": \"%s\"", jcpClient.getIPAPIs());
        payload += String.format(", \"urlAuth\": \"%s\"", jcpClient.getUrlAuth());
        payload += String.format(", \"ipAuth\": \"%s\"", jcpClient.getIPAuth());
        if (flow!=null)
            payload += String.format(", \"flow\": \"%s\"", flow);
        payload += "}";
        register(JOSPEvent.Type.JOD_COMM_JCP_CONN,phase,payload,t);
    }

    public static void registerJCPDisconnection(String phase, JCPClient2 jcpClient, String flow) {
        String payload;
        payload = "{";
        payload += String.format("\"urlAPIs\": \"%s\"", jcpClient.getUrlAPIs());
        payload += String.format(", \"ipAPIs\": \"%s\"", jcpClient.getIPAPIs());
        payload += String.format(", \"urlAuth\": \"%s\"", jcpClient.getUrlAuth());
        payload += String.format(", \"ipAuth\": \"%s\"", jcpClient.getIPAuth());
        if (flow!=null)
            payload += String.format(", \"flow\": \"%s\"", flow);
        payload += "}";
        register(JOSPEvent.Type.JOD_COMM_JCP_DISC,phase,payload);
    }


    // JOD_COMM_CLOUD_CONN,
    // JOD_COMM_CLOUD_DISC,

    public static void registerCloudConnect(String phase, JODGwO2SClient gwClient) {
        registerCloudConnect(phase,gwClient, null);
    }

    public static void registerCloudConnect(String phase, JODGwO2SClient gwClient, Throwable t) {
        String payload;
        payload = "{";
        payload += String.format("\"connected\": \"%s\"", gwClient.isConnected());
        if (gwClient.isConnected() || gwClient.getServerAddr()!=null) {
            payload += String.format(", \"url\": \"%s\"", gwClient.getServerAddr().getHostName());
            payload += String.format(", \"ip\": \"%s\"", gwClient.getServerAddr().getHostAddress());
        }
        payload += String.format(", \"port\": \"%s\"", gwClient.getServerPort());
        if (gwClient.isConnected() || gwClient.getClientAddr()!=null) {
            payload += ", \"client\": {";
            payload += String.format("\"id\": \"%s\"", gwClient.getClientId());
            payload += String.format(", \"url\": \"%s\"", gwClient.getClientAddr().getHostName());
            payload += String.format(", \"ip\": \"%s\"", gwClient.getClientAddr().getHostAddress());
            payload += String.format(", \"port\": \"%s\"", gwClient.getClientPort());
            payload += "}";
        }
        payload += "}";
        register(JOSPEvent.Type.JOD_COMM_CLOUD_CONN,phase,payload,t);
    }

    public static void registerCloudDisconnect(String phase, JODGwO2SClient gwClient) {
        String payload;
        payload = "{";
        payload += String.format("\"connected\": \"%s\"", gwClient.isConnected());
        if (gwClient.isConnected() || gwClient.getServerAddr()!=null) {
            payload += String.format(", \"url\": \"%s\"", gwClient.getServerAddr().getHostName());
            payload += String.format(", \"ip\": \"%s\"", gwClient.getServerAddr().getHostAddress());
        }
        payload += String.format(", \"port\": \"%s\"", gwClient.getServerPort());
        if (gwClient.isConnected() || gwClient.getClientAddr()!=null) {
            payload += ", \"client\": {";
            payload += String.format("\"id\": \"%s\"", gwClient.getClientId());
            payload += String.format(", \"url\": \"%s\"", gwClient.getClientAddr().getHostName());
            payload += String.format(", \"ip\": \"%s\"", gwClient.getClientAddr().getHostAddress());
            payload += String.format(", \"port\": \"%s\"", gwClient.getClientPort());
            payload += "}";
        }
        payload += "}";
        register(JOSPEvent.Type.JOD_COMM_CLOUD_DISC,phase,payload);
    }


    // JOD_COMM_LOC_START,
    // JOD_COMM_LOC_STOP,
    // JOD_COMM_LOC_CONN,
    // JOD_COMM_LOC_DISC,

    public static void registerLocalStart(String phase, JODLocalServer localServer) {
        registerLocalStart(phase,localServer, null);
    }

    public static void registerLocalStart(String phase, JODLocalServer localServer, Throwable t) {
        String payload;
        payload = "{";
        payload += String.format("\"running\": \"%s\"", localServer.isRunning());
        if (localServer.isRunning() || localServer.getAddress()!=null) {
            payload += String.format(", \"url\": \"%s\"", localServer.getAddress().getHostName());
            payload += String.format(", \"ip\": \"%s\"", localServer.getAddress().getHostAddress());
        }
        payload += String.format(", \"port\": \"%s\"", localServer.getPort());
        payload += String.format(", \"clientsCount\": \"%s\"", localServer.getClients().size());
        payload += "}";
        register(JOSPEvent.Type.JOD_COMM_LOC_START,phase,payload,t);
    }

    public static void registerLocalStop(String phase, JODLocalServer localServer) {
        registerLocalStop(phase,localServer, null);
    }

    public static void registerLocalStop(String phase, JODLocalServer localServer, Throwable t) {
        String payload;
        payload = "{";
        payload += String.format("\"running\": \"%s\"", localServer.isRunning());
        if (localServer.isRunning() || localServer.getAddress()!=null) {
            payload += String.format(", \"url\": \"%s\"", localServer.getAddress().getHostName());
            payload += String.format(", \"ip\": \"%s\"", localServer.getAddress().getHostAddress());
        }
        payload += String.format(", \"port\": \"%s\"", localServer.getPort());
        payload += String.format(", \"clientsCount\": \"%s\"", localServer.getClients().size());
        payload += "}";
        register(JOSPEvent.Type.JOD_COMM_LOC_STOP,phase,payload,t);
    }

    public static void registerLocalConn(String phase, JODLocalClientInfo clientInfo, ClientInfo client) {
        String payload;
        payload = "{";
        payload += String.format("\"connected\": \"%s\"", clientInfo.isConnected());
        payload += String.format(", \"srvId\": \"%s\"", clientInfo.getSrvId());
        payload += String.format(", \"usrId\": \"%s\"", clientInfo.getUsrId());
        payload += String.format(", \"instanceId\": \"%s\"", clientInfo.getInstanceId());
        payload += String.format(", \"clientId\": \"%s\"", clientInfo.getClientId());

        payload += ", \"connectingCli\": {";
        payload += String.format("\"id\": \"%s\"", client.getClientId());
        payload += String.format(", \"url\": \"%s\"", client.getPeerAddress().getHostName());
        payload += String.format(", \"ip\": \"%s\"", client.getPeerAddress().getHostAddress());
        payload += String.format(", \"port\": \"%s\"", client.getPeerPort());
        payload += "}";

        if (clientInfo.isConnected() || clientInfo.getClientAddress()!=null) {
            payload += ", \"serviceCli\": {";
            payload += String.format("\"id\": \"%s\"", clientInfo.getClientId());
            payload += String.format(", \"url\": \"%s\"", clientInfo.getClientAddress().getHostName());
            payload += String.format(", \"ip\": \"%s\"", clientInfo.getClientAddress().getHostAddress());
            payload += String.format(", \"port\": \"%s\"", clientInfo.getClientPort());
            payload += "}";
        }

        payload += "}";
        register(JOSPEvent.Type.JOD_COMM_LOC_CONN,phase,payload);
    }

    public static void registerLocalDisc(String phase, JODLocalClientInfo clientInfo, ClientInfo client) {
        String payload;
        payload = "{";
        payload += String.format("\"connected\": \"%s\"", clientInfo.isConnected());
        payload += String.format(", \"srvId\": \"%s\"", clientInfo.getSrvId());
        payload += String.format(", \"usrId\": \"%s\"", clientInfo.getUsrId());
        payload += String.format(", \"instanceId\": \"%s\"", clientInfo.getInstanceId());
        payload += String.format(", \"clientId\": \"%s\"", clientInfo.getClientId());

        payload += ", \"disconnectingCli\": {";
        payload += String.format("\"id\": \"%s\"", client.getClientId());
        payload += String.format(", \"url\": \"%s\"", client.getPeerAddress().getHostName());
        payload += String.format(", \"ip\": \"%s\"", client.getPeerAddress().getHostAddress());
        payload += String.format(", \"port\": \"%s\"", client.getPeerPort());
        payload += "}";

        if (clientInfo.isConnected() || clientInfo.getClientAddress()!=null) {
            payload += ", \"serviceCli\": {";
            payload += String.format("\"id\": \"%s\"", clientInfo.getClientId());
            payload += String.format(", \"url\": \"%s\"", clientInfo.getClientAddress().getHostName());
            payload += String.format(", \"ip\": \"%s\"", clientInfo.getClientAddress().getHostAddress());
            payload += String.format(", \"port\": \"%s\"", clientInfo.getClientPort());
            payload += "}";
        }

        payload += "}";
        register(JOSPEvent.Type.JOD_COMM_LOC_DISC,phase,payload);
    }


    // JOD_PERMS_LOAD,
    // JOD_PERMS_MISSING,
    // JOD_PERMS_ADD,
    // JOD_PERMS_UPD,
    // JOD_PERMS_REM,

    public static void registerPermLoaded(String phase, List<JOSPPerm> permissions) {
        String payload;
        payload = "{";
        payload += String.format("\"count\": \"%s\"", permissions.size());
        if (permissions.size()>0)
            payload += String.format(", \"objId\": \"%s\"", permissions.get(0).getObjId());
        payload += "}";
        register(JOSPEvent.Type.JOD_PERMS_LOAD,phase,payload);
    }

    public static void registerPermLoaded(String phase, Throwable t) {
        String payload = "";
        register(JOSPEvent.Type.JOD_PERMS_LOAD,phase,payload,t);
    }

    public static void registerPermAdded(JOSPPerm newPerm) {
        String payload;
        payload = "{";
        payload += "\"add\": {";
        payload += String.format("    \"id\": \"%s\", ", newPerm.getId());
        payload += String.format("    \"srvId\": \"%s\", ", newPerm.getSrvId());
        payload += String.format("    \"usrId\": \"%s\", ", newPerm.getUsrId());
        payload += String.format("    \"connType\": \"%s\", ", newPerm.getConnType());
        payload += String.format("    \"permType\": \"%s\"", newPerm.getPermType());
        payload += "}";
        payload += "}";

        register(JOSPEvent.Type.JOD_PERMS_ADD,"Permission added", payload);
    }

    public static void registerPermUpdated(JOSPPerm oldPerm, JOSPPerm newPerm) {
        String payload;
        payload = "{";
        payload += "\"add\": {";
        payload += String.format("    \"id\": \"%s\", ", newPerm.getId());
        payload += String.format("    \"srvId\": \"%s\", ", newPerm.getSrvId());
        payload += String.format("    \"usrId\": \"%s\", ", newPerm.getUsrId());
        payload += String.format("    \"connType\": \"%s\", ", newPerm.getConnType());
        payload += String.format("    \"permType\": \"%s\"", newPerm.getPermType());
        payload += "},";
        payload += "\"rem\": {";
        payload += String.format("    \"id\": \"%s\", ", oldPerm.getId());
        payload += String.format("    \"srvId\": \"%s\", ", oldPerm.getSrvId());
        payload += String.format("    \"usrId\": \"%s\", ", oldPerm.getUsrId());
        payload += String.format("    \"connType\": \"%s\", ", oldPerm.getConnType());
        payload += String.format("    \"permType\": \"%s\"", oldPerm.getPermType());
        payload += "}";
        payload += "}";

        register(JOSPEvent.Type.JOD_PERMS_UPD,"Permission updated", payload);
    }

    public static void registerPermRemoved(JOSPPerm oldPerm) {
        String payload;
        payload = "{";
        payload += "\"rem\": {";
        payload += String.format("    \"id\": \"%s\", ", oldPerm.getId());
        payload += String.format("    \"srvId\": \"%s\", ", oldPerm.getSrvId());
        payload += String.format("    \"usrId\": \"%s\", ", oldPerm.getUsrId());
        payload += String.format("    \"connType\": \"%s\", ", oldPerm.getConnType());
        payload += String.format("    \"permType\": \"%s\"", oldPerm.getPermType());
        payload += "}";
        payload += "}";

        register(JOSPEvent.Type.JOD_PERMS_REM,"Permission removed", payload);
    }


    // JOD_STRUCT_LOAD,
    /*
    JOD_STRUCT_UPD,
    */

    public static void registerStructLoad(String model, String version, int compsCount) {
        String payload;
        payload = "{";
        payload += String.format("\"model\": \"%s\"", model);
        payload += String.format(", \"version\": \"%s\"", version);
        payload += String.format(", \"compsCount\": \"%s\"", compsCount);
        payload += "}";
        register(JOSPEvent.Type.JOD_STRUCT_LOAD,"Structure loaded", payload);
    }

    public static void registerStructLoad(Throwable t) {
        String payload = "";
        register(JOSPEvent.Type.JOD_STRUCT_LOAD,"Structure loaded", payload,t);
    }


    // JOD_INFO_UPD,
    // JOD_STATUS_UPD,

    public static void registerStatusUpd(AbsJODState comp, JODStateUpdate update) {
        String payload = String.format("{\"comp\": \"%s\", \"name\": \"%s\", \"update\": \"%s\"}", comp.getPath().getString(), comp.getName(), update.encode());
        register(JOSPEvent.Type.JOD_STATUS_UPD,"Status updated", payload);
    }

    public static void registerInfoUpd(String infoName, String newValue) {
        registerInfoUpd(infoName,null,newValue);
    }

    public static void registerInfoUpd(String infoName, String oldValue, String newValue) {
        String payload;
        payload = "{";
        payload += String.format("\"info\": \"%s\"", infoName);
        if (oldValue!=null)
            payload += String.format(", oldValue\": \"%s\"", oldValue);
        payload += String.format(", newValue\": \"%s\"", newValue);
        payload += "}";
        register(JOSPEvent.Type.JOD_INFO_UPD, String.format("Info %s updated", infoName), payload);
    }

    public static void registerInfoUpd(String infoName, Throwable t) {
        String payload;
        payload = "{";
        payload += String.format("\"info\": \"%s\"", infoName);
        payload += "}";
        register(JOSPEvent.Type.JOD_INFO_UPD, String.format("Info %s updated", infoName), payload, t);
    }

    // JOD_ACTION_REQ,
    // JOD_ACTION_EXEC

    public static void registerActionReq(String srvId, String usrId, JOSPPerm.Connection connType, JOSPProtocol.ActionCmd cmd) {
        String payload;
        payload = "{";
        payload += String.format("\"srvId\": \"%s\"", srvId);
        payload += String.format(", \"usrId\": \"%s\"", usrId);
        payload += String.format(", \"connType\": \"%s\"", connType.toString());
        payload += String.format(", \"component\": \"%s\"", cmd.getComponentPath());
        payload += String.format(", \"command\": \"%s\"", cmd.getCommand().getClass().getSimpleName());
        payload += "}";
        register(JOSPEvent.Type.JOD_ACTION_REQ,"Action requested", payload);
    }

    public static void registerActionExec(String srvId, String usrId, JOSPPerm.Connection connType, JOSPProtocol.ActionCmd cmd) {
        String payload;
        payload = "{";
        payload += String.format("\"srvId\": \"%s\"", srvId);
        payload += String.format(", \"usrId\": \"%s\"", usrId);
        payload += String.format(", \"connType\": \"%s\"", connType.toString());
        payload += String.format(", \"component\": \"%s\"", cmd.getComponentPath());
        payload += String.format(", \"command\": \"%s\"", cmd.getCommand().getClass().getSimpleName());
        payload += "}";
        register(JOSPEvent.Type.JOD_ACTION_EXEC,"Action requested", payload);
    }

    public static void registerActionExecFail(String error, String srvId, String usrId, JOSPPerm.Connection connType, JOSPProtocol.ActionCmd cmd) {
        String payload;
        payload = "{";
        payload += String.format("\"srvId\": \"%s\"", srvId);
        payload += String.format(", \"usrId\": \"%s\"", usrId);
        payload += String.format(", \"connType\": \"%s\"", connType.toString());
        payload += String.format(", \"component\": \"%s\"", cmd.getComponentPath());
        payload += String.format(", \"command\": \"%s\"", cmd.getCommand().getClass().getSimpleName());
        payload += String.format(", \"error\": \"%s\"", error);
        payload += "}";
        register(JOSPEvent.Type.JOD_ACTION_EXEC,"Action executed", payload, new Exception(error));
    }

}
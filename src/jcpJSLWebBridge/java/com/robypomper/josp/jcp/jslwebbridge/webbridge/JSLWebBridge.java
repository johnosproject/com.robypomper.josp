package com.robypomper.josp.jcp.jslwebbridge.webbridge;

import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.comm.peer.PeerInfoRemote;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaTimers;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.jslwebbridge.exceptions.*;
import com.robypomper.josp.jsl.FactoryJSL;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.remote.ObjInfo;
import com.robypomper.josp.jsl.objs.remote.ObjPerms;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.states.StateException;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class JSLWebBridge {

    // Class constants

    public static final String HEART_BEAT = "HB";
    public static final boolean LOC_COMM_ENABLED = false;
    public static final String TH_SCHEDULE_JSL_REMOVE = "SCHEDULE_JSL_REMOVE";
    public static final String TH_EMITTERS_HEARTBEAT = "EMITTERS_HEARTBEAT";


    public static final String LOG_CREATED_JSL = "JSL Instance '%s' created for session '%s'";
    public static final String LOG_REMOVED_JSL = "JSL Instance '%s' removed for session '%s'";
    public static final String LOG_ERR_REMOVED_JSL = "Error on JSL Instance '%s' removing for session '%s' (%s)";
    public static final String LOG_SCHEDULE_JSL_REMOVE = "JSL Instance scheduled for remove for session '%s'";
    public static final String LOG_SCHEDULE_JSL_REMOVE_ABORT = "Aborted JSL Instance removing for session '%s'";
    public static final String LOG_CREATED_EMITTER = "JSL Instance '%s' created emitter for session '%s' at address '%s'";
    public static final String LOG_REMOVED_EMITTER = "JSL Instance '%s' removed emitter for session '%s' at address '%s'";
    public static final String LOG_SEND_EVENT = "JSL Instance '%s' for session '%s' at address '%s' send '%s' event";
    public static final String LOG_SEND_EVENT_DISCONNECTED = "JSL Instance '%s' for session '%s' at address '%s' emitter disconnected";
    public static final String LOG_ERR_SEND_EVENT = "Error on JSL Instance '%s' for session '%s' at address '%s' sending '%s' event (%s)";
    public static final String ASSERTION_NO_JSL = "Can't call JSLWebBridge.%s() method when no JSL Instance was created for session '%s'";
    public static final String ASSERTION_NO_EMITTERS = "Can't call JSLWebBridge.%s() method when no emitters list was created for session '%s'";
    public static final String ASSERTION_NO_EMITTER_CLIENT = "Can't call JSLWebBridge.%s() method when no emitters list was created for session '%s' at address '%s'";

    //@formatter:off
    public static final String EVENT_JCP_APIS_CONN          = "{\"what\": \"JCP_APIS_CONN\",        \"url\": \"%s\"}";
    public static final String EVENT_JCP_APIS_DISCONN       = "{\"what\": \"JCP_APIS_DISCONN\",     \"url\": \"%s\"}";
    public static final String EVENT_JCP_APIS_FAIL_GEN      = "{\"what\": \"JCP_APIS_FAIL_GEN\",    \"url\": \"%s\",    \"error\": \"%s\"}";
    public static final String EVENT_JCP_APIS_FAIL_AUTH     = "{\"what\": \"JCP_APIS_FAIL_AUT\",    \"url\": \"%s\",    \"error\": \"%s\"}";
    public static final String EVENT_JCP_APIS_LOGIN         = "{\"what\": \"JCP_APIS_LOGIN\",       \"url\": \"%s\",    \"userid\": \"%s\", \"username\": \"%s\"}";
    public static final String EVENT_JCP_APIS_LOGOUT        = "{\"what\": \"JCP_APIS_LOGOUT\",      \"url\": \"%s\"}";

    public static final String EVENT_JCP_GWS_CONNECTING     = "{\"what\": \"JCP_GWS_CONNECTING\",       \"proto\": \"%s\",  \"host\": \"%s\",   \"port\": \"%s\"}";
    public static final String EVENT_JCP_GWS_WAITING        = "{\"what\": \"JCP_GWS_WAITING\",          \"proto\": \"%s\",  \"host\": \"%s\",   \"port\": \"%s\"}";
    public static final String EVENT_JCP_GWS_CONNECTED      = "{\"what\": \"JCP_GWS_CONNECTED\",        \"proto\": \"%s\",  \"host\": \"%s\",   \"port\": \"%s\"}";
    public static final String EVENT_JCP_GWS_DISCONNECTING  = "{\"what\": \"JCP_GWS_DISCONNECTING\",    \"proto\": \"%s\",  \"host\": \"%s\",   \"port\": \"%s\"}";
    public static final String EVENT_JCP_GWS_DISCONNECTED   = "{\"what\": \"JCP_GWS_DISCONNECTED\",     \"proto\": \"%s\",  \"host\": \"%s\",   \"port\": \"%s\"}";
    public static final String EVENT_JCP_GWS_FAIL           = "{\"what\": \"JCP_GWS_FAIL\",             \"proto\": \"%s\",  \"host\": \"%s\",   \"port\": \"%s\",   \"error\": \"%s\"}";

    public static final String EVENT_OBJ_ADD            = "{\"what\": \"OBJ_ADD\",          \"objId\": \"%s\"}";
    public static final String EVENT_OBJ_REM            = "{\"what\": \"OBJ_REM\",          \"objId\": \"%s\"}";
    public static final String EVENT_OBJ_CONN           = "{\"what\": \"OBJ_CONN\",         \"objId\": \"%s\"}";
    public static final String EVENT_OBJ_DISCONN        = "{\"what\": \"OBJ_DISCONN\",      \"objId\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_STRUCT     = "{\"what\": \"OBJ_UPD_STRUCT\",   \"objId\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_PERMS      = "{\"what\": \"OBJ_UPD_PERMS\",    \"objId\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_PERM_SRV   = "{\"what\": \"OBJ_UPD_PERM_SRV\", \"objId\": \"%s\",  \"new\": \"%s\",        \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_COMP       = "{\"what\": \"OBJ_UPD_COMP\",     \"objId\": \"%s\",  \"compPath\": \"%s\",   \"new\": \"%s\",    \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_INFO_NAME          = "{\"what\": \"OBJ_UPD_INFO_NAME\",        \"objId\": \"%s\",  \"new\": \"%s\", \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_INFO_OWNER         = "{\"what\": \"OBJ_UPD_INFO_OWNER\",       \"objId\": \"%s\",  \"new\": \"%s\", \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_INFO_JOD_VERSION   = "{\"what\": \"OBJ_UPD_INFO_JOD_VERSION\", \"objId\": \"%s\",  \"new\": \"%s\", \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_INFO_MODEL         = "{\"what\": \"OBJ_UPD_INFO_MODEL\",       \"objId\": \"%s\",  \"new\": \"%s\", \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_INFO_BRAND         = "{\"what\": \"OBJ_UPD_INFO_BRAND\",       \"objId\": \"%s\",  \"new\": \"%s\", \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_INFO_LONG_DESCR    = "{\"what\": \"OBJ_UPD_INFO_LONG_DESCR\",  \"objId\": \"%s\",  \"new\": \"%s\", \"old\": \"%s\"}";
    //@formatter:on


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLWebBridge.class);
    private final JSLParams jslParams;
    // jsl instances
    private final Map<String, JSL> jslInstances = new HashMap<>();
    private final Map<String, Timer> jslRemoveTimers = new HashMap<>();
    private final int jslRemoveScheduledDelaySeconds;
    // sse emitters
    private final Map<String, Map<String, SseEmitter>> jslEmitters = new HashMap<>();
    private final Map<SseEmitter, Integer> jslEmittersCounts = new HashMap<>();
    private final Map<SseEmitter, String> jslEmittersClientAddress = new HashMap<>();
    // jsl listeners 1st level
    private final Map<JSL, JSLObjsMngr.ObjsMngrListener> objsMngrListeners = new HashMap<>();
    private final Map<JSL, JCPClient2.ConnectionListener> cloudAPIsListeners = new HashMap<>();
    private final Map<JSL, JCPClient2.LoginListener> cloudAPIsLoginListeners = new HashMap<>();
    private final Map<JSL, PeerConnectionListener> cloudConnectionListeners = new HashMap<>();
    // objs listeners 2nd level
    private final Map<JSL, Map<JSLRemoteObject, ObjInfo.RemoteObjectInfoListener>> objInfoListeners = new HashMap<>();
    private final Map<JSL, Map<JSLRemoteObject, ObjComm.RemoteObjectConnListener>> objCommListeners = new HashMap<>();
    private final Map<JSL, Map<JSLRemoteObject, ObjStruct.RemoteObjectStructListener>> objStructListeners = new HashMap<>();
    private final Map<JSL, Map<JSLRemoteObject, ObjPerms.RemoteObjectPermsListener>> objPermsListeners = new HashMap<>();
    // components listeners 3rd level
    private final Map<JSL, Map<JSLRemoteObject, Map<JSLComponent, Object>>> objComponentListeners = new HashMap<>();
    // heartbeats
    private final int heartbeatTimerDelaySeconds;


    // Constructors

    public JSLWebBridge(JSLParams jslParams,
                        int jslRemoveScheduledDelaySeconds,
                        int heartbeatTimerDelaySeconds) {
        this.jslParams = jslParams;
        this.jslRemoveScheduledDelaySeconds = jslRemoveScheduledDelaySeconds;
        this.heartbeatTimerDelaySeconds = heartbeatTimerDelaySeconds;
        startHeartBeatTimer();
    }

    public void dismiss() {
        for (Map.Entry<String, JSL> sessionJSL : jslInstances.entrySet()) {
            String sessionId = sessionJSL.getKey();
            JSL jsl = sessionJSL.getValue();
            for (SseEmitter emitter : jslEmitters.get(sessionId).values())
                removeSSEEmitter(emitter, sessionId);
            try {
                removeJSLInstance(sessionId);
            } catch (EmittersNotEmptyForSessionException e) {
                log.warn(String.format(LOG_ERR_REMOVED_JSL, jsl.getServiceInfo().getFullId(), sessionId, e), e);
            }
        }
    }

    // Getters

    public JSL getJSLInstance(String sessionId) throws JSLNotInitForSessionException {
        JSL jsl = jslInstances.get(sessionId);
        if (jsl == null)
            throw new JSLNotInitForSessionException(sessionId);
        return jsl;
    }

    public Map<String, SseEmitter> getJSLEmitters(String sessionId) throws EmittersNotInitForSessionException {
        Map<String, SseEmitter> emitters = jslEmitters.get(sessionId);
        if (emitters == null)
            throw new EmittersNotInitForSessionException(sessionId);
        return emitters;
    }


    // JSL Instances mngm

    public JSL createJSLInstance(String sessionId, String clientId, String clientSecret) throws JSLAlreadyInitForSessionException, JSLErrorOnInitException {
        if (jslInstances.get(sessionId) != null)
            throw new JSLAlreadyInitForSessionException(sessionId);

        JSL jsl;
        try {
            jsl = doCreateJSLInstance(jslParams, clientId, clientSecret);

        } catch (JSL.FactoryException | StateException e) {
            throw new JSLErrorOnInitException(sessionId, e);
        }
        jslInstances.put(sessionId, jsl);
        jslEmitters.put(sessionId, new HashMap<>());

        registerJSLEvents(jsl, sessionId);

        log.info(String.format(LOG_CREATED_JSL, jsl.getServiceInfo().getFullId(), sessionId));
        return jsl;
    }

    private static JSL doCreateJSLInstance(JSLParams jslParams, String clientId, String clientSecret) throws JSL.FactoryException, StateException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JSLSettings_002.JCP_SSL, jslParams.useSSL);
        properties.put(JSLSettings_002.JCP_URL_APIS, jslParams.urlAPIs);
        properties.put(JSLSettings_002.JCP_URL_AUTH, jslParams.urlAuth);
        properties.put(JSLSettings_002.JCP_CLIENT_ID, clientId);
        properties.put(JSLSettings_002.JCP_CLIENT_SECRET, clientSecret);
        properties.put(JSLSettings_002.JCP_CLIENT_CALLBACK, jslParams.clientCallback);
        properties.put(JSLSettings_002.JSLSRV_ID, clientId);
        properties.put(JSLSettings_002.JSLSRV_NAME, clientId);
        properties.put(JSLSettings_002.JSLCOMM_LOCAL_ENABLED, LOC_COMM_ENABLED);

        JSL.Settings settings = FactoryJSL.loadSettings(properties, jslParams.jslVersion);
        JSL jsl = FactoryJSL.createJSL(settings, jslParams.jslVersion);
        jsl.startup();

        return jsl;
    }

    private void removeJSLInstance(String sessionId) throws EmittersNotEmptyForSessionException {
        try {
            if (getJSLEmitters(sessionId).size() > 0)
                throw new EmittersNotEmptyForSessionException(sessionId);
        } catch (EmittersNotInitForSessionException ignore) {
        }

        JSL jsl;
        try {
            jsl = getJSLInstance(sessionId);

        } catch (JSLNotInitForSessionException e) {
            JavaAssertions.makeWarning_Failed(e, String.format(ASSERTION_NO_JSL, "removeJSLInstance", sessionId));
            return;
        }

        deregisterJSLEvents(jsl);

        try {
            jsl.shutdown();

        } catch (StateException e) {
            log.warn(String.format(LOG_ERR_REMOVED_JSL, jsl.getServiceInfo().getFullId(), sessionId, e) + ", continue removing JSL Instance", e);
        }

        jslInstances.remove(sessionId);
        jslEmitters.remove(sessionId);
        //session.invalidate();
        log.info(String.format(LOG_REMOVED_JSL, jsl.getServiceInfo().getFullId(), sessionId));
    }

    private void scheduleRemoveJSLInstance(String sessionId) {
        if (jslRemoveTimers.get(sessionId) != null)
            return;

        Timer timer = JavaTimers.initAndStart(new ScheduleJSLRemove(sessionId), TH_SCHEDULE_JSL_REMOVE, jslRemoveScheduledDelaySeconds);
        log.debug(String.format(LOG_SCHEDULE_JSL_REMOVE, sessionId));
        jslRemoveTimers.put(sessionId, timer);
    }

    private class ScheduleJSLRemove implements Runnable {

        private final String sessionId;

        public ScheduleJSLRemove(String sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public void run() {
            try {
                if (getJSLEmitters(sessionId).size() > 0) {
                    log.debug(String.format(LOG_SCHEDULE_JSL_REMOVE_ABORT, sessionId));
                    jslRemoveTimers.remove(sessionId);
                    return;
                }

            } catch (EmittersNotInitForSessionException e) {
                JavaAssertions.makeWarning_Failed("Emitters list for session '%s' must exist until corresponding JSL exist.");
            }

            JSL jsl = null;
            try {
                jsl = getJSLInstance(sessionId);

            } catch (JSLNotInitForSessionException e) {
                JavaAssertions.makeWarning_Failed("JSL Instance for session '%s' must exist when his schedule remove timer is active.");
                jslRemoveTimers.remove(sessionId);
                return;
            }

            try {
                removeJSLInstance(sessionId);

            } catch (EmittersNotEmptyForSessionException e) {
                log.warn(String.format(LOG_ERR_REMOVED_JSL, jsl.getServiceInfo().getFullId(), sessionId, e), e);
            }
            jslRemoveTimers.remove(sessionId);
        }

    }


    // SSE mgnm

    public SseEmitter createSSEEmitter(String sessionId) throws EmitterAlreadyInitForSessionException, EmittersNotInitForSessionException, JSLNotInitForSessionException {
        JSL jsl = getJSLInstance(sessionId);

        SseEmitter emitter = new SseEmitter(-1L);
        String clientAddress = getClientFullAddress();
        if (getJSLEmitters(sessionId).get(clientAddress) != null)
            throw new EmitterAlreadyInitForSessionException(sessionId, clientAddress);

        getJSLEmitters(sessionId).put(clientAddress, emitter);
        jslEmittersCounts.put(emitter, 0);
        jslEmittersClientAddress.put(emitter, clientAddress);

        log.info(String.format(LOG_CREATED_EMITTER, jsl.getServiceInfo().getFullId(), sessionId, getClientFullAddress()));
        return emitter;
    }

    private void removeSSEEmitter(SseEmitter emitter, String sessionId) {
        JSL jsl;
        try {
            jsl = getJSLInstance(sessionId);

        } catch (JSLNotInitForSessionException e) {
            JavaAssertions.makeWarning_Failed(e, String.format(ASSERTION_NO_JSL, "removeSSEEmitter", sessionId));
            return;
        }

        Map<String, SseEmitter> emitters;
        try {
            emitters = getJSLEmitters(sessionId);

        } catch (EmittersNotInitForSessionException e) {
            JavaAssertions.makeWarning_Failed(e, String.format(ASSERTION_NO_EMITTERS, "removeSSEEmitter", sessionId));
            return;
        }

        String clientAddress = getClientFullAddress(emitter);
        emitter.complete();
        jslEmittersCounts.remove(emitter);
        jslEmittersClientAddress.remove(emitter);
        emitters.remove(clientAddress);
        log.info(String.format(LOG_REMOVED_EMITTER, jsl.getServiceInfo().getFullId(), sessionId, clientAddress));

        if (emitters.size() == 0)
            scheduleRemoveJSLInstance(sessionId);
    }


    // JSL events registration

    private void registerJSLEvents(JSL jsl, String sessionId) {
        jsl.getObjsMngr().addListener(createObjsMngrListener(jsl, sessionId));
        jsl.getCommunication().getCloudAPIs().addConnectionListener(createCloudAPIsListener(jsl, sessionId));
        jsl.getCommunication().getCloudAPIs().addLoginListener(createCloudAPIsLoginListener(jsl, sessionId));
        jsl.getCommunication().getCloudConnection().addListener(createCloudConnectionListener(jsl, sessionId));

        objInfoListeners.put(jsl, new HashMap<>());
        objCommListeners.put(jsl, new HashMap<>());
        objStructListeners.put(jsl, new HashMap<>());
        objPermsListeners.put(jsl, new HashMap<>());

        objComponentListeners.put(jsl, new HashMap<>());
    }

    private void deregisterJSLEvents(JSL jsl) {
        jsl.getObjsMngr().removeListener(objsMngrListeners.remove(jsl));
        jsl.getCommunication().getCloudAPIs().removeConnectionListener(cloudAPIsListeners.remove(jsl));
        jsl.getCommunication().getCloudAPIs().removeLoginListener(cloudAPIsLoginListeners.remove(jsl));
        jsl.getCommunication().getCloudConnection().removeListener(cloudConnectionListeners.remove(jsl));

        for (JSLRemoteObject obj : jsl.getObjsMngr().getAllObjects()) {
            removeObjComponentListenerRecursively(jsl, obj, obj.getStruct().getStructure());
            objComponentListeners.get(jsl).remove(obj);

            objInfoListeners.get(jsl).remove(obj);
            objCommListeners.get(jsl).remove(obj);
            objStructListeners.get(jsl).remove(obj);
            objPermsListeners.get(jsl).remove(obj);
        }

        objInfoListeners.remove(jsl);
        objCommListeners.remove(jsl);
        objStructListeners.remove(jsl);
        objPermsListeners.remove(jsl);
        objComponentListeners.remove(jsl);
    }


    // JSL Listeners 1st level

    private JSLObjsMngr.ObjsMngrListener createObjsMngrListener(JSL jsl, String sessionId) {
        JSLObjsMngr.ObjsMngrListener l = new JSLObjsMngr.ObjsMngrListener() {
            @Override
            public void onObjAdded(JSLRemoteObject obj) {
                emit(sessionId, String.format(EVENT_OBJ_ADD, obj.getId()));

                obj.getInfo().addListener(createObjInfoListener(jsl, obj, sessionId));
                obj.getComm().addListener(createObjCommListener(jsl, obj, sessionId));
                obj.getStruct().addListener(createObjStructListener(jsl, obj, sessionId));
                obj.getPerms().addListener(createObjPermsListener(jsl, obj, sessionId));

                objComponentListeners.get(jsl).put(obj, new HashMap<>());
                addObjComponentListenerRecursively(jsl, obj, obj.getStruct().getStructure(), sessionId);
            }

            @Override
            public void onObjRemoved(JSLRemoteObject obj) {
                emit(sessionId, String.format(EVENT_OBJ_REM, obj.getId()));

                obj.getInfo().removeListener(objInfoListeners.get(jsl).remove(obj));
                obj.getComm().removeListener(objCommListeners.get(jsl).remove(obj));
                obj.getStruct().removeListener(objStructListeners.get(jsl).remove(obj));
                obj.getPerms().removeListener(objPermsListeners.get(jsl).remove(obj));

                objComponentListeners.get(jsl).remove(obj);
                removeObjComponentListenerRecursively(jsl, obj, obj.getStruct().getStructure());
            }
        };
        objsMngrListeners.put(jsl, l);
        return l;
    }

    private JCPClient2.ConnectionListener createCloudAPIsListener(JSL jsl, String sessionId) {
        JCPClient2.ConnectionListener l = new JCPClient2.ConnectionListener() {
            @Override
            public void onConnected(JCPClient2 jcpClient) {
                emit(sessionId, String.format(EVENT_JCP_APIS_CONN, jcpClient.getAPIsUrl()));
            }

            @Override
            public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
                emit(sessionId, String.format(EVENT_JCP_APIS_FAIL_GEN, jcpClient.getAPIsUrl(), t));
            }

            @Override
            public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
                emit(sessionId, String.format(EVENT_JCP_APIS_FAIL_AUTH, jcpClient.getAPIsUrl(), t));
            }

            @Override
            public void onDisconnected(JCPClient2 jcpClient) {
                emit(sessionId, String.format(EVENT_JCP_APIS_DISCONN, jcpClient.getAPIsUrl()));
            }
        };
        cloudAPIsListeners.put(jsl, l);
        return l;
    }

    private JCPClient2.LoginListener createCloudAPIsLoginListener(JSL jsl, String sessionId) {
        JCPClient2.LoginListener l = new JCPClient2.LoginListener() {
            @Override
            public void onLogin(JCPClient2 jcpClient) {
                emit(sessionId, String.format(EVENT_JCP_APIS_LOGIN, jcpClient.getAPIsUrl(), jsl.getUserMngr().getUserId(), jsl.getUserMngr().getUsername()));
            }

            @Override
            public void onLogout(JCPClient2 jcpClient) {
                emit(sessionId, String.format(EVENT_JCP_APIS_LOGOUT, jcpClient.getAPIsUrl()));
            }
        };
        cloudAPIsLoginListeners.put(jsl, l);
        return l;
    }

    private PeerConnectionListener createCloudConnectionListener(JSL jsl, String sessionId) {
        PeerConnectionListener l = new PeerConnectionListener() {
            @Override
            public void onConnecting(Peer peer) {
                PeerInfoRemote remoteInfo = peer.getConnectionInfo().getRemoteInfo();
                emit(sessionId, String.format(EVENT_JCP_GWS_CONNECTING, remoteInfo.getProto(), remoteInfo.getHostname(), remoteInfo.getPort()));
            }

            @Override
            public void onWaiting(Peer peer) {
                PeerInfoRemote remoteInfo = peer.getConnectionInfo().getRemoteInfo();
                emit(sessionId, String.format(EVENT_JCP_GWS_WAITING, remoteInfo.getProto(), remoteInfo.getHostname(), remoteInfo.getPort()));
            }

            @Override
            public void onConnect(Peer peer) {
                PeerInfoRemote remoteInfo = peer.getConnectionInfo().getRemoteInfo();
                emit(sessionId, String.format(EVENT_JCP_GWS_CONNECTED, remoteInfo.getProto(), remoteInfo.getHostname(), remoteInfo.getPort()));
            }

            @Override
            public void onDisconnecting(Peer peer) {
                PeerInfoRemote remoteInfo = peer.getConnectionInfo().getRemoteInfo();
                emit(sessionId, String.format(EVENT_JCP_GWS_DISCONNECTING, remoteInfo.getProto(), remoteInfo.getHostname(), remoteInfo.getPort()));
            }

            @Override
            public void onDisconnect(Peer peer) {
                PeerInfoRemote remoteInfo = peer.getConnectionInfo().getRemoteInfo();
                emit(sessionId, String.format(EVENT_JCP_GWS_DISCONNECTED, remoteInfo.getProto(), remoteInfo.getHostname(), remoteInfo.getPort()));
            }

            @Override
            public void onFail(Peer peer, String failMsg, Throwable t) {
                PeerInfoRemote remoteInfo = peer.getConnectionInfo().getRemoteInfo();
                emit(sessionId, String.format(EVENT_JCP_GWS_FAIL, remoteInfo.getProto(), remoteInfo.getHostname(), remoteInfo.getPort(), t));
            }
        };
        cloudConnectionListeners.put(jsl, l);
        return l;
    }


    // Objs Listeners 2nd level

    private ObjInfo.RemoteObjectInfoListener createObjInfoListener(JSL jsl, JSLRemoteObject obj, String sessionId) {
        ObjInfo.RemoteObjectInfoListener l = new ObjInfo.RemoteObjectInfoListener() {
            @Override
            public void onNameChanged(JSLRemoteObject obj, String newName, String oldName) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_INFO_NAME, obj.getId(), newName, oldName));
            }

            @Override
            public void onOwnerIdChanged(JSLRemoteObject obj, String newOwnerId, String oldOwnerId) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_INFO_OWNER, obj.getId(), newOwnerId, oldOwnerId));
            }

            @Override
            public void onJODVersionChanged(JSLRemoteObject obj, String newJODVersion, String oldJODVersion) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_INFO_JOD_VERSION, obj.getId(), newJODVersion, oldJODVersion));
            }

            @Override
            public void onModelChanged(JSLRemoteObject obj, String newModel, String oldModel) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_INFO_MODEL, obj.getId(), newModel, oldModel));
            }

            @Override
            public void onBrandChanged(JSLRemoteObject obj, String newBrand, String oldBrand) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_INFO_BRAND, obj.getId(), newBrand, oldBrand));
            }

            @Override
            public void onLongDescrChanged(JSLRemoteObject obj, String newLongDescr, String oldLongDescr) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_INFO_LONG_DESCR, obj.getId(), newLongDescr, oldLongDescr));
            }
        };
        objInfoListeners.get(jsl).put(obj, l);
        return l;
    }

    private ObjComm.RemoteObjectConnListener createObjCommListener(JSL jsl, JSLRemoteObject obj, String sessionId) {
        ObjComm.RemoteObjectConnListener l = new ObjComm.RemoteObjectConnListener() {
            @Override
            public void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            }

            @Override
            public void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            }

            @Override
            public void onCloudConnected(JSLRemoteObject obj) {
                emit(sessionId, String.format(EVENT_OBJ_CONN, obj.getId()));
            }

            @Override
            public void onCloudDisconnected(JSLRemoteObject obj) {
                emit(sessionId, String.format(EVENT_OBJ_DISCONN, obj.getId()));
            }
        };
        objCommListeners.get(jsl).put(obj, l);
        return l;
    }

    private ObjStruct.RemoteObjectStructListener createObjStructListener(JSL jsl, JSLRemoteObject obj, String sessionId) {
        ObjStruct.RemoteObjectStructListener l = new ObjStruct.RemoteObjectStructListener() {
            @Override
            public void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_STRUCT, obj.getId()));

                removeObjComponentListenerRecursively(jsl, obj, newRoot);
                addObjComponentListenerRecursively(jsl, obj, newRoot, sessionId);
            }
        };
        objStructListeners.get(jsl).put(obj, l);
        return l;
    }

    private ObjPerms.RemoteObjectPermsListener createObjPermsListener(JSL jsl, JSLRemoteObject obj, String sessionId) {
        ObjPerms.RemoteObjectPermsListener l = new ObjPerms.RemoteObjectPermsListener() {
            @Override
            public void onPermissionsChanged(JSLRemoteObject obj, List<JOSPPerm> newPerms, List<JOSPPerm> oldPerms) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_PERMS, obj.getId()));
            }

            @Override
            public void onServicePermChanged(JSLRemoteObject obj, JOSPPerm.Connection connType, JOSPPerm.Type newPermType, JOSPPerm.Type oldPermType) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_PERM_SRV, obj.getId(), newPermType, oldPermType));
            }
        };
        objPermsListeners.get(jsl).put(obj, l);
        return l;
    }


    // Components Listeners 3rd level

    private void addObjComponentListenerRecursively(JSL jsl, JSLRemoteObject obj, JSLComponent component, String sessionId) {
        if (component instanceof JSLBooleanState)
            ((JSLBooleanState) component).addListener(createObjCompBooleanListener(jsl, obj, (JSLBooleanState) component, sessionId));

        else if (component instanceof JSLRangeState)
            ((JSLRangeState) component).addListener(createObjCompRangeListener(jsl, obj, (JSLRangeState) component, sessionId));

        else if (component instanceof JSLContainer)
            for (JSLComponent c : ((JSLContainer) component).getComponents())
                addObjComponentListenerRecursively(jsl, obj, c, sessionId);
    }

    private JSLBooleanState.BooleanStateListener createObjCompBooleanListener(JSL jsl, JSLRemoteObject obj, JSLBooleanState compBoolean, String sessionId) {
        JSLBooleanState.BooleanStateListener l = new JSLBooleanState.BooleanStateListener() {

            @Override
            public void onStateChanged(JSLBooleanState component, boolean newState, boolean oldState) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_COMP, obj.getId(), component.getPath().getString(), newState, oldState));
            }

        };
        objComponentListeners.get(jsl).get(obj).put(compBoolean, l);
        return l;
    }

    private JSLRangeState.RangeStateListener createObjCompRangeListener(JSL jsl, JSLRemoteObject obj, JSLRangeState compRange, String sessionId) {
        JSLRangeState.RangeStateListener l = new JSLRangeState.RangeStateListener() {

            @Override
            public void onStateChanged(JSLRangeState component, double newState, double oldState) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_COMP, obj.getId(), component.getPath().getString(), newState, oldState));
            }

            @Override
            public void onMinReached(JSLRangeState component, double state, double min) {
            }

            @Override
            public void onMaxReached(JSLRangeState component, double state, double max) {
            }

        };
        objComponentListeners.get(jsl).get(obj).put(compRange, l);
        return l;
    }

    private void removeObjComponentListenerRecursively(JSL jsl, JSLRemoteObject obj, JSLComponent component) {
        if (component instanceof JSLBooleanState)
            ((JSLBooleanState) component).removeListener((JSLBooleanState.BooleanStateListener) objComponentListeners.get(jsl).get(obj).remove(component));

        else if (component instanceof JSLRangeState)
            ((JSLRangeState) component).removeListener((JSLRangeState.RangeStateListener) objComponentListeners.get(jsl).get(obj).remove(component));

        else if (component instanceof JSLContainer)
            for (JSLComponent c : ((JSLContainer) component).getComponents())
                removeObjComponentListenerRecursively(jsl, obj, c);
    }


    // Emitter send methods

    private void emit(String data) {
        for (String sessionId : jslInstances.keySet())
            emit(sessionId, data);
    }

    private void emit(String sessionId, String data) {
        Collection<SseEmitter> emitters;
        try {
            emitters = getJSLEmitters(sessionId).values();

        } catch (EmittersNotInitForSessionException e) {
            JavaAssertions.makeWarning_Failed(e, String.format(ASSERTION_NO_EMITTERS, "emit", sessionId) + ", remove JSL Instance");
            try {
                removeJSLInstance(sessionId);
            } catch (EmittersNotEmptyForSessionException ignore) { /* Can't throw EmittersNotEmpty because in EmittersNotInit catch block */ }
            return;
        }

        if (emitters.size() == 0) {
            JavaAssertions.makeWarning_Failed(String.format(ASSERTION_NO_EMITTERS, "emit", sessionId) + ", remove JSL Instance");
            scheduleRemoveJSLInstance(sessionId);
            return;
        }

        List<SseEmitter> emittersTmp = new ArrayList<>(emitters);
        for (SseEmitter emitter : emittersTmp)
            emit(emitter, sessionId, data);
    }

    private void emit(SseEmitter emitter, String sessionId, String data) {
        JSL jsl;
        try {
            jsl = getJSLInstance(sessionId);

        } catch (JSLNotInitForSessionException e) {
            JavaAssertions.makeWarning_Failed(e, String.format(ASSERTION_NO_JSL, "emit", sessionId) + ", remove JSL Instance");
            return;
        }

        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data(data)
                .id(String.valueOf(increaseSSECounter(emitter)));

        try {
            emitter.send(event);
            log.debug(String.format(LOG_SEND_EVENT, jsl.getServiceInfo().getFullId(), sessionId, getClientFullAddress(emitter), data));

        } catch (Exception e) {
            if (e instanceof ClientAbortException)
                log.debug(String.format(LOG_SEND_EVENT_DISCONNECTED, jsl.getServiceInfo().getFullId(), sessionId, getClientFullAddress(emitter)) + ", remove emitter");
            else
                log.warn(String.format(LOG_ERR_SEND_EVENT, jsl.getServiceInfo().getFullId(), sessionId, getClientFullAddress(emitter), data, e) + ", remove emitter", e);
            removeSSEEmitter(emitter, sessionId);
        }
    }

    private int increaseSSECounter(SseEmitter emitter) {
        int newCount = jslEmittersCounts.get(emitter) + 1;
        jslEmittersCounts.put(emitter, newCount);
        return newCount;
    }


    // Session utils

    private String getClientAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null)
            ipAddress = request.getRemoteAddr();
        return ipAddress;
    }

    private int getClientPort() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getRemotePort();
    }

    private String getClientFullAddress() {
        return String.format("%s:%d", getClientAddress(), getClientPort());
    }

    private String getClientFullAddress(SseEmitter emitter) {
        return jslEmittersClientAddress.get(emitter);
    }


    // Heartbeat timer

    private void startHeartBeatTimer() {
        JavaTimers.initAndStart(new HeartBeatTimer(), TH_EMITTERS_HEARTBEAT, heartbeatTimerDelaySeconds * 1000L, heartbeatTimerDelaySeconds * 1000L);
    }

    private class HeartBeatTimer implements Runnable {

        @Override
        public void run() {
            emit(HEART_BEAT);

        }

    }

}

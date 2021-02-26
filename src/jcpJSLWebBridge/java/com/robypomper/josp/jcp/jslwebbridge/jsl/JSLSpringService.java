package com.robypomper.josp.jcp.jslwebbridge.jsl;

import com.robypomper.java.JavaAssertions;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jsl.FactoryJSL;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.remote.ObjInfo;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.DefaultJSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.states.StateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Service
public class JSLSpringService {

    // Class constants

    public static final String HEART_BEAT = "HB";
    public static final boolean LOC_COMM_ENABLED = false;


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLSpringService.class);
    private final String jslVersion;
    private final boolean useSSL;
    private final String urlAPIs;
    private final String urlAuth;
    private final String clientId;
    private final String clientSecret;
    private final String clientCallback;
    private final String srvId;
    private final Map<JSL, List<SseEmitter>> emitters = new HashMap<>();
    private final Map<JSL, HttpSession> sessions = new HashMap<>();
    private int sseCount = 0;
    private final int heartbeatTimerDelaySeconds;
    private Timer heartbeatTimer;


    // Constructor

    public JSLSpringService(@Value("${jsl.version:2.0.0}") String jslVersion,
                            @Value("${" + JSLSettings_002.JCP_SSL + ".public:" + JSLSettings_002.JCP_SSL_DEF + "}") boolean useSSL,
                            @Value("${" + JSLSettings_002.JCP_URL_APIS + ":" + JSLSettings_002.JCP_URL_DEF_APIS + "}") String urlAPIs,
                            @Value("${" + JSLSettings_002.JCP_URL_AUTH + ":" + JSLSettings_002.JCP_URL_DEF_AUTH + "}") String urlAuth,
                            @Value("${" + JSLSettings_002.JCP_CLIENT_ID + ":}") String clientId,
                            @Value("${" + JSLSettings_002.JCP_CLIENT_SECRET + ":}") String clientSecret,
                            @Value("${" + JSLSettings_002.JCP_CLIENT_CALLBACK + ":}") String clientCallback,
                            @Value("${" + JSLSettings_002.JSLSRV_ID + ":}") String srvId,
                            @Value("${jcp.jsl.heartbeat.delay:60}") int heartbeatTimerDelaySeconds) {
        if (clientId.isEmpty())
            throw new IllegalArgumentException(String.format("Properties '%s' must be set before run the JCP JSL Web Bridge", JSLSettings_002.JCP_CLIENT_ID));
        if (clientSecret.isEmpty())
            throw new IllegalArgumentException(String.format("Properties '%s' must be set before run the JCP JSL Web Bridge", JSLSettings_002.JCP_CLIENT_SECRET));
        if (clientCallback.isEmpty())
            throw new IllegalArgumentException(String.format("Properties '%s' must be set before run the JCP JSL Web Bridge", JSLSettings_002.JCP_CLIENT_CALLBACK));
        if (srvId.isEmpty())
            throw new IllegalArgumentException(String.format("Properties '%s' must be set before run the JCP JSL Web Bridge", JSLSettings_002.JSLSRV_ID));

        this.jslVersion = jslVersion;
        this.useSSL = useSSL;
        this.urlAPIs = urlAPIs;
        this.urlAuth = urlAuth;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.clientCallback = clientCallback;
        this.srvId = srvId;
        this.heartbeatTimerDelaySeconds = heartbeatTimerDelaySeconds;

        startHeartBeatTimer();
    }


    // Sessions and JSLs

    public JSL getHttp(HttpSession session) {
        try {
            return get(session);

        } catch (JSLSpringService.JSLSpringException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can not access to JSL instance for session '%s'.", session.getId()), e);
        }
    }

    public JSL get(HttpSession session) throws JSLSpringException {
        synchronized (this) {
            if (session.getAttribute("JSL-Instance") != null)
                return (JSL) session.getAttribute("JSL-Instance");

            try {
                return registerSessions(session);

            } catch (JSL.FactoryException | StateException e) {
                throw new JSLSpringException(String.format("Error creating JSL instance for sessions '%s'", session.getId()), e);
            }
        }
    }

    private JSL registerSessions(HttpSession session) throws JSL.FactoryException, StateException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JSLSettings_002.JCP_SSL, useSSL);
        properties.put(JSLSettings_002.JCP_URL_APIS, urlAPIs);
        properties.put(JSLSettings_002.JCP_URL_AUTH, urlAuth);
        properties.put(JSLSettings_002.JCP_CLIENT_ID, clientId);
        properties.put(JSLSettings_002.JCP_CLIENT_SECRET, clientSecret);
        properties.put(JSLSettings_002.JCP_CLIENT_CALLBACK, clientCallback);
        properties.put(JSLSettings_002.JSLSRV_ID, srvId);
        properties.put(JSLSettings_002.JSLSRV_NAME, clientId);
        properties.put(JSLSettings_002.JSLCOMM_LOCAL_ENABLED, LOC_COMM_ENABLED);
        JSL.Settings settings = FactoryJSL.loadSettings(properties, jslVersion);

        JSL jsl = FactoryJSL.createJSL(settings, jslVersion);
        registerListenersForSSE(jsl);
        jsl.startup();

        session.setAttribute("JSL-Instance", jsl);
        sessions.put(jsl, session);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        log.info(String.format("Registered HTTP session '%s' with instance id '%s' on client '%s:%d'", session.getId(), jsl.getServiceInfo().getInstanceId(), ipAddress, request.getRemotePort()));
        return jsl;
    }


    // Objects and components

    public List<JSLRemoteObject> listObjects(JSL jsl) {
        return jsl.getObjsMngr().getAllObjects();
    }

    public JSLRemoteObject getObj(JSL jsl, String objId) {
        JSLRemoteObject obj = jsl.getObjsMngr().getById(objId);
        if (obj == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Object with required id ('%s') not found.", objId));

        return obj;
    }

    public <T extends JSLComponent> T getComp(JSL jsl, String objId, String compPath, Class<T> compClass) {
        JSLRemoteObject obj = jsl.getObjsMngr().getById(objId);
        if (obj == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Object with required id ('%s') not found.", objId));

        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), new DefaultJSLComponentPath(compPath));
        if (comp == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Component '%s' on object '%s' not found.", compPath, objId));

        if (!compClass.isInstance(comp))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Component '%s' on object '%s' is not of %s type ('%s').", compPath, objId, compClass.getSimpleName(), comp.getClass().getSimpleName()));

        return compClass.cast(comp);
    }

    public JSLRemoteObject findObj(JSL jsl, String objId) {
        try {
            return getObj(jsl, objId);

        } catch (Throwable ignore) {
            return null;
        }
    }

    public <T extends JSLComponent> T findComp(JSL jsl, String objId, String compPath, Class<T> compClass) {
        try {
            return getComp(jsl, objId, compPath, compClass);

        } catch (Throwable ignore) {
            return null;
        }
    }


    // User

    public JSLUserMngr getUserMngr(JSL jsl) {
        return jsl.getUserMngr();
    }

    public String getLoginUrl(JSL jsl) {
        return jsl.getJCPClient().getAuthLoginUrl();
    }

    public String getLogoutUrl(JSL jsl, String redirectUrl) {
        return jsl.getJCPClient().getAuthLogoutUrl(redirectUrl);
    }

    public boolean login(JSL jsl, String code) throws StateException, JCPClient2.AuthenticationException {
        jsl.getJCPClient().setLoginCodeAndReconnect(code);
        return jsl.getJCPClient().isConnected();
    }

    public void logout(JSL jsl) {
        jsl.getJCPClient().userLogout();
    }


    // Service

    public JSL getJSL(JSL jsl) {
        return jsl;
    }


    // Permissions

    public static JOSPPerm.Type getObjPerm(JSLRemoteObject obj) {
        return obj.getPerms().getServicePerm(obj.getComm().isCloudConnected() ? JOSPPerm.Connection.LocalAndCloud : JOSPPerm.Connection.OnlyLocal);
    }

    public boolean serviceCanPerm(JSLRemoteObject obj, JOSPPerm.Type type) {
        if (obj.getInfo().getOwnerId().equals(JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString()))
            return true;

        JOSPPerm.Type objPermType = getObjPerm(obj);
        switch (type) {
            case CoOwner:
                return objPermType == JOSPPerm.Type.CoOwner;
            case Actions:
                return objPermType == JOSPPerm.Type.CoOwner
                        || objPermType == JOSPPerm.Type.Actions;
            case Status:
                return objPermType == JOSPPerm.Type.CoOwner
                        || objPermType == JOSPPerm.Type.Actions
                        || objPermType == JOSPPerm.Type.Status;
            case None:
                return true;
        }
        return false;
    }

    public JOSPPerm getPerm(JSLRemoteObject obj, String permId) {
        JOSPPerm perm = null;
        for (JOSPPerm p : obj.getPerms().getPerms())
            if (p.getId().equalsIgnoreCase(permId)) {
                perm = p;
                break;
            }
        if (perm == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Permission with required id ('%s') not found on object '%s'.", permId, obj.getId()));

        return perm;
    }


    // SSE Emitters

    public SseEmitter newEmitter(HttpSession session) throws JSLSpringException {
        JSL jsl;
        jsl = get(session);

        SseEmitter e = new SseEmitter(60 * 60 * 1000L);
        if (emitters.get(jsl) == null)
            emitters.put(jsl, new ArrayList<>());
        emitters.get(jsl).add(e);
        emitJCPFEClientConnected(jsl, e);
        return e;
    }

    private void emitJCPFEClientConnected(JSL jsl, SseEmitter emitter) {
        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data("Connected:" + jsl.getServiceInfo().getInstanceId())
                .id(String.valueOf(sseCount++));
        try {
            emitter.send(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void emit(JSL jsl, String data) {
        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data(data)
                .id(String.valueOf(sseCount++));

        if (emitters.get(jsl) == null || emitters.get(jsl).size() == 0) {
            log.warn(String.format("Error on send event '%s' (data) to not found emitter for '%s' JSL instance because emitter not found, check if remove JSL instance.", data, jsl.getServiceInfo().getFullId()));
            processEmitterError(jsl, null);
            return;
        }

        List<SseEmitter> jslEmitters = new ArrayList<>(emitters.get(jsl));
        for (SseEmitter emitter : jslEmitters)
            try {
                try {
                    emitter.send(event);
                    log.debug(String.format("Send event '%s' (data) to '%s' emitter for '%s' JSL instance.", data, emitter, jsl.getServiceInfo().getFullId()));

                } catch (IllegalStateException e) {
                    Thread.sleep(5000);
                    emitter.send(event);
                }

            } catch (Exception e) {
                log.warn(String.format("Error on send event '%s' (data) to '%s' emitter for '%s' JSL instance because %s, remove emitter.", data, emitter, jsl.getServiceInfo().getFullId(), e.getMessage()));
                processEmitterError(jsl, emitter);
            }
    }

    private void processEmitterError(JSL jsl, SseEmitter emitter) {
        if (emitter != null) {
            emitter.complete();
            emitters.get(jsl).remove(emitter);
        }

        if (emitters.get(jsl) == null) {
            JavaAssertions.makeWarning_Failed(String.format("Emitter list for '%s' JSL instance must not be null", jsl.getServiceInfo().getFullId()));
            return;
        }

        if (emitters.get(jsl).size() == 0) {
            try {
                jsl.shutdown();

            } catch (StateException e) {
                log.warn(String.format("Error on shutdown '%s' JSL instance because %s, remove emitter.", jsl, e.getMessage()));
            }

            emitters.remove(jsl);

            HttpSession session = sessions.get(jsl);
            session.removeAttribute("JSL-Instance");
            session.invalidate();
        }
    }

    private void registerListenersForSSE(JSL jsl) {
        jsl.getObjsMngr().addListener(new JSLObjsMngr.ObjsMngrListener() {

            @Override
            public void onObjAdded(JSLRemoteObject obj) {
                emit(jsl, "ObjAdd:" + obj.getId());

                obj.getComm().addListener(new ObjComm.RemoteObjectConnListener() {
                    @Override
                    public void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient) {
                    }

                    @Override
                    public void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient) {
                    }

                    @Override
                    public void onCloudConnected(JSLRemoteObject obj) {
                        emit(jsl, "ObjConnected:" + obj.getId());
                    }

                    @Override
                    public void onCloudDisconnected(JSLRemoteObject obj) {
                        emit(jsl, "ObjDisconnected:" + obj.getId());
                    }
                });
                obj.getInfo().addListener(new ObjInfo.RemoteObjectInfoListener() {
                    @Override
                    public void onNameChanged(JSLRemoteObject obj, String newName, String oldName) {
                        emit(jsl, "ObjUpd:" + obj.getId() + ";what:name");
                    }

                    @Override
                    public void onOwnerIdChanged(JSLRemoteObject obj, String newOwnerId, String oldOwnerId) {
                        emit(jsl, "ObjUpd:" + obj.getId() + ";what:owner");
                    }

                    @Override
                    public void onJODVersionChanged(JSLRemoteObject obj, String newJODVersion, String oldJODVersion) {
                        emit(jsl, "ObjUpd:" + obj.getId() + ";what:jodVersion");
                    }

                    @Override
                    public void onModelChanged(JSLRemoteObject obj, String newModel, String oldModel) {
                        emit(jsl, "ObjUpd:" + obj.getId() + ";what:model");
                    }

                    @Override
                    public void onBrandChanged(JSLRemoteObject obj, String newBrand, String oldBrand) {
                        emit(jsl, "ObjUpd:" + obj.getId() + ";what:brand");
                    }

                    @Override
                    public void onLongDescrChanged(JSLRemoteObject obj, String newLongDescr, String oldLongDescr) {
                        emit(jsl, "ObjUpd:" + obj.getId() + ";what:longDescr");
                    }
                });
                obj.getStruct().addListener(new ObjStruct.RemoteObjectStructListener() {
                    @Override
                    public void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot) {
                        registerListenersForSSE(jsl, obj.getStruct().getStructure());
                    }
                });
            }

            @Override
            public void onObjRemoved(JSLRemoteObject obj) {
                emit(jsl, "ObjRem:" + obj.getId());
            }

        });
    }

    private void registerListenersForSSE(JSL jsl, JSLComponent component) {
        if (component instanceof JSLBooleanState)
            ((JSLBooleanState) component).addListener(new JSLBooleanState.BooleanStateListener() {

                @Override
                public void onStateChanged(JSLBooleanState component, boolean newState, boolean oldState) {
                    emit(jsl, "StateUpd:" + component.getRemoteObject().getId() + ";Comp:" + component.getPath().getString());
                }

            });
        else if (component instanceof JSLRangeState)
            ((JSLRangeState) component).addListener(new JSLRangeState.RangeStateListener() {

                @Override
                public void onStateChanged(JSLRangeState component, double newState, double oldState) {
                    emit(jsl, "StateUpd:" + component.getRemoteObject().getId() + ";Comp:" + component.getPath().getString());
                }

                @Override
                public void onMinReached(JSLRangeState component, double state, double min) {
                }

                @Override
                public void onMaxReached(JSLRangeState component, double state, double max) {
                }

            });
        else if (component instanceof JSLContainer) {
            for (JSLComponent c : ((JSLContainer) component).getComponents())
                registerListenersForSSE(jsl, c);
        }
    }


    // Heartbeat timer

    private void startHeartBeatTimer() {
        heartbeatTimer = new Timer();
        heartbeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendHeartBeat();
            }
        }, heartbeatTimerDelaySeconds * 1000, heartbeatTimerDelaySeconds * 1000);
    }

    private void stopHeartBeatTimer() {
        heartbeatTimer.cancel();
        heartbeatTimer = null;
    }

    private void sendHeartBeat() {
        Set<Map.Entry<JSL, List<SseEmitter>>> tmpList = new HashSet<>(emitters.entrySet());
        for (Map.Entry<JSL, List<SseEmitter>> entry : tmpList) {
            List<SseEmitter> tmpList2 = new ArrayList<>(entry.getValue());
            for (SseEmitter emitter : tmpList2) {
                try {
                    emitter.send(HEART_BEAT);

                } catch (IOException | IllegalStateException e) {
                    log.warn(String.format("Error on send event '%s' (heartbeat) to '%s' emitter for '%s' JSL instance because %s, remove emitter.", HEART_BEAT, emitter, entry.getKey(), e.getMessage()));
                    processEmitterError(entry.getKey(), emitter);
                }
            }
        }
    }


    // Exceptions

    public static class JSLSpringException extends Throwable {
        public JSLSpringException(String msg, Throwable t) {
            super(msg, t);
        }
    }

}

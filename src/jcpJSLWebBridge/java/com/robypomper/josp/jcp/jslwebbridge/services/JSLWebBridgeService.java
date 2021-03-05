package com.robypomper.josp.jcp.jslwebbridge.services;

import com.robypomper.josp.jcp.jslwebbridge.exceptions.*;
import com.robypomper.josp.jcp.jslwebbridge.webbridge.JSLParams;
import com.robypomper.josp.jcp.jslwebbridge.webbridge.JSLWebBridge;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.DefaultJSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.protocol.JOSPPerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;

@Service
public class JSLWebBridgeService {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLWebBridgeService.class);
    private final JSLWebBridge webBridge;


    // Constructors

    @Autowired
    protected JSLWebBridgeService(JSLParams jslParams,
                                  @Value("${jcp.jsl.remove.delay:900}") int jslRemoveScheduledDelaySeconds,
                                  @Value("${jcp.jsl.heartbeat.delay:60}") int heartbeatTimerDelaySeconds) {
        webBridge = new JSLWebBridge(jslParams, jslRemoveScheduledDelaySeconds, heartbeatTimerDelaySeconds);
    }


    @PreDestroy
    public void destroy() {
        webBridge.dismiss();
        log.trace("JSL webBridge service destroyed");
    }


    // Getters

    public JSL getJSL(String sessionId) throws ResponseStatusException {
        try {
            return webBridge.getJSLInstance(sessionId);

        } catch (JSLNotInitForSessionException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
        }
    }

    public JSLRemoteObject getJSLObj(String sessionId, String objId) throws ResponseStatusException {
        JSL jsl = getJSL(sessionId);

        JSLRemoteObject obj = jsl.getObjsMngr().getById(objId);
        if (obj == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Required obj '%s' not found", objId));

        return obj;
    }

    public <T extends JSLComponent> T getJSLObjComp(String sessionId, String objId, String compPath, Class<T> compClass) throws ResponseStatusException {
        JSLRemoteObject obj = getJSLObj(sessionId, objId);

        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), new DefaultJSLComponentPath(compPath));
        if (comp == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Required component '%s' on object '%s' not found.", compPath, objId));

        if (!compClass.isInstance(comp))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Component '%s' on object '%s' is not %s component type.", compPath, objId, compClass.getSimpleName()));

        return compClass.cast(comp);
    }

    public JOSPPerm getJSLObjPerm(String sessionId, String objId, String permId) throws ResponseStatusException {
        JSLRemoteObject obj = getJSLObj(sessionId, objId);

        JOSPPerm perm = null;
        for (JOSPPerm permSearch : obj.getPerms().getPerms()) {
            if (permSearch.getId().equals(permId)) {
                perm = permSearch;
                break;
            }
        }

        if (perm == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Required permission '%s' on object '%s' not found.", permId, objId));

        return perm;
    }


    // Creators

    public JSL createJSL(String sessionId, String clientId, String clientSecret) throws ResponseStatusException {
        try {
            return webBridge.createJSLInstance(sessionId, clientId, clientSecret);

        } catch (JSLAlreadyInitForSessionException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());

        } catch (JSLErrorOnInitException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }

    public SseEmitter createJSLEmitter(String sessionId) throws ResponseStatusException {
        try {
            return webBridge.createSSEEmitter(sessionId);

        } catch (JSLNotInitForSessionException | EmittersNotInitForSessionException | EmitterAlreadyInitForSessionException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
        }
    }

}

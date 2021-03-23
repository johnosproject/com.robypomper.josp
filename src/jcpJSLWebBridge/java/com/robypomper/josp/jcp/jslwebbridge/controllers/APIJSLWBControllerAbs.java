package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLNotInitForSessionException;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.DefaultJSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.protocol.JOSPPerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.spring.web.plugins.Docket;

public class APIJSLWBControllerAbs {

    // Class constants

    private static final String LOG_ERR_JSL_NOT_INIT = "Can't %s for '%s' session because JSL instance not initialized";
    private static final String LOG_ERR_MISSING_PERMS_ON_ACTION = "Permission denied to current user/service on send '%s' action commands to '%s' object.";
    private static final String LOG_ERR_OBJ_NOT_CONN_ON_ACTION = "Can't send '%s' action commands because '%s' object is not connected.";
    private static final String LOG_ERR_MISSING_PERMS_ON_HISTORY = "Permission denied to current user/service on send '%s' history request to '%s' object.";
    private static final String LOG_ERR_OBJ_NOT_CONN_ON_HISTORY = "Can't send '%s' history request because '%s' object is not connected.";
    private static final String LOG_ERR_MISSING_PERMS = "Permission denied to current user/service on %s to '%s' object.";
    private static final String LOG_ERR_OBJ_NOT_CONN = "Can't %s because '%s' object is not connected.";


    // Internal vars

    // swagger
    @Autowired
    private SwaggerConfigurer swagger;
    private final String swaggerAPIName;
    private final String swaggerAPIVers;
    private final String swaggerAPISuffix;
    private final String swaggerSubGroupName;
    private final String swaggerSubGroupDescr;
    @Autowired
    private JSLWebBridgeService webBridgeService;


    // Constructor

    protected APIJSLWBControllerAbs(String swaggerAPIName, String swaggerAPIVers, String swaggerAPISuffix, String swaggerSubGroupName, String swaggerSubGroupDescr) {
        this.swaggerAPIName = swaggerAPIName;
        this.swaggerAPIVers = swaggerAPIVers;
        this.swaggerAPISuffix = swaggerAPISuffix;
        this.swaggerSubGroupName = swaggerSubGroupName;
        this.swaggerSubGroupDescr = swaggerSubGroupDescr;
    }


    // Swagger configs

    public Docket swaggerConfig() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(swaggerSubGroupName, swaggerSubGroupDescr);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(swaggerAPIName, swaggerAPIVers, swaggerAPISuffix, sg), swagger.getUrlBaseAuth());
    }


    // Getter utils

    protected JSL getJSL(String sessionId, String request) {
        try {
            return webBridgeService.getJSL(sessionId);

        } catch (JSLNotInitForSessionException e) {
            throw jslNotInitForSessionException(sessionId, request);
        }
    }

    protected JSLRemoteObject getJSLObj(String sessionId, String objId, String request) {
        JSL jsl = getJSL(sessionId,request);

        JSLRemoteObject obj = jsl.getObjsMngr().getById(objId);
        if (obj == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Required obj '%s' not found", objId));

        return obj;
    }

    protected <T extends JSLComponent> T getJSLObjComp(String sessionId, String objId, String compPath, Class<T> compClass, String request) {
        JSLRemoteObject obj = getJSLObj(sessionId, objId, request);

        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), new DefaultJSLComponentPath(compPath));
        if (comp == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Required component '%s' on object '%s' not found.", compPath, objId));

        if (!compClass.isInstance(comp))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Component '%s' on object '%s' is not %s component type.", compPath, objId, compClass.getSimpleName()));

        return compClass.cast(comp);
    }

    protected JOSPPerm getJSLObjPerm(String sessionId, String objId, String permId, String request) {
        JSLRemoteObject obj = getJSLObj(sessionId, objId,request);

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

    // Exception utils

    protected ResponseStatusException jslNotInitForSessionException(String sessionId, String request) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(LOG_ERR_JSL_NOT_INIT, request, sessionId));
    }

    protected ResponseStatusException missingPermissionsExceptionOnSendAction(String objId, String compPath, JSLRemoteObject.MissingPermission e) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(LOG_ERR_MISSING_PERMS_ON_ACTION, compPath, objId), e);
    }

    protected ResponseStatusException objNotConnectedExceptionOnSendAction(String objId, String compPath, JSLRemoteObject.ObjectNotConnected e) {
        return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format(LOG_ERR_OBJ_NOT_CONN_ON_ACTION, compPath, objId), e);
    }

    protected ResponseStatusException missingPermissionsExceptionOnHistoryRequest(String objId, String compPath, JSLRemoteObject.MissingPermission e) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(LOG_ERR_MISSING_PERMS_ON_HISTORY, compPath, objId), e);
    }

    protected ResponseStatusException objNotConnectedExceptionOnHistoryRequest(String objId, String compPath, JSLRemoteObject.ObjectNotConnected e) {
        return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format(LOG_ERR_OBJ_NOT_CONN_ON_HISTORY, compPath, objId), e);
    }

    protected ResponseStatusException missingPermissionsException(String objId, String request, JSLRemoteObject.MissingPermission e) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(LOG_ERR_MISSING_PERMS, request, objId), e);
    }

    protected ResponseStatusException objNotConnectedException(String objId, String request, JSLRemoteObject.ObjectNotConnected e) {
        return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format(LOG_ERR_OBJ_NOT_CONN, request, objId), e);
    }

}

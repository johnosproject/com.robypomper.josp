package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.spring.web.plugins.Docket;

public class APIJSLWBControllerAbs {

    // Class constants

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


    // Exception utils

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

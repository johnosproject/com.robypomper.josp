package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.core.service;

import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.service.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.service.Paths20;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.controllers.ControllerImplJSL;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jsl.JSL;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;


/**
 * JCP JSL Web Bridge - Service 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImplJSL {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;


    // Constructors

    public Controller20() {
        super(Paths20.API_NAME, Paths20.API_VER, JCPJSLWBVersions.API_NAME, Paths20.DOCS_NAME, Paths20.DOCS_DESCR);
    }


    // Methods

    @GetMapping(path = Paths20.FULL_PATH_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_DETAILS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Params20.JOSPSrvHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "JSL Instance not initialized")
    })
    public ResponseEntity<Params20.JOSPSrvHtml> jsonServiceDetails(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId(), "get service");

        Params20.JOSPSrvHtml jospSrv = new Params20.JOSPSrvHtml();
        jospSrv.name = jsl.getServiceInfo().getSrvName();
        jospSrv.state = jsl.getState().toString();
        jospSrv.stateJCP = jsl.getJCPClient().getState().toString();
        jospSrv.isJCPConnected = jsl.getJCPClient().isConnected();
        jospSrv.stateCloud = jsl.getCommunication().getCloudConnection().getState().toString();
        jospSrv.isCloudConnected = jsl.getCommunication().getCloudConnection().getState().isConnected();
        jospSrv.stateLocal = jsl.getCommunication().getLocalConnections().getState().toString();
        jospSrv.isLocalRunning = jsl.getCommunication().getLocalConnections().isRunning();
        jospSrv.srvId = jsl.getServiceInfo().getSrvId();
        jospSrv.usrId = jsl.getServiceInfo().getUserId();
        jospSrv.instId = jsl.getServiceInfo().getInstanceId();
        jospSrv.jslVersion = jsl.version();
        jospSrv.supportedJCPAPIsVersions = jsl.versionsJCPAPIs();
        jospSrv.supportedJOSPProtocolVersions = jsl.versionsJOSPProtocol();
        jospSrv.supportedJODVersions = jsl.versionsJOSPObject();
        jospSrv.sessionId = session.getId();
        return ResponseEntity.ok(jospSrv);
    }

}

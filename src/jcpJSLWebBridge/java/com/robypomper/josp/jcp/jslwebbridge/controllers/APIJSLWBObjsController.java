package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jcp.params.jslwb.JOSPObjHtml;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBObjs;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBObjs.SubGroupObjs.NAME})
public class APIJSLWBObjsController extends APIJSLWBControllerAbs {

    // Internal var

    private static final Logger log = LoggerFactory.getLogger(APIJSLWBObjsController.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;


    // Constructors

    public APIJSLWBObjsController() {
        super(APIJSLWBObjs.API_NAME, APIJSLWBObjs.API_VER, JCPJSLWBVersions.API_NAME, APIJSLWBObjs.SubGroupObjs.NAME, APIJSLWBObjs.SubGroupObjs.DESCR);
    }


    // Swagger configs

    @Bean
    public Docket swaggerConfig_APIJSLWBObjs() {
        return swaggerConfig();
    }


    // Methods - Objs List

    @GetMapping(path = APIJSLWBObjs.FULL_PATH_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBObjs.DESCR_PATH_LIST)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<JOSPObjHtml>> jsonObjectsList(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        List<JOSPObjHtml> objHtml = new ArrayList<>();
        for (JSLRemoteObject o : jsl.getObjsMngr().getAllObjects())
            objHtml.add(new JOSPObjHtml(o));

        return ResponseEntity.ok(objHtml);
    }


    // Methods - Objs Details

    @GetMapping(path = APIJSLWBObjs.FULL_PATH_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBObjs.DESCR_PATH_DETAILS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<JOSPObjHtml> jsonObjectDetails(@ApiIgnore HttpSession session,
                                                         @PathVariable("obj_id") String objId) {
        JSLRemoteObject obj = webBridgeService.getJSLObj(session.getId(), objId);
        return ResponseEntity.ok(new JOSPObjHtml(obj));
    }


    // Set owner and name

    @PostMapping(path = APIJSLWBObjs.FULL_PATH_OWNER, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBObjs.DESCR_PATH_OWNER)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectOwner(@ApiIgnore HttpSession session,
                                                   @PathVariable("obj_id") String objId,
                                                   @RequestParam("new_owner") String newOwner) {
        JSLRemoteObject obj = webBridgeService.getJSLObj(session.getId(), objId);

        try {
            obj.getInfo().setOwnerId(newOwner);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "set owner id", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "set owner id", e);
        }

        return ResponseEntity.ok(true);
    }

    @PostMapping(path = APIJSLWBObjs.FULL_PATH_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBObjs.DESCR_PATH_NAME)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectName(@ApiIgnore HttpSession session,
                                                  @PathVariable("obj_id") String objId,
                                                  @RequestParam("new_name") String newName) {
        JSLRemoteObject obj = webBridgeService.getJSLObj(session.getId(), objId);

        try {
            obj.getInfo().setName(newName);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "set name", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "set name", e);
        }

        return ResponseEntity.ok(true);
    }


    // Events

    @GetMapping(path = APIJSLWBObjs.FULL_PATH_EVENTS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBObjs.DESCR_PATH_EVENTS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<JOSPEvent>> jsonObjectEvents(@ApiIgnore HttpSession session,
                                                            @PathVariable("obj_id") String objId,
                                                            HistoryLimits limits) {
        JSLRemoteObject obj = webBridgeService.getJSLObj(session.getId(), objId);

        try {
            return ResponseEntity.ok(obj.getInfo().getEventsHistory(limits, 20));

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "get events", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "get events", e);
        }
    }

}

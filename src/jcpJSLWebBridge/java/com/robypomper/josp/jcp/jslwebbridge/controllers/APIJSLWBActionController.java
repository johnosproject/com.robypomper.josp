package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jcp.params.jslwb.JOSPObjHtml;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBAction;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpSession;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBAction.SubGroupAction.NAME})
public class APIJSLWBActionController extends APIJSLWBControllerAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(APIJSLWBActionController.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;


    // Constructors

    public APIJSLWBActionController() {
        super(APIJSLWBAction.API_NAME, APIJSLWBAction.API_VER, JCPJSLWBVersions.API_NAME, APIJSLWBAction.SubGroupAction.NAME, APIJSLWBAction.SubGroupAction.DESCR);
    }


    // Swagger configs

    @Bean
    public Docket swaggerConfig_APIJSLWBAction() {
        return swaggerConfig();
    }


    // Methods - Boolean

    @GetMapping(path = APIJSLWBAction.FULL_PATH_BOOL_SWITCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBAction.DESCR_PATH_BOOL_SWITCH)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonBoolSwitch(@ApiIgnore HttpSession session,
                                                  @PathVariable("obj_id") String objId,
                                                  @PathVariable("comp_path") String compPath) {
        JSLBooleanAction comp = webBridgeService.getJSLObjComp(session.getId(), objId, compPath, JSLBooleanAction.class);

        try {
            comp.execSwitch();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = APIJSLWBAction.FULL_PATH_BOOL_TRUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBAction.DESCR_PATH_BOOL_TRUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonBoolTrue(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLBooleanAction comp = webBridgeService.getJSLObjComp(session.getId(), objId, compPath, JSLBooleanAction.class);

        try {
            comp.execSetTrue();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = APIJSLWBAction.FULL_PATH_BOOL_FALSE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBAction.DESCR_PATH_BOOL_FALSE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonBoolFalse(@ApiIgnore HttpSession session,
                                                 @PathVariable("obj_id") String objId,
                                                 @PathVariable("comp_path") String compPath) {
        JSLBooleanAction comp = webBridgeService.getJSLObjComp(session.getId(), objId, compPath, JSLBooleanAction.class);

        try {
            comp.execSetFalse();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }


    // Methods - Range

    @PostMapping(path = APIJSLWBAction.FULL_PATH_RANGE_SET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBAction.DESCR_PATH_RANGE_SET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeSet_POST(@ApiIgnore HttpSession session,
                                                     @PathVariable("obj_id") String objId,
                                                     @PathVariable("comp_path") String compPath,
                                                     @RequestParam("val") String val) {
        return jsonRangeSet(session, objId, compPath, val);
    }

    @GetMapping(path = APIJSLWBAction.FULL_PATH_RANGE_SETg, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBAction.DESCR_PATH_RANGE_SETg)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeSet(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath,
                                                @PathVariable("val") String val) {
        Double dVal = JavaFormatter.strToDouble(val);
        if (dVal == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request param 'val' can't be cast to double (%s), action '%s' on '%s' object not executed.", val, compPath, objId));

        JSLRangeAction comp = webBridgeService.getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class);

        try {
            comp.execSetValue(dVal);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = APIJSLWBAction.FULL_PATH_RANGE_INC, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBAction.DESCR_PATH_RANGE_INC)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeInc(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = webBridgeService.getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class);

        try {
            comp.execIncrease();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = APIJSLWBAction.FULL_PATH_RANGE_DEC, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBAction.DESCR_PATH_RANGE_DEC)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeDec(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = webBridgeService.getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class);

        try {
            comp.execDecrease();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = APIJSLWBAction.FULL_PATH_RANGE_MAX, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBAction.DESCR_PATH_RANGE_MAX)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeMax(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = webBridgeService.getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class);

        try {
            comp.execSetMax();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = APIJSLWBAction.FULL_PATH_RANGE_MIN, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBAction.DESCR_PATH_RANGE_MIN)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeMin(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = webBridgeService.getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class);

        try {
            comp.execSetMin();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = APIJSLWBAction.FULL_PATH_RANGE_1_2, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBAction.DESCR_PATH_RANGE_1_2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRange1_2(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = webBridgeService.getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class);

        try {
            double half = comp.getMin() + ((comp.getMax() - comp.getMin()) / 2);
            comp.execSetValue(half);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = APIJSLWBAction.FULL_PATH_RANGE_1_3, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBAction.DESCR_PATH_RANGE_1_3)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRange1_3(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = webBridgeService.getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class);

        try {
            double fist_third = comp.getMin() + ((comp.getMax() - comp.getMin()) / 3);
            comp.execSetValue(fist_third);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = APIJSLWBAction.FULL_PATH_RANGE_2_3, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBAction.DESCR_PATH_RANGE_2_3)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRange2_3(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = webBridgeService.getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class);

        try {
            double second_third = comp.getMin() + ((comp.getMax() - comp.getMin()) / 3) + ((comp.getMax() - comp.getMin()) / 3);
            comp.execSetValue(second_third);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

}

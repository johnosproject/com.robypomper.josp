package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jcp.params.jslwb.JOSPObjHtml;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBState;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPStatusHistory;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpSession;
import java.util.List;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBState.SubGroupState.NAME})
public class APIJSLWBStateController extends APIJSLWBControllerAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(APIJSLWBStateController.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;


    // Constructors

    public APIJSLWBStateController() {
        super(APIJSLWBState.API_NAME, APIJSLWBState.API_VER, JCPJSLWBVersions.API_NAME, APIJSLWBState.SubGroupState.NAME, APIJSLWBState.SubGroupState.DESCR);
    }


    // Swagger configs

    @Bean
    public Docket swaggerConfig_APIJSLWBState() {
        return swaggerConfig();
    }


    // Methods - Boolean

    @GetMapping(path = APIJSLWBState.FULL_PATH_BOOL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBState.DESCR_PATH_BOOL)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonBool(@ApiIgnore HttpSession session,
                                            @PathVariable("obj_id") String objId,
                                            @PathVariable("comp_path") String compPath) {
        JSLBooleanState comp = getJSLObjComp(session.getId(), objId, compPath, JSLBooleanState.class, "get boolean component state");
        return ResponseEntity.ok(comp.getState());
    }


    // Methods - Range

    @GetMapping(path = APIJSLWBState.FULL_PATH_RANGE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBState.DESCR_PATH_RANGE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Double> jsonRange(@ApiIgnore HttpSession session,
                                            @PathVariable("obj_id") String objId,
                                            @PathVariable("comp_path") String compPath) {
        JSLRangeState comp = getJSLObjComp(session.getId(), objId, compPath, JSLRangeState.class,"get range component state");
        return ResponseEntity.ok(comp.getState());
    }


    // Methods - History

    @GetMapping(path = APIJSLWBState.FULL_PATH_STATUS_HISTORY, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBState.DESCR_PATH_STATUS_HISTORY)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<JOSPStatusHistory>> jsonStatusHistory(@ApiIgnore HttpSession session,
                                                                     @PathVariable("obj_id") String objId,
                                                                     @PathVariable("comp_path") String compPath,
                                                                     HistoryLimits limits) {
        JSLRemoteObject obj = getJSLObj(session.getId(),objId,"get component state history");
        JSLComponent comp = getJSLObjComp(session.getId(),objId,compPath, JSLComponent.class,"get component state history");

        try {
            return ResponseEntity.ok(obj.getStruct().getComponentHistory(comp, limits, 20));

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnHistoryRequest(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnHistoryRequest(objId, compPath, e);
        }
    }

}

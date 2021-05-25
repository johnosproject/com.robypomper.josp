package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.core.objects.states;

import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.states.Paths20;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.controllers.ControllerImplJSL;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * JCP JSL Web Bridge - Objects / States 2.0
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


    // Methods - Boolean

    @GetMapping(path = Paths20.FULL_PATH_BOOL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_BOOL)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonBool(@ApiIgnore HttpSession session,
                                            @PathVariable(Paths20.PARAM_OBJ) String objId,
                                            @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLBooleanState comp = getJSLObjComp(session.getId(), objId, compPath, JSLBooleanState.class, "get boolean component state");
        return ResponseEntity.ok(comp.getState());
    }


    // Methods - Range

    @GetMapping(path = Paths20.FULL_PATH_RANGE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_RANGE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Double.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Double> jsonRange(@ApiIgnore HttpSession session,
                                            @PathVariable(Paths20.PARAM_OBJ) String objId,
                                            @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLRangeState comp = getJSLObjComp(session.getId(), objId, compPath, JSLRangeState.class, "get range component state");
        return ResponseEntity.ok(comp.getState());
    }


    // Methods - History

    @GetMapping(path = Paths20.FULL_PATH_STATUS_HISTORY, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_HISTORY)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPStatusHistory.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<JOSPStatusHistory>> jsonStatusHistory(@ApiIgnore HttpSession session,
                                                                     @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                                     @PathVariable(Paths20.PARAM_COMP) String compPath,
                                                                     HistoryLimits limits) {
        JSLRemoteObject obj = getJSLObj(session.getId(), objId, "get component state history");
        JSLComponent comp = getJSLObjComp(session.getId(), objId, compPath, JSLComponent.class, "get component state history");

        try {
            return ResponseEntity.ok(obj.getStruct().getComponentHistory(comp, limits, 20));

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnHistoryRequest(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnHistoryRequest(objId, compPath, e);
        }
    }

}

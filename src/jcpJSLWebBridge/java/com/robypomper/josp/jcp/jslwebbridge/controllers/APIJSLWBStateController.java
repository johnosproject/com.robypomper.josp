package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.info.JCPFEVersions;
import com.robypomper.josp.jcp.jslwebbridge.jsl.JSLSpringService;
import com.robypomper.josp.jcp.params.jslwb.JOSPObjHtml;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBState;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPStatusHistory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
public class APIJSLWBStateController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIJSLWBState() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIJSLWBState.SubGroupState.NAME, APIJSLWBState.SubGroupState.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIJSLWBState.API_NAME, APIJSLWBState.API_VER, JCPFEVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods - Boolean

    @GetMapping(path = APIJSLWBState.FULL_PATH_BOOL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonBool(@ApiIgnore HttpSession session,
                                            @PathVariable("obj_id") String objId,
                                            @PathVariable("comp_path") String compPath) {
        JSLBooleanAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLBooleanAction.class);
        return ResponseEntity.ok(comp.getState());
    }


    // Methods - Range

    @GetMapping(path = APIJSLWBState.FULL_PATH_RANGE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Double> jsonRange(@ApiIgnore HttpSession session,
                                            @PathVariable("obj_id") String objId,
                                            @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLRangeAction.class);
        return ResponseEntity.ok(comp.getState());
    }


    // Methods - History

    @GetMapping(path = APIJSLWBState.FULL_STATUS_HISTORY, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<JOSPStatusHistory>> jsonStatusHistory(@ApiIgnore HttpSession session,
                                                                     @PathVariable("obj_id") String objId,
                                                                     @PathVariable("comp_path") String compPath,
                                                                     HistoryLimits limits) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);
        JSLComponent comp = obj.getStruct().getComponent(compPath);
        try {
            return ResponseEntity.ok(obj.getStruct().getComponentHistory(comp, limits, 20));
        } catch (JSLRemoteObject.ObjectNotConnected | JSLRemoteObject.MissingPermission objectNotConnected) {
            objectNotConnected.printStackTrace();
        }
        return null;
    }

}

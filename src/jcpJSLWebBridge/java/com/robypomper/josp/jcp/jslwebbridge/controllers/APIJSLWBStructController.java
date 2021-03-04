package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jcp.params.jslwb.*;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBStruct;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBStruct.SubGroupStructure.NAME})
public class APIJSLWBStructController extends APIJSLWBControllerAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(APIJSLWBStructController.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;


    // Constructors

    public APIJSLWBStructController() {
        super(APIJSLWBStruct.API_NAME, APIJSLWBStruct.API_VER, JCPJSLWBVersions.API_NAME, APIJSLWBStruct.SubGroupStructure.NAME, APIJSLWBStruct.SubGroupStructure.DESCR);
    }


    // Swagger configs

    @Bean
    public Docket swaggerConfig_APIJSLWBStruct() {
        return swaggerConfig();
    }


    // Methods - Obj's Structure

    @GetMapping(path = APIJSLWBStruct.FULL_PATH_STRUCT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBStruct.DESCR_PATH_STRUCT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<JOSPStructHtml> jsonObjectStructure(@ApiIgnore HttpSession session,
                                                              @PathVariable("obj_id") String objId) {
        JSLRemoteObject obj = webBridgeService.getJSLObj(session.getId(), objId);

        return ResponseEntity.ok(new JOSPStructHtml(obj.getStruct().getStructure(), true));     // ToDo add MissingPermission exception to getStructure() method
    }


    // Obj's compos

    @GetMapping(path = APIJSLWBStruct.FULL_PATH_COMP, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBStruct.DESCR_PATH_COMP)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<JOSPComponentHtml> jsonObjectComponent(@ApiIgnore HttpSession session,
                                                                 @PathVariable("obj_id") String objId,
                                                                 @PathVariable("comp_path") String compPath) {
        JSLRemoteObject obj = webBridgeService.getJSLObj(session.getId(), objId);

        JSLComponent comp = compPath.equals("-") ? obj.getStruct().getStructure() : webBridgeService.getJSLObjComp(session.getId(), objId, compPath, JSLComponent.class);
        return ResponseEntity.ok(generateJOSPComponentHtml(comp));
    }


    // Shared structure support methods

    public static List<JOSPComponentHtml> convert(Collection<JSLComponent> subComps) {
        List<JOSPComponentHtml> subCompsHtml = new ArrayList<>();
        for (JSLComponent sc : subComps)
            subCompsHtml.add(generateJOSPComponentHtml(sc));
        return subCompsHtml;
    }

    private static JOSPComponentHtml generateJOSPComponentHtml(JSLComponent comp) {
        if (comp instanceof JSLContainer)
            return new JOSPContainerHtml((JSLContainer) comp, true);

        // Actions
        if (comp instanceof JSLBooleanAction)
            return new JOSPBooleanActionHtml((JSLBooleanAction) comp);
        if (comp instanceof JSLRangeAction)
            return new JOSPRangeActionHtml((JSLRangeAction) comp);

        // States
        if (comp instanceof JSLBooleanState)
            return new JOSPBooleanStateHtml((JSLBooleanState) comp);
        if (comp instanceof JSLRangeState)
            return new JOSPRangeStateHtml((JSLRangeState) comp);

        return new JOSPComponentHtml(comp);
    }

}

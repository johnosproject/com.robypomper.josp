package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.info.JCPFEVersions;
import com.robypomper.josp.jcp.jslwebbridge.jsl.JSLSpringService;
import com.robypomper.josp.jcp.params.jslwb.*;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBStruct;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.JOSPPerm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBStruct.SubGroupStructure.NAME})
public class APIJSLWBStructController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIJSLWBStruct() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIJSLWBStruct.SubGroupStructure.NAME, APIJSLWBStruct.SubGroupStructure.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIJSLWBStruct.API_NAME, APIJSLWBStruct.API_VER, JCPFEVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods - Obj's Structure

    @GetMapping(path = APIJSLWBStruct.FULL_PATH_STRUCT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<JOSPStructHtml> jsonObjectStructure(@ApiIgnore HttpSession session,
                                                              @PathVariable("obj_id") String objId) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (!jslService.serviceCanPerm(obj, JOSPPerm.Type.Status))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Access denied to current user/service on access '%s' object's structure.", objId));

        return ResponseEntity.ok(new JOSPStructHtml(obj.getStruct().getStructure(), true));
    }


    // Obj's compos

    @GetMapping(path = APIJSLWBStruct.FULL_PATH_COMP, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<JOSPComponentHtml> jsonObjectComponent(@ApiIgnore HttpSession session,
                                                                 @PathVariable("obj_id") String objId,
                                                                 @PathVariable("comp_path") String compPath) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (!jslService.serviceCanPerm(obj, JOSPPerm.Type.Status))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Access denied to current user/service on access '%s' object's structure.", objId));

        JSLComponent comp = compPath.equals("-") ? obj.getStruct().getStructure() : jslService.getComp(jslService.getHttp(session), objId, compPath, JSLComponent.class);
        return ResponseEntity.ok(generateJOSPComponentHtml(comp));
    }


    // Shared structures


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

package com.robypomper.josp.jcp.fe.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.jcp.fe.HTMLUtils;
import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jcp.params.fe.*;
import com.robypomper.josp.jcp.paths.fe.APIFEObjs;
import com.robypomper.josp.jcp.paths.fe.APIFEStruct;
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
@Api(tags = {APIFEStruct.SubGroupStructure.NAME})
public class APIFEStructController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIFEStruct() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIFEStruct.SubGroupStructure.NAME, APIFEStruct.SubGroupStructure.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIFEStruct.API_NAME, APIFEStruct.API_VER, sg), swagger.getUrlBaseAuth());
    }


    // Methods - Obj's Structure

    @GetMapping(path = APIFEStruct.FULL_PATH_STRUCT, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(path = APIFEStruct.FULL_PATH_STRUCT, produces = MediaType.TEXT_HTML_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlObjectStructure(@ApiIgnore HttpSession session,
                                      @PathVariable("obj_id") String objId) {
        JOSPStructHtml structHtml = jsonObjectStructure(session, objId).getBody();
        if (structHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error on get '%s' object's structure.", objId));

        try {
            return HTMLUtils.toHTMLFormattedJSON(structHtml,
                    String.format("%s Object's Structure", jslService.getObj(jslService.getHttp(session), objId).getName()),
                    String.format("<a href=\"%s\">Object</a>", APIFEObjs.FULL_PATH_DETAILS(objId)));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get '%s' object's structure on formatting response (%s).", objId, e.getMessage()), e);
        }
    }


    // Obj's compos

    @GetMapping(path = APIFEStruct.FULL_PATH_COMP, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(path = APIFEStruct.FULL_PATH_COMP, produces = MediaType.TEXT_HTML_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlObjectComponent(@ApiIgnore HttpSession session,
                                      @PathVariable("obj_id") String objId,
                                      @PathVariable("comp_path") String compPath) {
        JOSPComponentHtml compHtml = jsonObjectComponent(session, objId, compPath).getBody();
        if (compHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error on get '%s' object's component '%s'.", objId, compPath));

        try {
            return HTMLUtils.toHTMLFormattedJSON(compHtml,
                    String.format("%s Object's Component %s", jslService.getObj(jslService.getHttp(session), objId).getName(), compPath),
                    String.format("<a href=\"%s\">Object</a>\n<a href=\"%s\">Object's structure</a>", APIFEObjs.FULL_PATH_DETAILS(objId), APIFEStruct.FULL_PATH_STRUCT(objId)));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get '%s' object's structure on formatting response (%s).", objId, e.getMessage()), e);
        }
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

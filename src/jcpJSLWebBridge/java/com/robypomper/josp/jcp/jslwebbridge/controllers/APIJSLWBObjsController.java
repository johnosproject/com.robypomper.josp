package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.info.JCPFEVersions;
import com.robypomper.josp.jcp.jslwebbridge.jsl.JSLSpringService;
import com.robypomper.josp.jcp.params.jslwb.JOSPObjHtml;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBObjs;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBObjs.SubGroupObjs.NAME})
public class APIJSLWBObjsController {

    // Internal var

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIJSLWBObjs() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIJSLWBObjs.SubGroupObjs.NAME, APIJSLWBObjs.SubGroupObjs.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIJSLWBObjs.API_NAME, APIJSLWBObjs.API_VER, JCPFEVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods - Objs List

    public static List<JOSPObjHtml> objectsList(List<JSLRemoteObject> objs) {
        // Convert object list
        List<JOSPObjHtml> objHtml = new ArrayList<>();
        for (JSLRemoteObject o : objs)
            objHtml.add(new JOSPObjHtml(o));

        return objHtml;
    }

    @GetMapping(path = APIJSLWBObjs.FULL_PATH_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Provide the objects list")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<JOSPObjHtml>> jsonObjectsList(@ApiIgnore HttpSession session) {
        return ResponseEntity.ok(objectsList(jslService.listObjects(jslService.getHttp(session))));
    }


    // Methods - Objs Details

    @GetMapping(path = APIJSLWBObjs.FULL_PATH_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<JOSPObjHtml> jsonObjectDetails(@ApiIgnore HttpSession session,
                                                         @PathVariable("obj_id") String objId) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);
        return ResponseEntity.ok(new JOSPObjHtml(obj));
    }


    // Set owner and name

    @PostMapping(path = APIJSLWBObjs.FULL_PATH_OWNER, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectOwner(@ApiIgnore HttpSession session,
                                                   @PathVariable("obj_id") String objId,
                                                   @RequestParam("new_owner") String newOwner) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (JSLSpringService.getObjPerm(obj) != JOSPPerm.Type.CoOwner)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on update permission to '%s' object.", objId));

        try {
            obj.getInfo().setOwnerId(newOwner);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on set owner to '%s' object.", objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send 'set owner' message because '%s' object is not connected.", objId), e);
        }

        return ResponseEntity.ok(true);
    }

    @PostMapping(path = APIJSLWBObjs.FULL_PATH_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectName(@ApiIgnore HttpSession session,
                                                  @PathVariable("obj_id") String objId,
                                                  @RequestParam("new_name") String newName) {

        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);
        try {
            obj.getInfo().setName(newName);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on rename '%s' object.", objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send rename message because '%s' object is not connected.", objId), e);
        }

        return ResponseEntity.ok(true);
    }


    // Events

    @GetMapping(path = APIJSLWBObjs.FULL_PATH_EVENTS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<JOSPEvent>> jsonObjectEvents(@ApiIgnore HttpSession session,
                                                            @PathVariable("obj_id") String objId,
                                                            HistoryLimits limits) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);
        try {
            return ResponseEntity.ok(obj.getInfo().getEventsHistory(limits, 20));
        } catch (JSLRemoteObject.ObjectNotConnected | JSLRemoteObject.MissingPermission objectNotConnected) {
            objectNotConnected.printStackTrace();
        }
        return null;
    }

}

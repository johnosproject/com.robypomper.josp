package com.robypomper.josp.jcp.fe.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.jcp.fe.HTMLUtils;
import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jcp.info.JCPFEVersions;
import com.robypomper.josp.jcp.params.fe.JOSPObjHtml;
import com.robypomper.josp.jcp.paths.fe.APIFEObjs;
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
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@RestController
@Api(tags = {APIFEObjs.SubGroupObjs.NAME})
public class APIFEObjsController {

    // Internal var

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIFEObjs() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIFEObjs.SubGroupObjs.NAME, APIFEObjs.SubGroupObjs.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIFEObjs.API_NAME, APIFEObjs.API_VER, JCPFEVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods - Objs List

    public static List<JOSPObjHtml> objectsList(List<JSLRemoteObject> objs) {
        // Convert object list
        List<JOSPObjHtml> objHtml = new ArrayList<>();
        for (JSLRemoteObject o : objs)
            objHtml.add(new JOSPObjHtml(o));

        return objHtml;
    }

    @GetMapping(path = APIFEObjs.FULL_PATH_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Provide the objects list")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<JOSPObjHtml>> jsonObjectsList(@ApiIgnore HttpSession session) {
        return ResponseEntity.ok(objectsList(jslService.listObjects(jslService.getHttp(session))));
    }

    @GetMapping(path = APIFEObjs.FULL_PATH_LIST, produces = MediaType.TEXT_HTML_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlObjectsList(@ApiIgnore HttpSession session) {
        List<JOSPObjHtml> objHtml = jsonObjectsList(session).getBody();
        if (objHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error on get '%s' objects list.");

        try {
            return HTMLUtils.toHTMLFormattedJSON(objHtml, "Object's List");

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get objects list on formatting response (%s).", e.getMessage()), e);
        }
    }


    // Methods - Objs Details

    @GetMapping(path = APIFEObjs.FULL_PATH_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(path = APIFEObjs.FULL_PATH_DETAILS, produces = MediaType.TEXT_HTML_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlObjectDetails(@ApiIgnore HttpSession session,
                                    @PathVariable("obj_id") String objId) {
        JOSPObjHtml objHtml = jsonObjectDetails(session, objId).getBody();
        if (objHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error on get '%s' object.", objId));

        try {
            return HTMLUtils.toHTMLFormattedJSON(objHtml,
                    String.format("%s Object", jslService.getObj(jslService.getHttp(session), objId).getName()),
                    String.format("<a href=\"%s\">Object</a>", APIFEObjs.FULL_PATH_DETAILS(objId)));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get '%s' object's permissions on formatting response (%s).", objId, e.getMessage()), e);
        }
    }


    // Set owner and name

    @GetMapping(path = APIFEObjs.FULL_PATH_OWNER, produces = MediaType.TEXT_HTML_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String formObjectOwner(@ApiIgnore HttpSession session,
                                  CsrfToken token,
                                  @PathVariable("obj_id") String objId) {
        // ONLY HTML

        return "<form id = \"form_id\" method=\"post\">\n" +
                "    <input type=\"text\" id=\"new_owner\" name=\"new_owner\" value=\"" + jslService.getObj(jslService.getHttp(session), objId).getInfo().getOwnerId() + "\">\n" +
                "    <input type=\"submit\" value=\"Set\">\n" +
                "    <input type=\"hidden\" name=\"_csrf\" value=\"" + token.getToken() + "\"/>\n" +
                "</form>\n" +
                "<form id = \"form_id_anonymous\" method=\"post\">\n" +
                "    <input type=\"hidden\" id=\"new_owner\" name=\"new_owner\" value=\"" + JOSPPerm.WildCards.USR_ANONYMOUS_ID + "\">\n" +
                "    <input type=\"submit\" value=\"Set Anonymous Owner\">\n" +
                "    <input type=\"hidden\" name=\"_csrf\" value=\"" + token.getToken() + "\"/>\n" +
                "</form>\n" +
                "<form id = \"form_id_self\" method=\"post\">\n" +
                "    <input type=\"hidden\" id=\"new_owner\" name=\"new_owner\" value=\"" + jslService.getUserMngr(jslService.getHttp(session)).getUserId() + "\">\n" +
                "    <input type=\"submit\" value=\"Set current user\">\n" +
                "    <input type=\"hidden\" name=\"_csrf\" value=\"" + token.getToken() + "\"/>\n" +
                "</form>\n";
    }

    @PostMapping(path = APIFEObjs.FULL_PATH_OWNER, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(path = APIFEObjs.FULL_PATH_OWNER, produces = MediaType.TEXT_HTML_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlObjectOwner(@ApiIgnore HttpSession session,
                                  @PathVariable("obj_id") String objId,
                                  @RequestParam("new_owner") String newOwner) {
        Boolean success = jsonObjectOwner(session, objId, newOwner).getBody();
        success = success != null && success;
        return HTMLUtils.redirectAndReturn(APIFEObjs.FULL_PATH_DETAILS(objId), success);
    }

    @GetMapping(path = APIFEObjs.FULL_PATH_NAME, produces = MediaType.TEXT_HTML_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String formObjectRename(@ApiIgnore HttpSession session,
                                   CsrfToken token,
                                   @PathVariable("obj_id") String objId) {
        // ONLY HTML
        return "<form id = \"form_id\" method=\"post\">\n" +
                "    <input type=\"text\" id=\"new_name\" name=\"new_name\" value=\"\n" + jslService.getObj(jslService.getHttp(session), objId).getName() + "\">\n" +
                "    <input type=\"submit\" value=\"Set\">\n" +
                "    <input type=\"hidden\" name=\"_csrf\" value=\"" + token.getToken() + "\"/>\n" +
                "</form>\n" +
                "</script>";
    }

    @PostMapping(path = APIFEObjs.FULL_PATH_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(path = APIFEObjs.FULL_PATH_NAME, produces = MediaType.TEXT_HTML_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlObjectName(@ApiIgnore HttpSession session,
                                 @PathVariable("obj_id") String objId,
                                 @RequestParam("new_name") String newName) {
        Boolean success = jsonObjectName(session, objId, newName).getBody();
        success = success != null && success;
        return HTMLUtils.redirectAndReturn(APIFEObjs.FULL_PATH_DETAILS(objId), success);
    }


    // Events

    @GetMapping(path = APIFEObjs.FULL_PATH_EVENTS, produces = MediaType.APPLICATION_JSON_VALUE)
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

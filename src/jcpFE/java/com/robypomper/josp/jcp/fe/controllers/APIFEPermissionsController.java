package com.robypomper.josp.jcp.fe.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.jcp.fe.HTMLUtils;
import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jcp.info.JCPFEVersions;
import com.robypomper.josp.jcp.params.fe.JOSPObjHtml;
import com.robypomper.josp.jcp.params.fe.JOSPPermHtml;
import com.robypomper.josp.jcp.paths.fe.APIFEObjs;
import com.robypomper.josp.jcp.paths.fe.APIFEPermissions;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.JOSPPerm;
import io.swagger.annotations.Api;
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
@Api(tags = {APIFEPermissions.SubGroupPermissions.NAME})
public class APIFEPermissionsController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIFEPermissions() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIFEPermissions.SubGroupPermissions.NAME, APIFEPermissions.SubGroupPermissions.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIFEPermissions.API_NAME, APIFEPermissions.API_VER, JCPFEVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods - Obj's Perms List

    @GetMapping(path = APIFEPermissions.FULL_PATH_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<JOSPPermHtml>> jsonObjectPermissions(@ApiIgnore HttpSession session,
                                                                    @PathVariable("obj_id") String objId) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (!jslService.serviceCanPerm(obj, JOSPPerm.Type.CoOwner))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Access denied to current user/service on access '%s' object's permissions.", objId));

        // Convert permission list
        List<JOSPPermHtml> permsHtml = new ArrayList<>();
        for (JOSPPerm p : obj.getPerms().getPerms())
            permsHtml.add(new JOSPPermHtml(p));

        return ResponseEntity.ok(permsHtml);
    }

    @GetMapping(path = APIFEPermissions.FULL_PATH_LIST, produces = MediaType.TEXT_HTML_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlObjectPermissions(@ApiIgnore HttpSession session,
                                        @PathVariable("obj_id") String objId) {
        List<JOSPPermHtml> permsHtml = jsonObjectPermissions(session, objId).getBody();
        if (permsHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error on get '%s' object's permissions.", objId));

        try {
            return HTMLUtils.toHTMLFormattedJSON(permsHtml,
                    String.format("%s Object's Permissions", jslService.getObj(jslService.getHttp(session), objId).getName()),
                    String.format("<a href=\"%s\">Object</a>", APIFEObjs.FULL_PATH_DETAILS(objId)));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get '%s' object's permissions on formatting response (%s).", objId, e.getMessage()), e);
        }
    }


    // Methods - Obj's perm add

    @GetMapping(path = APIFEPermissions.FULL_PATH_ADD, produces = MediaType.TEXT_HTML_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String formObjectPermissionAdd(CsrfToken token,
                                          @PathVariable("obj_id") String objId) {
        // ONLY HTML

        return "<form id = \"form_id\" method=\"post\">\n" +
                "    <input list=\"services\" name=\"srv_id\">\n" +
                "    <datalist id=\"services\">\n" +
                "      <option value=\"#All\">\n" +
                "    </datalist>" +
                "    <input list=\"users\" name=\"usr_id\">\n" +
                "    <datalist id=\"users\">\n" +
                "      <option value=\"#Owner\">\n" +
                "      <option value=\"#All\">\n" +
                "    </datalist>" +
                "    <select name=\"type\" id=\"type\">\n" +
                String.format("        <option value=\"%s\">%s</option>\n", JOSPPerm.Type.None.toString(), JOSPPerm.Type.None.toString()) +
                String.format("        <option value=\"%s\">%s</option>\n", JOSPPerm.Type.Status.toString(), JOSPPerm.Type.Status.toString()) +
                String.format("        <option value=\"%s\">%s</option>\n", JOSPPerm.Type.Actions.toString(), JOSPPerm.Type.Actions.toString()) +
                String.format("        <option value=\"%s\">%s</option>\n", JOSPPerm.Type.CoOwner.toString(), JOSPPerm.Type.CoOwner.toString()) +
                "    </select>" +
                "    <select name=\"conn\" id=\"conn\">\n" +
                String.format("        <option value=\"%s\">%s</option>\n", JOSPPerm.Connection.OnlyLocal.toString(), JOSPPerm.Connection.OnlyLocal.toString()) +
                String.format("        <option value=\"%s\">%s</option>\n", JOSPPerm.Connection.LocalAndCloud.toString(), JOSPPerm.Connection.LocalAndCloud.toString()) +
                "    </select>" +
                "    <input type=\"submit\" value=\"Add\">\n" +
                "    <input type=\"hidden\" name=\"_csrf\" value=\"" + token.getToken() + "\"/>\n" +
                "</form>\n" +
                "</script>";
    }

    @PostMapping(path = APIFEPermissions.FULL_PATH_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectPermissionAdd(@ApiIgnore HttpSession session,
                                                           @PathVariable("obj_id") String objId,
                                                           @RequestParam("srv_id") String srvId,
                                                           @RequestParam("usr_id") String usrId,
                                                           @RequestParam("type") JOSPPerm.Type type,
                                                           @RequestParam("conn") JOSPPerm.Connection connection) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (JSLSpringService.getObjPerm(obj) != JOSPPerm.Type.CoOwner)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on update permission to '%s' object.", objId));

        try {
            obj.getPerms().addPerm(srvId, usrId, type, connection);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on update permission to '%s' object.", objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send 'update permission' message because '%s' object is not connected.", objId), e);
        }

        return ResponseEntity.ok(true);
    }

    @PostMapping(path = APIFEPermissions.FULL_PATH_ADD, produces = MediaType.TEXT_HTML_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlObjectPermissionAdd(@ApiIgnore HttpSession session,
                                          @PathVariable("obj_id") String objId,
                                          @RequestParam("srv_id") String srvId,
                                          @RequestParam("usr_id") String usrId,
                                          @RequestParam("type") JOSPPerm.Type type,
                                          @RequestParam("conn") JOSPPerm.Connection connection) {
        Boolean success = jsonObjectPermissionAdd(session, objId, srvId, usrId, type, connection).getBody();
        success = success != null && success;
        return HTMLUtils.redirectAndReturn(APIFEPermissions.FULL_PATH_LIST(objId), success);
    }


    // Methods - Obj's perm upd

    @GetMapping(path = APIFEPermissions.FULL_PATH_UPD, produces = MediaType.TEXT_HTML_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String formObjectPermissionUpd(@ApiIgnore HttpSession session,
                                          CsrfToken token,
                                          @PathVariable("obj_id") String objId,
                                          @PathVariable("perm_id") String permId) {
        // ONLY HTML
        JOSPPerm p = jslService.getPerm(jslService.getObj(jslService.getHttp(session), objId), permId);

        return "<form id = \"form_id\" method=\"post\" onsubmit=\"actionUpdater()\">\n" +
                "    <select name=\"type\" id=\"type\">\n" +
                String.format("        <option%s value=\"%s\">%s</option>\n", p.getPermType() == JOSPPerm.Type.None ? " selected=\"selected\"" : "", JOSPPerm.Type.None.toString(), JOSPPerm.Type.None.toString()) +
                String.format("        <option%s value=\"%s\">%s</option>\n", p.getPermType() == JOSPPerm.Type.Status ? " selected=\"selected\"" : "", JOSPPerm.Type.Status.toString(), JOSPPerm.Type.Status.toString()) +
                String.format("        <option%s value=\"%s\">%s</option>\n", p.getPermType() == JOSPPerm.Type.Actions ? " selected=\"selected\"" : "", JOSPPerm.Type.Actions.toString(), JOSPPerm.Type.Actions.toString()) +
                String.format("        <option%s value=\"%s\">%s</option>\n", p.getPermType() == JOSPPerm.Type.CoOwner ? " selected=\"selected\"" : "", JOSPPerm.Type.CoOwner.toString(), JOSPPerm.Type.CoOwner.toString()) +
                "    </select>" +
                "    <select name=\"conn\" id=\"conn\">\n" +
                String.format("        <option%s value=\"%s\">%s</option>\n", p.getConnType() == JOSPPerm.Connection.OnlyLocal ? " selected=\"selected\"" : "", JOSPPerm.Connection.OnlyLocal.toString(), JOSPPerm.Connection.OnlyLocal.toString()) +
                String.format("        <option%s value=\"%s\">%s</option>\n", p.getConnType() == JOSPPerm.Connection.LocalAndCloud ? " selected=\"selected\"" : "", JOSPPerm.Connection.LocalAndCloud.toString(), JOSPPerm.Connection.LocalAndCloud.toString()) +
                "    </select>" +
                "    <input type=\"submit\" value=\"Upd\">\n" +
                "    <input type=\"hidden\" name=\"_csrf\" value=\"" + token.getToken() + "\"/>\n" +
                "</form>\n" +
                "<script>\n" +
                "function actionUpdater(){\n" +
                "    var action_src = document.getElementsByName(\"new_name\")[0].value;\n" +
                "    var form = document.getElementById(\"form_id\");\n" +
                "    form.action = action_src ;\n" +
                "}\n" +
                "</script>";
    }

    @PostMapping(path = APIFEPermissions.FULL_PATH_UPD, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectPermissionUpd(@ApiIgnore HttpSession session,
                                                           @PathVariable("obj_id") String objId,
                                                           @PathVariable("perm_id") String permId,
                                                           @RequestParam(value = "srv_id", required = false) String srvId,
                                                           @RequestParam(value = "usr_id", required = false) String usrId,
                                                           @RequestParam(value = "type", required = false) JOSPPerm.Type type,
                                                           @RequestParam(value = "conn", required = false) JOSPPerm.Connection connection) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (JSLSpringService.getObjPerm(obj) != JOSPPerm.Type.CoOwner)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on update permission to '%s' object.", objId));

        JOSPPerm perm = jslService.getPerm(obj, permId);
        if (srvId == null)
            srvId = perm.getSrvId();
        if (usrId == null)
            usrId = perm.getUsrId();
        if (type == null)
            type = perm.getPermType();
        if (connection == null)
            connection = perm.getConnType();

        try {
            obj.getPerms().updPerm(permId, srvId, usrId, type, connection);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on update permission to '%s' object.", objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send 'update permission' message because '%s' object is not connected.", objId), e);
        }

        return ResponseEntity.ok(true);
    }

    @PostMapping(path = APIFEPermissions.FULL_PATH_UPD, produces = MediaType.TEXT_HTML_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlObjectPermissionUpd(@ApiIgnore HttpSession session,
                                          @PathVariable("obj_id") String objId,
                                          @PathVariable("perm_id") String permId,
                                          @RequestParam(value = "srv_id", required = false) String srvId,
                                          @RequestParam(value = "usr_id", required = false) String usrId,
                                          @RequestParam(value = "type", required = false) JOSPPerm.Type type,
                                          @RequestParam(value = "conn", required = false) JOSPPerm.Connection connection) {
        Boolean success = jsonObjectPermissionUpd(session, objId, srvId, usrId, permId, type, connection).getBody();
        success = success != null && success;
        return HTMLUtils.redirectAndReturn(APIFEPermissions.FULL_PATH_LIST(objId), success);
    }

    // Methods - Obj's perm remove

    @GetMapping(path = APIFEPermissions.FULL_PATH_DEL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectPermissionDel(@ApiIgnore HttpSession session,
                                                           @PathVariable("obj_id") String objId,
                                                           @PathVariable("perm_id") String permId) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (JSLSpringService.getObjPerm(obj) != JOSPPerm.Type.CoOwner)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on update permission to '%s' object.", objId));

        JOSPPerm perm = jslService.getPerm(obj, permId);

        try {
            obj.getPerms().remPerm(perm.getId());

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on duplicate permission to '%s' object.", objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send 'duplicate permission' message because '%s' object is not connected.", objId), e);
        }

        return ResponseEntity.ok(true);
    }

    @GetMapping(path = APIFEPermissions.FULL_PATH_DEL, produces = MediaType.TEXT_HTML_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlObjectPermissionDel(@ApiIgnore HttpSession session,
                                          @PathVariable("obj_id") String objId,
                                          @PathVariable("perm_id") String permId) {
        Boolean success = jsonObjectPermissionDel(session, objId, permId).getBody();
        success = success != null && success;
        return HTMLUtils.redirectAndReturn(APIFEPermissions.FULL_PATH_LIST(objId), success);
    }


    // Methods - Obj's perm duplicate

    @GetMapping(path = APIFEPermissions.FULL_PATH_DUP, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectPermissionDup(@ApiIgnore HttpSession session,
                                                           @PathVariable("obj_id") String objId,
                                                           @PathVariable("perm_id") String permId) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (JSLSpringService.getObjPerm(obj) != JOSPPerm.Type.CoOwner)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on update permission to '%s' object.", objId));

        JOSPPerm perm = jslService.getPerm(obj, permId);

        try {
            obj.getPerms().addPerm(perm.getSrvId(), perm.getUsrId(), perm.getPermType(), perm.getConnType());

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on duplicate permission to '%s' object.", objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send 'duplicate permission' message because '%s' object is not connected.", objId), e);
        }

        return ResponseEntity.ok(true);
    }

    @GetMapping(path = APIFEPermissions.FULL_PATH_DUP, produces = MediaType.TEXT_HTML_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlObjectPermissionDup(@ApiIgnore HttpSession session,
                                          @PathVariable("obj_id") String objId,
                                          @PathVariable("perm_id") String permId) {
        Boolean success = jsonObjectPermissionDup(session, objId, permId).getBody();
        success = success != null && success;
        return HTMLUtils.redirectAndReturn(APIFEPermissions.FULL_PATH_LIST(objId), success);
    }

}

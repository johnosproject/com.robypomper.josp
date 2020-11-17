package com.robypomper.josp.jcp.fe.apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.jcp.fe.HTMLUtils;
import com.robypomper.josp.jcp.fe.apis.params.JOSPPermHtml;
import com.robypomper.josp.jcp.fe.apis.paths.APIJCPFEObjs;
import com.robypomper.josp.jcp.fe.apis.paths.APIJCPFEPermissions;
import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.JOSPPerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
@RestController
//@Api(tags = {APIJCPFEPermissions.SubGroupPermissions.NAME})
public class PermissionsController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;


    // Obj's perms list

    @GetMapping(path = APIJCPFEPermissions.FULL_PATH_LIST)
    public ResponseEntity<List<JOSPPermHtml>> jsonObjectPermissions(HttpSession session,
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

    @GetMapping(path = APIJCPFEPermissions.FULL_PATH_LIST, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlObjectPermissions(HttpSession session,
                                        @PathVariable("obj_id") String objId) {
        List<JOSPPermHtml> permsHtml = jsonObjectPermissions(session, objId).getBody();
        if (permsHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error on get '%s' object's permissions.", objId));

        try {
            return HTMLUtils.toHTMLFormattedJSON(permsHtml,
                    String.format("%s Object's Permissions", jslService.getObj(jslService.getHttp(session), objId).getName()),
                    String.format("<a href=\"%s\">Object</a>", APIJCPFEObjs.FULL_PATH_DETAILS(objId)));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get '%s' object's permissions on formatting response (%s).", objId, e.getMessage()), e);
        }
    }


    // Obj's perm add

    @GetMapping(path = APIJCPFEPermissions.FULL_PATH_ADD, produces = MediaType.TEXT_HTML_VALUE)
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

    @PostMapping(path = APIJCPFEPermissions.FULL_PATH_ADD)
    public ResponseEntity<Boolean> jsonObjectPermissionAdd(HttpSession session,
                                                           @PathVariable("obj_id") String objId,
                                                           @RequestParam("srv_id") String srvId,
                                                           @RequestParam("usr_id") String usrId,
                                                           @RequestParam("type") JOSPPerm.Type type,
                                                           @RequestParam("conn") JOSPPerm.Connection connection) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (jslService.getObjPerm(obj) != JOSPPerm.Type.CoOwner)
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

    @PostMapping(path = APIJCPFEPermissions.FULL_PATH_ADD, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlObjectPermissionAdd(HttpSession session,
                                          @PathVariable("obj_id") String objId,
                                          @RequestParam("srv_id") String srvId,
                                          @RequestParam("usr_id") String usrId,
                                          @RequestParam("type") JOSPPerm.Type type,
                                          @RequestParam("conn") JOSPPerm.Connection connection) {
        Boolean success = jsonObjectPermissionAdd(session, objId, srvId, usrId, type, connection).getBody();
        success = success != null && success;
        return HTMLUtils.redirectAndReturn(APIJCPFEPermissions.FULL_PATH_LIST(objId), success);
    }


    // Obj's perm upd

    @GetMapping(path = APIJCPFEPermissions.FULL_PATH_UPD, produces = MediaType.TEXT_HTML_VALUE)
    public String formObjectPermissionUpd(HttpSession session,
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

    @PostMapping(path = APIJCPFEPermissions.FULL_PATH_UPD)
    public ResponseEntity<Boolean> jsonObjectPermissionUpd(HttpSession session,
                                                           @PathVariable("obj_id") String objId,
                                                           @PathVariable("perm_id") String permId,
                                                           @RequestParam(value = "srv_id", required = false) String srvId,
                                                           @RequestParam(value = "usr_id", required = false) String usrId,
                                                           @RequestParam(value = "type", required = false) JOSPPerm.Type type,
                                                           @RequestParam(value = "conn", required = false) JOSPPerm.Connection connection) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (jslService.getObjPerm(obj) != JOSPPerm.Type.CoOwner)
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

    @PostMapping(path = APIJCPFEPermissions.FULL_PATH_UPD, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlObjectPermissionUpd(HttpSession session,
                                          @PathVariable("obj_id") String objId,
                                          @PathVariable("perm_id") String permId,
                                          @RequestParam(value = "srv_id", required = false) String srvId,
                                          @RequestParam(value = "usr_id", required = false) String usrId,
                                          @RequestParam(value = "type", required = false) JOSPPerm.Type type,
                                          @RequestParam(value = "conn", required = false) JOSPPerm.Connection connection) {
        Boolean success = jsonObjectPermissionUpd(session, objId, srvId, usrId, permId, type, connection).getBody();
        success = success != null && success;
        return HTMLUtils.redirectAndReturn(APIJCPFEPermissions.FULL_PATH_LIST(objId), success);
    }

    // Obj's perm remove

    @GetMapping(path = APIJCPFEPermissions.FULL_PATH_DEL)
    public ResponseEntity<Boolean> jsonObjectPermissionDel(HttpSession session,
                                                           @PathVariable("obj_id") String objId,
                                                           @PathVariable("perm_id") String permId) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (jslService.getObjPerm(obj) != JOSPPerm.Type.CoOwner)
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

    @GetMapping(path = APIJCPFEPermissions.FULL_PATH_DEL, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlObjectPermissionDel(HttpSession session,
                                          @PathVariable("obj_id") String objId,
                                          @PathVariable("perm_id") String permId) {
        Boolean success = jsonObjectPermissionDel(session, objId, permId).getBody();
        success = success != null && success;
        return HTMLUtils.redirectAndReturn(APIJCPFEPermissions.FULL_PATH_LIST(objId), success);
    }


    // Obj's perm duplicate

    @GetMapping(path = APIJCPFEPermissions.FULL_PATH_DUP)
    public ResponseEntity<Boolean> jsonObjectPermissionDup(HttpSession session,
                                                           @PathVariable("obj_id") String objId,
                                                           @PathVariable("perm_id") String permId) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (jslService.getObjPerm(obj) != JOSPPerm.Type.CoOwner)
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

    @GetMapping(path = APIJCPFEPermissions.FULL_PATH_DUP, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlObjectPermissionDup(HttpSession session,
                                          @PathVariable("obj_id") String objId,
                                          @PathVariable("perm_id") String permId) {
        Boolean success = jsonObjectPermissionDup(session, objId, permId).getBody();
        success = success != null && success;
        return HTMLUtils.redirectAndReturn(APIJCPFEPermissions.FULL_PATH_LIST(objId), success);
    }


    // Shared structures

}

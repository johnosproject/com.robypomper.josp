package com.robypomper.josp.jcpfe.apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.jcpfe.HTMLUtils;
import com.robypomper.josp.jcpfe.apis.params.JOSPObjHtml;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEObjs;
import com.robypomper.josp.jcpfe.jsl.JSLSpringService;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.JOSPPerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@SessionScope
//@Api(tags = {APIJCPFEObjs.SubGroupObjs.NAME})
public class ObjsMngrController {

    // Internal var

    @Autowired
    private JSLSpringService jslService;


    // List and details

    public static List<JOSPObjHtml> objectsList(HttpSession session,
                                                JSLSpringService jslStaticService) {
        // Convert object list
        List<JOSPObjHtml> objHtml = new ArrayList<>();
        for (JSLRemoteObject o : jslStaticService.listObjects(jslStaticService.getHttp(session)))
            objHtml.add(new JOSPObjHtml(o));

        return objHtml;
    }

    @GetMapping(path = APIJCPFEObjs.FULL_PATH_LIST)
    public ResponseEntity<List<JOSPObjHtml>> jsonObjectsList(HttpSession session) {
        return ResponseEntity.ok(objectsList(session, jslService));
    }

    @GetMapping(path = APIJCPFEObjs.FULL_PATH_LIST, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlObjectsList(HttpSession session) {
        List<JOSPObjHtml> objHtml = jsonObjectsList(session).getBody();
        if (objHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error on get '%s' objects list.");

        try {
            return HTMLUtils.toHTMLFormattedJSON(objHtml, "Object's List");

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get objects list on formatting response (%s).", e.getMessage()), e);
        }
    }

    @GetMapping(path = APIJCPFEObjs.FULL_PATH_DETAILS)
    public ResponseEntity<JOSPObjHtml> jsonObjectDetails(HttpSession session,
                                                         @PathVariable("obj_id") String objId) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);
        return ResponseEntity.ok(new JOSPObjHtml(obj));
    }

    @GetMapping(path = APIJCPFEObjs.FULL_PATH_DETAILS, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlObjectDetails(HttpSession session,
                                    @PathVariable("obj_id") String objId) {
        JOSPObjHtml objHtml = jsonObjectDetails(session, objId).getBody();
        if (objHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error on get '%s' object.", objId));

        try {
            return HTMLUtils.toHTMLFormattedJSON(objHtml,
                    String.format("%s Object", jslService.getObj(jslService.getHttp(session), objId).getName()),
                    String.format("<a href=\"%s\">Object</a>", APIJCPFEObjs.FULL_PATH_DETAILS(objId)));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get '%s' object's permissions on formatting response (%s).", objId, e.getMessage()), e);
        }
    }


    // Set owner and name

    @GetMapping(path = APIJCPFEObjs.FULL_PATH_OWNER, produces = MediaType.TEXT_HTML_VALUE)
    public String formObjectOwner(HttpSession session,
                                  @PathVariable("obj_id") String objId) {
        // ONLY HTML

        return "<form id = \"form_id\" method=\"post\">\n" +
                "    <input type=\"text\" id=\"new_owner\" name=\"new_owner\" value=\"" + jslService.getObj(jslService.getHttp(session), objId).getOwnerId() + "\">\n" +
                "    <input type=\"submit\" value=\"Set\">\n" +
                "</form>\n" +
                "<form id = \"form_id_anonymous\" method=\"post\">\n" +
                "    <input type=\"hidden\" id=\"new_owner\" name=\"new_owner\" value=\"" + JOSPPerm.WildCards.USR_ANONYMOUS_ID + "\">\n" +
                "    <input type=\"submit\" value=\"Set Anonymous Owner\">\n" +
                "</form>\n" +
                "<form id = \"form_id_self\" method=\"post\">\n" +
                "    <input type=\"hidden\" id=\"new_owner\" name=\"new_owner\" value=\"" + jslService.getUserMngr(jslService.getHttp(session)).getUserId() + "\">\n" +
                "    <input type=\"submit\" value=\"Set current user\">\n" +
                "</form>\n";
    }

    @PostMapping(path = APIJCPFEObjs.FULL_PATH_OWNER)
    public ResponseEntity<Boolean> jsonObjectOwner(HttpSession session,
                                                   @PathVariable("obj_id") String objId,
                                                   @RequestParam("new_owner") String newOwner) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (jslService.getObjPerm(obj) != JOSPPerm.Type.CoOwner)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on update permission to '%s' object.", objId));

        try {
            obj.setOwnerId(newOwner);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on set owner to '%s' object.", objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send 'set owner' message because '%s' object is not connected.", objId), e);
        }

        return ResponseEntity.ok(true);
    }

    @PostMapping(path = APIJCPFEObjs.FULL_PATH_OWNER, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlObjectOwner(HttpSession session,
                                  @PathVariable("obj_id") String objId,
                                  @RequestParam("new_owner") String newOwner) {
        Boolean success = jsonObjectOwner(session, objId, newOwner).getBody();
        success = success != null && success;
        return HTMLUtils.redirectAndReturn(APIJCPFEObjs.FULL_PATH_DETAILS(objId), success);
    }

    @GetMapping(path = APIJCPFEObjs.FULL_PATH_NAME, produces = MediaType.TEXT_HTML_VALUE)
    public String formObjectRename(HttpSession session,
                                   @PathVariable("obj_id") String objId) {
        // ONLY HTML

        return "<form id = \"form_id\" method=\"post\">\n" +
                "    <input type=\"text\" id=\"new_name\" name=\"new_name\" value=\"\n" + jslService.getObj(jslService.getHttp(session), objId).getName() + "\">\n" +
                "    <input type=\"submit\" value=\"Set\">\n" +
                "</form>\n" +
                "</script>";
    }

    @PostMapping(path = APIJCPFEObjs.FULL_PATH_NAME)
    public ResponseEntity<Boolean> jsonObjectName(HttpSession session,
                                                  @PathVariable("obj_id") String objId,
                                                  @RequestParam("new_name") String newName) {

        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);
        try {
            obj.setName(newName);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on rename '%s' object.", objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send rename message because '%s' object is not connected.", objId), e);
        }

        return ResponseEntity.ok(true);
    }

    @PostMapping(path = APIJCPFEObjs.FULL_PATH_NAME, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlObjectName(HttpSession session,
                                 @PathVariable("obj_id") String objId,
                                 @RequestParam("new_name") String newName) {
        Boolean success = jsonObjectName(session, objId, newName).getBody();
        success = success != null && success;
        return HTMLUtils.redirectAndReturn(APIJCPFEObjs.FULL_PATH_DETAILS(objId), success);
    }


    // Shared structures

}

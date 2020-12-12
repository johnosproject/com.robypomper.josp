package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.info.JCPFEVersions;
import com.robypomper.josp.jcp.jslwebbridge.jsl.JSLSpringService;
import com.robypomper.josp.jcp.params.jslwb.JOSPObjHtml;
import com.robypomper.josp.jcp.params.jslwb.JOSPPermHtml;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBPermissions;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBPermissions.SubGroupPermissions.NAME})
public class APIJSLWBPermissionsController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIJSLWBPermissions() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIJSLWBPermissions.SubGroupPermissions.NAME, APIJSLWBPermissions.SubGroupPermissions.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIJSLWBPermissions.API_NAME, APIJSLWBPermissions.API_VER, JCPFEVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods - Obj's Perms List

    @GetMapping(path = APIJSLWBPermissions.FULL_PATH_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
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


    // Methods - Obj's perm add

    @PostMapping(path = APIJSLWBPermissions.FULL_PATH_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
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


    // Methods - Obj's perm upd

    @PostMapping(path = APIJSLWBPermissions.FULL_PATH_UPD, produces = MediaType.APPLICATION_JSON_VALUE)
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

    // Methods - Obj's perm remove

    @GetMapping(path = APIJSLWBPermissions.FULL_PATH_DEL, produces = MediaType.APPLICATION_JSON_VALUE)
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


    // Methods - Obj's perm duplicate

    @GetMapping(path = APIJSLWBPermissions.FULL_PATH_DUP, produces = MediaType.APPLICATION_JSON_VALUE)
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

}

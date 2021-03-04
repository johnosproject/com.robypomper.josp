package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jcp.params.jslwb.JOSPObjHtml;
import com.robypomper.josp.jcp.params.jslwb.JOSPPermHtml;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBPermissions;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.JOSPPerm;
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
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBPermissions.SubGroupPermissions.NAME})
public class APIJSLWBPermissionsController extends APIJSLWBControllerAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(APIJSLWBPermissionsController.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;


    // Constructors

    public APIJSLWBPermissionsController() {
        super(APIJSLWBPermissions.API_NAME, APIJSLWBPermissions.API_VER, JCPJSLWBVersions.API_NAME, APIJSLWBPermissions.SubGroupPermissions.NAME, APIJSLWBPermissions.SubGroupPermissions.DESCR);
    }


    // Swagger configs

    @Bean
    public Docket swaggerConfig_APIJSLWBPermissions() {
        return swaggerConfig();
    }


    // Methods - Obj's Perms List

    @GetMapping(path = APIJSLWBPermissions.FULL_PATH_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBPermissions.DESCR_PATH_LIST)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<JOSPPermHtml>> jsonObjectPermissions(@ApiIgnore HttpSession session,
                                                                    @PathVariable("obj_id") String objId) {
        JSLRemoteObject obj = webBridgeService.getJSLObj(session.getId(), objId);

        // Convert permission list
        List<JOSPPermHtml> permsHtml = new ArrayList<>();
        for (JOSPPerm p : obj.getPerms().getPerms())        // ToDo add MissingPermission exception on getPerms() method
            permsHtml.add(new JOSPPermHtml(p));

        return ResponseEntity.ok(permsHtml);
    }


    // Methods - Obj's perm add

    @PostMapping(path = APIJSLWBPermissions.FULL_PATH_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBPermissions.DESCR_PATH_ADD)
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
        JSLRemoteObject obj = webBridgeService.getJSLObj(session.getId(), objId);

        try {
            obj.getPerms().addPerm(srvId, usrId, type, connection);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "add permission", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "add permission", e);
        }

        return ResponseEntity.ok(true);
    }


    // Methods - Obj's perm upd

    @PostMapping(path = APIJSLWBPermissions.FULL_PATH_UPD, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBPermissions.DESCR_PATH_UPD)
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
        JSLRemoteObject obj = webBridgeService.getJSLObj(session.getId(), objId);
        JOSPPerm perm = webBridgeService.getJSLObjPerm(session.getId(), objId, permId);

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
            throw missingPermissionsException(objId, "update permission", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "update permission", e);
        }

        return ResponseEntity.ok(true);
    }

    // Methods - Obj's perm remove

    @GetMapping(path = APIJSLWBPermissions.FULL_PATH_DEL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBPermissions.DESCR_PATH_DEL)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectPermissionDel(@ApiIgnore HttpSession session,
                                                           @PathVariable("obj_id") String objId,
                                                           @PathVariable("perm_id") String permId) {
        JSLRemoteObject obj = webBridgeService.getJSLObj(session.getId(), objId);
        JOSPPerm perm = webBridgeService.getJSLObjPerm(session.getId(), objId, permId);

        try {
            obj.getPerms().remPerm(perm.getId());

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "duplicate permission", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "duplicate permission", e);
        }

        return ResponseEntity.ok(true);
    }


    // Methods - Obj's perm duplicate

    @GetMapping(path = APIJSLWBPermissions.FULL_PATH_DUP, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBPermissions.DESCR_PATH_DUP)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectPermissionDup(@ApiIgnore HttpSession session,
                                                           @PathVariable("obj_id") String objId,
                                                           @PathVariable("perm_id") String permId) {
        JSLRemoteObject obj = webBridgeService.getJSLObj(session.getId(), objId);
        JOSPPerm perm = webBridgeService.getJSLObjPerm(session.getId(), objId, permId);

        try {
            obj.getPerms().addPerm(perm.getSrvId(), perm.getUsrId(), perm.getPermType(), perm.getConnType());

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "remove permission", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "remove permission", e);
        }

        return ResponseEntity.ok(true);
    }

}

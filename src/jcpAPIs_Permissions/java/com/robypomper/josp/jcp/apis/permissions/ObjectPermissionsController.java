package com.robypomper.josp.jcp.apis.permissions;

import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jcp.apis.paths.APIPermissions;
import com.robypomper.josp.jcp.db.PermissionsDBService;
import com.robypomper.josp.jcp.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.info.JCPAPIsGroups;
import com.robypomper.josp.protocol.JOSPPerm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * API Permission Objs's controller.
 * <p>
 * This controller expose methods used by the JOD Objects to manage their
 * permissions.
 */
@SuppressWarnings("unused")
@RestController
@Api(tags = {JCPAPIsGroups.API_PERM_SG_OBJ_NAME})
public class ObjectPermissionsController {

    // Internal vars

    @Autowired
    private PermissionsDBService permissionsDBService;


    // Methods

    /**
     * Generate and return a valid Object's permission set depending on required
     * <code>strategy</code>.
     * <p>
     * Strategies:
     * <ul>
     *     <li>
     *         STANDARD:<br>
     *             #Owner, #All, LocalAndCloud, CoOwner
     *     </li>
     *     <li>
     *         PUBLIC:<br>
     *             #Owner, #All, LocalAndCloud, CoOwner<br>
     *             #All, #All, OnlyLocal, Action
     *     </li>
     * </ul>
     *
     * @param objId    the object id to register.
     * @param strategy the strategy to use for permission generation.
     * @return a set of object's permissions.
     */
    @GetMapping(path = APIPermissions.FULL_PATH_OBJGENERATE + "/{strategy}")
    @ApiOperation(value = "Generate object permission set",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
            @ApiResponse(code = 501, message = "Requested '" + APIObjs.HEADER_OBJID + "' strategy not implemented")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<String> generatePermissions(
            @RequestHeader(APIObjs.HEADER_OBJID) String objId,
            @PathVariable("strategy") JOSPPerm.GenerateStrategy strategy) {

        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        List<JOSPPerm> objPerms = new ArrayList<>();
        Date updateDate = new Date();

        if (strategy == JOSPPerm.GenerateStrategy.STANDARD) {
            objPerms.add(new JOSPPerm(objId, JOSPPerm.WildCards.SRV_ALL.toString(), JOSPPerm.WildCards.USR_OWNER.toString(), JOSPPerm.Type.CoOwner, JOSPPerm.Connection.LocalAndCloud, updateDate));

        } else if (strategy == JOSPPerm.GenerateStrategy.PUBLIC) {
            objPerms.add(new JOSPPerm(objId, JOSPPerm.WildCards.SRV_ALL.toString(), JOSPPerm.WildCards.USR_OWNER.toString(), JOSPPerm.Type.CoOwner, JOSPPerm.Connection.LocalAndCloud, updateDate));
            objPerms.add(new JOSPPerm(objId, JOSPPerm.WildCards.SRV_ALL.toString(), JOSPPerm.WildCards.USR_ALL.toString(), JOSPPerm.Type.Actions, JOSPPerm.Connection.OnlyLocal, updateDate));

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, String.format("Can't generate obj's permission because unknown strategy '%s'.", strategy));
        }

        return ResponseEntity.ok(JOSPPerm.toString(objPerms));

    }

}

package com.robypomper.josp.jcp.apis.permissions;

import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jcp.apis.paths.APIPermissions;
import com.robypomper.josp.jcp.db.PermissionsDBService;
import com.robypomper.josp.jcp.db.entities.Permission;
import com.robypomper.josp.jcp.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.gw.JOSPGWsBroker;
import com.robypomper.josp.jcp.info.JCPAPIsGroups;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
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
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPPerm.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
            @ApiResponse(code = 501, message = "Requested '" + APIObjs.HEADER_OBJID + "' strategy not implemented")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<List<JOSPPerm>> generatePermissions(
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

        return ResponseEntity.ok(objPerms);

    }

    /**
     * Method used by <code>objId</code> to sync their (local stored) permission with their
     * (cloud stored) permission.
     * <p>
     * This method receive object's local permissions and get object's cloud
     * permission, the merge them.
     * <p>
     * Merge operation also update object's permissions cloud stored and return
     * the same permission set to the object (so it can store updated permission
     * locally).
     *
     * @param objId the object id to register.
     * @return true if the object0s owner is set.
     */
    @PostMapping(path = APIPermissions.FULL_PATH_OBJMERGE)
    @ApiOperation(value = "Current object's permission sync method",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPPerm.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<List<JOSPPerm>> mergeAndStorePermissions(
            @RequestHeader(APIObjs.HEADER_OBJID) String objId,
            @RequestBody List<JOSPPerm> objPerms) {

        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        List<Permission> jcpPerms = permissionsDBService.findByObj(objId);
        List<Permission> savedPerms = mergeAndUpdJCPPermissions(jcpPerms, objPerms);
        return ResponseEntity.ok(toObjPerms(savedPerms));
    }


    // Obj permission mngm

    /**
     * Transform an {@link Permission} list in a {@link JOSPPerm} list.
     *
     * @param savedPerms original {@link Permission} list.
     * @return new {@link JOSPPerm} list.
     */
    private List<JOSPPerm> toObjPerms(List<Permission> savedPerms) {
        List<JOSPPerm> objPerms = new ArrayList<>();

        if (savedPerms == null)
            return objPerms;

        for (Permission p : savedPerms)
            objPerms.add(new JOSPPerm(p.getObjId(), p.getUsrId(), p.getSrvId(), p.getConnection().toString(), p.getType().toString(), JOSPProtocol.getDateFormatter().format(p.getUpdatedAt())));

        return objPerms;
    }

    /**
     * Merge given {@link Permission} lists and update JCP db.
     * <p>
     * Returned list contain all permissions contained in given list, keeping
     * most up-to-date duplicates and without deleted (updatedAt=0) permissions.
     *
     * @param jcpPerms object's permissions from JCP db.
     * @param objPerms object's permission uploaded from object.
     * @return updated object's permission list.
     */
    private List<Permission> mergeAndUpdJCPPermissions(List<Permission> jcpPerms, List<JOSPPerm> objPerms) {
        List<Permission> merged = new ArrayList<>(jcpPerms);
        List<Permission> deleted = new ArrayList<>();

        for (JOSPPerm p : objPerms) {
            Permission duplicate = find(merged, p);

            if (duplicate == null) {                                            // New permission
                if (p.getUpdatedAt().getTime() == 0)
                    continue;
                Permission perm = new Permission();
                //perm.setId(p.id); not exist yet
                perm.setObjId(p.getObjId());
                perm.setSrvId(p.getSrvId());
                perm.setUsrId(p.getUsrId());
                perm.setType(p.getPermType());
                perm.setConnection(p.getConnType());
                perm.setUpdatedAt(p.getUpdatedAt());
                merged.add(perm);

            } else if (p.getUpdatedAt().getTime() == 0) {
                duplicate.setUpdatedAt(new Date(0));

            } else {                                                            // Update
                if (p.getUpdatedAt().after(duplicate.getUpdatedAt())) {
                    if (duplicate.getUpdatedAt().getTime() == 0)
                        continue;
                    duplicate.setConnection(p.getConnType());
                    duplicate.setType(p.getPermType());
                    duplicate.setUpdatedAt(p.getUpdatedAt());
                }
            }
        }

        List<Permission> confirmed = new LinkedList<>();
        for (Permission p : merged)
            if (p.getUpdatedAt().getTime() == 0) {
                deleted.add(p);
            } else
                confirmed.add(p);

        List<Permission> savedPerms = permissionsDBService.addAll(confirmed);
        permissionsDBService.removeAll(deleted);

        return savedPerms;
    }

    /**
     * Look for given <code>permission</code> in the given <code>list</code>.
     * <p>
     * The permission comparison is applied on following fields:
     * <ul>
     *     <li>objId</li>
     *     <li>srvId</li>
     *     <li>usrId</li>
     *     <li>connection</li>
     *     <li>type</li>
     * </ul>
     *
     * @param list       the list where to lokking.
     * @param permission the permission to find.
     * @return the permission from the list and equal to given <code>permission</code>
     * or <code>null</code> if no permission in the list correspond to
     * given one.
     */
    private Permission find(List<Permission> list, JOSPPerm permission) {
        for (Permission p : list)
            if (permission.getId() != null && !permission.getId().isEmpty()) {
                if (p.getId().equals(permission.getId()))
                    return p;

            } else {
                if (
                        p.getObjId().compareToIgnoreCase(permission.getObjId()) == 0
                                && p.getSrvId().compareToIgnoreCase(permission.getSrvId()) == 0
                                && p.getUsrId().compareToIgnoreCase(permission.getUsrId()) == 0
                )
                    return p;
            }
        return null;
    }

}

package com.robypomper.josp.jcp.apis.permissions;

import com.robypomper.josp.jcp.apis.params.permissions.ObjPermission;
import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jcp.apis.paths.APIPermissions;
import com.robypomper.josp.jcp.db.ObjectOwnerDBService;
import com.robypomper.josp.jcp.db.PermissionsDBService;
import com.robypomper.josp.jcp.db.entities.ObjectId;
import com.robypomper.josp.jcp.db.entities.ObjectOwner;
import com.robypomper.josp.jcp.db.entities.Permission;
import com.robypomper.josp.jcp.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.info.JCPAPIsGroups;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import java.util.Optional;


/**
 * API Permission Objs's controller.
 * <p>
 * This controller expose methods used by the JOD Objects to manage their
 * permissions.
 */
@RestController
@Api(tags = {JCPAPIsGroups.API_PERM_SG_OBJ_NAME})
public class ObjectPermissionsController {

    // Internal vars

    @Autowired
    private PermissionsDBService permissionsDBService;

    @Autowired
    private ObjectOwnerDBService objOwnersDBService;


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
            @ApiResponse(code = 200, message = "Method worked successfully", response = ObjPermission.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
            @ApiResponse(code = 501, message = "Requested '" + APIObjs.HEADER_OBJID + "' strategy not implemented")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<List<ObjPermission>> generatePermissions(
            @RequestHeader(APIObjs.HEADER_OBJID)
                    String objId,
            @PathVariable("strategy")
                    PermissionsTypes.GenerateStrategy strategy) {

        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        List<ObjPermission> objPerms = new ArrayList<>();
        Date updateDate = new Date();

        if (strategy == PermissionsTypes.GenerateStrategy.STANDARD) {
            objPerms.add(new ObjPermission(objId, PermissionsTypes.WildCards.USR_OWNER.toString(), PermissionsTypes.WildCards.SRV_ALL.toString(), PermissionsTypes.Connection.LocalAndCloud, PermissionsTypes.Type.CoOwner, updateDate));

        } else if (strategy == PermissionsTypes.GenerateStrategy.PUBLIC) {
            objPerms.add(new ObjPermission(objId, PermissionsTypes.WildCards.USR_OWNER.toString(), PermissionsTypes.WildCards.SRV_ALL.toString(), PermissionsTypes.Connection.LocalAndCloud, PermissionsTypes.Type.CoOwner, updateDate));
            objPerms.add(new ObjPermission(objId, PermissionsTypes.WildCards.USR_ALL.toString(), PermissionsTypes.WildCards.SRV_ALL.toString(), PermissionsTypes.Connection.OnlyLocal, PermissionsTypes.Type.Actions, updateDate));

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
            @ApiResponse(code = 200, message = "Method worked successfully", response = ObjPermission.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<List<ObjPermission>> mergeAndStorePermissions(
            @RequestHeader(APIObjs.HEADER_OBJID)
                    String objId,
            @RequestBody
                    List<ObjPermission> objPerms) {

        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        List<Permission> jcpPerms = permissionsDBService.findByObj(objId);
        List<Permission> savedPerms = mergeAndUpdJCPPermissions(jcpPerms, objPerms);
        return ResponseEntity.ok(toObjPerms(savedPerms));
    }

    /**
     * Check if the <code>objId</code>'s owner is set.
     *
     * @param objId the object id to register.
     * @return true if the objects owner is set.
     */
    @GetMapping(path = APIPermissions.FULL_PATH_OBJOWNERGET)
    @ApiOperation(value = "Check if current object is registered",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<String> getOwner(
            @RequestHeader(APIObjs.HEADER_OBJID)
                    String objId) {

        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        return ResponseEntity.ok(objOwnersDBService.find(objId).get().getOwnerId());

    }

    /**
     * Set <code>objId</code> owner.
     * <p>
     * Read the ownerId from the body of <code>httpEntity</code>.
     *
     * @param objId      the object id to register.
     * @param httpEntity the http request for current method.
     * @return true if the owner was registered successfully.
     */
    @PostMapping(path = APIPermissions.FULL_PATH_OBJOWNER)
    @ApiOperation(value = "Set current object owner",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = ObjectId.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
            @ApiResponse(code = 409, message = "Object have already an owner set"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<Boolean> setOwner(
            @RequestHeader(APIObjs.HEADER_OBJID)
                    String objId,
            HttpEntity<String> httpEntity) {
        final String ownerId = httpEntity.getBody();

        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        if (objOwnersDBService.find(objId).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Object '%s' already set owner.", objId));

        if (ownerId == null || ownerId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing mandatory param 'ownerId'.");

        ObjectOwner objOwner = new ObjectOwner();
        objOwner.setObjId(objId);
        objOwner.setOwnerId(ownerId);
        ObjectOwner saved = objOwnersDBService.add(objOwner);
        return ResponseEntity.ok(saved != null);

    }

    /**
     * Reset <code>objId</code> owner.
     *
     * @param objId the object id to register.
     * @return true if the owner was reset successfully.
     */
    @DeleteMapping(path = APIPermissions.FULL_PATH_OBJOWNERRESET)
    @ApiOperation(value = "Reset current object owner",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = ObjectId.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
            @ApiResponse(code = 409, message = "Object haven't an owner set"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<Boolean> resetOwner(
            @RequestHeader(APIObjs.HEADER_OBJID)
                    String objId) {

        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        Optional<ObjectOwner> objOwner = objOwnersDBService.find(objId);
        if (!objOwner.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Object '%s' haven't set an owner.", objId));

        objOwnersDBService.delete(objOwner.get());
        return ResponseEntity.ok(true);
    }


    // Obj permission mngm

    /**
     * Transform an {@link Permission} list in a {@link ObjPermission} list.
     *
     * @param savedPerms original {@link Permission} list.
     * @return new {@link ObjPermission} list.
     */
    private List<ObjPermission> toObjPerms(List<Permission> savedPerms) {
        List<ObjPermission> objPerms = new ArrayList<>();

        if (savedPerms == null)
            return objPerms;

        for (Permission p : savedPerms)
            objPerms.add(new ObjPermission(p.getObjId(), p.getUsrId(), p.getSrvId(), p.getConnection(), p.getType(), p.getUpdatedAt()));

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
    private List<Permission> mergeAndUpdJCPPermissions(List<Permission> jcpPerms, List<ObjPermission> objPerms) {
        List<Permission> merged = new ArrayList<>(jcpPerms);
        List<Permission> deleted = new ArrayList<>();

        for (ObjPermission p : objPerms) {
            Permission duplicate = find(merged, p);

            if (duplicate == null) {                                            // New permission
                if (p.updatedAt.getTime() == 0)
                    continue;
                Permission perm = new Permission();
                //perm.setId(p.id); not exist yet
                perm.setObjId(p.objId);
                perm.setUsrId(p.usrId);
                perm.setSrvId(p.srvId);
                perm.setConnection(p.connection);
                perm.setType(p.type);
                perm.setUpdatedAt(p.updatedAt);
                merged.add(perm);

            } else if (p.updatedAt.getTime() == 0) {
                duplicate.setUpdatedAt(new Date(0));

            } else {                                                            // Update
                if (p.updatedAt.after(duplicate.getUpdatedAt())) {
                    if (duplicate.getUpdatedAt().getTime() == 0)
                        continue;
                    duplicate.setConnection(p.connection);
                    duplicate.setType(p.type);
                    duplicate.setUpdatedAt(p.updatedAt);
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
    private Permission find(List<Permission> list, ObjPermission permission) {
        for (Permission p : list)
            if (permission.id != null && permission.id != -1) {
                if (p.getId().equals(permission.id))
                    return p;

            } else {
                if (
                        p.getObjId().compareToIgnoreCase(permission.objId) == 0
                                && p.getSrvId().compareToIgnoreCase(permission.srvId) == 0
                                && p.getUsrId().compareToIgnoreCase(permission.usrId) == 0
                )
                    return p;
            }
        return null;
    }

}

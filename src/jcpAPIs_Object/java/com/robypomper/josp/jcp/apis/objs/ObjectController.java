package com.robypomper.josp.jcp.apis.objs;

import com.robypomper.java.JavaRandomStrings;
import com.robypomper.josp.jcp.apis.params.objs.GenerateObjId;
import com.robypomper.josp.jcp.apis.params.objs.RegisterObj;
import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jcp.db.ObjectDBService;
import com.robypomper.josp.jcp.db.ObjectIdDBService;
import com.robypomper.josp.jcp.db.ObjectOwnerDBService;
import com.robypomper.josp.jcp.db.entities.Object;
import com.robypomper.josp.jcp.db.entities.ObjectId;
import com.robypomper.josp.jcp.db.entities.ObjectInfo;
import com.robypomper.josp.jcp.db.entities.ObjectOwner;
import com.robypomper.josp.jcp.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.info.JCPAPIsGroups;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

/**
 * API Object's controller for the Object Info generators.
 * <p>
 * This controller expose methods used by the JOD Object Info system to manage
 * object's info.
 */
@RestController
@Api(tags = {JCPAPIsGroups.API_OBJS_SG_BASE_NAME})
public class ObjectController {

    // Internal vars

    @Autowired
    private ObjectIdDBService objectIdDBService;

    @Autowired
    private ObjectDBService objectDBService;

    @Autowired
    private ObjectOwnerDBService objOwnersDBService;


    // Methods

    /**
     * Generate and return a valid Object's ID.
     * <p>
     * Each object id is registered to given hardware id and object's owner (user
     * id). So when is request a object id from already registered pair (object hw/
     * owner id), same object id will returned.
     *
     * @param objIdParams object containing the object's hardware id and his owner id.
     * @return valid object's object id for given params.
     */
    @PostMapping(path = APIObjs.FULL_PATH_GENERATEID)
    @ApiOperation(value = "Generate a new, random, unique object ID",
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
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<String> generateObjectId(
            @RequestBody
                    GenerateObjId objIdParams) {

        ObjectId newObjId = findOrAddObjId(objIdParams.getObjIdHw(), objIdParams.getOwnerId());
        return ResponseEntity.ok(newObjId.getObjId());

    }


    /**
     * Check if object with given object id is not registered in the JCP.
     *
     * @param objId the object id to check.
     * @return true if the object is already registered, false otherwise.
     */
    @GetMapping(path = APIObjs.FULL_PATH_REGISTER_IS)
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
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID)
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<Boolean> isObjectRegister(
            @RequestHeader(APIObjs.HEADER_OBJID)
                    String objId) {

        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        return ResponseEntity.ok(objectDBService.find(objId).isPresent());

    }


    /**
     * If not yet registered, this method register given object info to JCP and
     * associate them to given object id.
     *
     * @param objId    the object id to register.
     * @param objParam the object's info to register.
     * @return true if the object is already registered, false otherwise.
     */
    @PostMapping(path = APIObjs.FULL_PATH_REGISTER)
    @ApiOperation(value = "Register new object to JCP",
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
            @ApiResponse(code = 409, message = "Object with same id already registered")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<Boolean> registerObject(
            @RequestHeader(APIObjs.HEADER_OBJID)
                    String objId,
            @RequestBody
                    RegisterObj objParam) {

        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        if (objectDBService.find(objId).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Object '%s' already registered.", objId));

        Object savedObj = objectDBService.add(toObject(objId, objParam));

        ObjectOwner objOwner = new ObjectOwner();
        objOwner.setObjId(objId);
        objOwner.setOwnerId(objParam.getOwnerId());
        ObjectOwner savedOwner = objOwnersDBService.add(objOwner);

        return ResponseEntity.ok(savedObj != null && savedOwner != null);

    }


    // ObjectId register

    /**
     * Look for an object id assocaited to the <code>objIdHw-usrId</code> pair.
     * <p>
     * If can't find the object is, the it create a new one and store it to the
     * object id list on JCP.
     *
     * @param objIdHw the object hardware id.
     * @param usrId   the owner id.
     * @return a valid ObjectId instance containing object, hardware and owner ids.
     */
    private ObjectId findOrAddObjId(String objIdHw, String usrId) {
        Optional<ObjectId> objId = objectIdDBService.find(objIdHw, usrId);
        if (objId.isPresent())
            return objId.get();

        ObjectId newObjId = new ObjectId();
        newObjId.setObjId(String.format("%s-%s-%s", objIdHw, JavaRandomStrings.randomAlfaString(5), JavaRandomStrings.randomAlfaString(5)));
        newObjId.setObjIdHw(objIdHw);
        newObjId.setUsrId(usrId);
        return objectIdDBService.add(newObjId);
    }

    /**
     * Transform given objParam to Object instance.
     *
     * @param objId    the object id to inject in result instance.
     * @param objParam object's info to be injected to the result instance.
     * @return a valid Object instance.
     */
    private Object toObject(String objId, RegisterObj objParam) {
        ObjectInfo info = new ObjectInfo();
        info.setModel(objParam.model);
        info.setBrand(objParam.brand);
        info.setLongDescr(objParam.longDescr);
        info.setStructure(objParam.structure);

        Object obj = new Object();
        obj.setObjId(objId);
        obj.setName(objParam.name);
        obj.setInfo(info);
        return obj;
    }

}

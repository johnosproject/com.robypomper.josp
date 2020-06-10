package com.robypomper.josp.jcp.apis.objs;

import com.robypomper.java.JavaRandomStrings;
import com.robypomper.josp.jcp.apis.params.objs.GenerateObjId;
import com.robypomper.josp.jcp.apis.params.objs.RegisterObj;
import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jcp.db.ObjectDBService;
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
@SuppressWarnings("unused")
@RestController
@Api(tags = {JCPAPIsGroups.API_OBJS_SG_BASE_NAME})
public class ObjectController {

    // Internal vars

    @Autowired
    private ObjectDBService objectDBService;


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
            @RequestBody GenerateObjId objIdParams) {

        Optional<Object> optObj = objectDBService.find(objIdParams.getObjIdHw());
        if (optObj.isPresent())
            return ResponseEntity.ok(optObj.get().getObjId());

        ObjectId newObjId = toObjectId(objIdParams);
        objectDBService.save(newObjId);
        return ResponseEntity.ok(newObjId.getObjId());
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
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<Boolean> registerOrUpdateObject(
            @RequestHeader(APIObjs.HEADER_OBJID) String objId,
            @RequestBody RegisterObj objParam) {

        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        Optional<Object> optObj = objectDBService.find(objId);
        if (!optObj.isPresent()) {
            // Register
            try {
                objectDBService.save(toObject(objId, objParam));

            } catch (Throwable e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Error on register new objects because %s.", e.getMessage()));
            }

        } else {
            // Update
            Object obj = optObj.get();
            updateObject(obj, objParam);
            try {
                objectDBService.save(obj);

            } catch (Throwable e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Error on update registered objects because %s.", e.getMessage()));
            }
        }

        return ResponseEntity.ok(true);
    }


    // ObjectId register

    private ObjectId toObjectId(GenerateObjId objIdParams) {
        ObjectId objId = new ObjectId();
        objId.setObjId(String.format("%s-%s-%s", objIdParams.getObjIdHw(), JavaRandomStrings.randomAlfaString(5), JavaRandomStrings.randomAlfaString(5)));
        objId.setObjIdHw(objIdParams.getObjIdHw());
        objId.setUsrId(objIdParams.getOwnerId());
        return objId;
    }

    /**
     * Transform given objParam to Object instance, including Owner and Info fields.
     *
     * @param objId    the object id to inject in result instance.
     * @param objParam object's info to be injected to the result instance.
     * @return a valid Object instance.
     */
    private Object toObject(String objId, RegisterObj objParam) {
        ObjectOwner owner = new ObjectOwner();
        owner.setObjId(objId);
        owner.setOwnerId(objParam.getOwnerId());

        ObjectInfo info = new ObjectInfo();
        info.setObjId(objId);
        info.setModel(objParam.getModel());
        info.setBrand(objParam.getBrand());
        info.setLongDescr(objParam.getLongDescr());
        info.setStructure(objParam.getStructure());

        Object obj = new Object();
        obj.setObjId(objId);
        obj.setName(objParam.getName());
        obj.setVersion(objParam.getVersion());
        obj.setOwner(owner);
        obj.setInfo(info);

        return obj;
    }

    private void updateObject(Object obj, RegisterObj objParam) {
        ObjectOwner owner = obj.getOwner();
        if (!objParam.getOwnerId().isEmpty())
            owner.setOwnerId(objParam.getOwnerId());

        ObjectInfo info = obj.getInfo();
        if (!objParam.getModel().isEmpty())
            info.setModel(objParam.getModel());
        if (!objParam.getBrand().isEmpty())
            info.setBrand(objParam.getBrand());
        if (!objParam.getLongDescr().isEmpty())
            info.setLongDescr(objParam.getLongDescr());
        if (!objParam.getStructure().isEmpty())
            info.setStructure(objParam.getStructure());

        if (!objParam.getName().isEmpty())
            obj.setName(objParam.getName());
    }

}

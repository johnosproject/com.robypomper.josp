/* *****************************************************************************
 * The John Cloud Platform set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jcp.apis.jcp;

import com.robypomper.java.JavaRandomStrings;
import com.robypomper.josp.jcp.apis.params.objs.GenerateObjId;
import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jcp.db.ObjectDBService;
import com.robypomper.josp.jcp.db.entities.Object;
import com.robypomper.josp.jcp.db.entities.ObjectId;
import com.robypomper.josp.jcp.docs.SwaggerConfigurer;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
@Api(tags = {APIObjs.SubGroupInfo.NAME})
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
        return regenerateObjectId(null, objIdParams);
    }

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
    @PostMapping(path = APIObjs.FULL_PATH_REGENERATEID)
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
    public ResponseEntity<String> regenerateObjectId(
            @RequestHeader(APIObjs.HEADER_OBJID) String oldObjId,
            @RequestBody GenerateObjId objIdParams) {

        ObjectId newObjId = toObjectId(objIdParams, oldObjId);
        objectDBService.save(newObjId);

        if (oldObjId != null) {
            Optional<Object> optObj = objectDBService.find(oldObjId);
            if (optObj.isPresent()) {
                Object obj = optObj.get();
                obj.setActive(false);
                objectDBService.save(obj);
            }
        }

        return ResponseEntity.ok(newObjId.getObjId());
    }


    // ObjectId register

    private ObjectId toObjectId(GenerateObjId objIdParams, String oldObjId) {
        ObjectId objId = new ObjectId();
        objId.setObjId(String.format("%s-%s-%s", objIdParams.getObjIdHw(), JavaRandomStrings.randomAlfaString(5), JavaRandomStrings.randomAlfaString(5)));
        objId.setObjIdHw(objIdParams.getObjIdHw());
        objId.setUsrId(objIdParams.getOwnerId());
        if (oldObjId != null)
            objId.setOldObjId(oldObjId);
        else
            objId.setOldObjId("");
        return objId;
    }

}

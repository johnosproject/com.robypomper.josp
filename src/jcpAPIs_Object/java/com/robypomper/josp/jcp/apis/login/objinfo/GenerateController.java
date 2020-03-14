package com.robypomper.josp.jcp.apis.login.objinfo;

import com.robypomper.java.JavaRandomStrings;
import com.robypomper.josp.jcp.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.info.JCPAPIsGroups;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * API Object's controller for the Object Info generators.
 * <p>
 * This controller expose methods used by the JOD Object Info system to generate
 * object's info (p.e: the cloud object's id as part of the object's id).
 */
@RestController
@RequestMapping(value = JCPAPIsGroups.PATH_OBJS + "/objinfo/generate")
@Api(tags = {JCPAPIsGroups.API_OBJS_SG_OBJINFO_GEN_NAME})
public class GenerateController {

    /**
     * Generate and return a valid Object's Cloud ID.
     * <p>
     * This value is used by object's to complete his ID (composed by object's
     * Hardware and Cloud IDs).
     * <p>
     * Each Cloud ID is registered to given Hardware ID and object's owner (User
     * ID). So when is request a Cloud ID from already registered pair (object hw/
     * Owner ID), same Cloud ID will returned.
     *
     * @param reqParams object containing the object's Hardware ID and his Owner ID.
     * @return valid object's Cloud ID for given params.
     */
    @PostMapping(path = "/obj_id")
    @ApiOperation(value = "Generate a new object Cloud ID",
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
            @ApiResponse(code = 401, message = "User not authenticated")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public String generateObjId(
            @ApiParam(
                    value = "A JSON value representing a ArgsGenerateObjId.",
                    examples = @Example(value =
                    @ExampleProperty(
                            mediaType = APPLICATION_JSON_VALUE,
                            value = "{objIdHw: OBJ_ID_HW, usrId: OWNER_ID}"
                    )
                    )
            )
            @RequestBody
                    ArgsGenerateObjId reqParams) {
        System.out.println(String.format("Received generateObjId request from %s and %s", reqParams.usrId, reqParams.objIdHw));
        // ToDo: implement GenerateController::generateObjId()
        return JavaRandomStrings.randomAlfaString(5);
    }

    public static class ArgsGenerateObjId {
        public String objIdHw;
        public String usrId;
    }

}

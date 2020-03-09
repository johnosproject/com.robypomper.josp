package com.robypomper.josp.jcp.apis.login;

import com.robypomper.josp.jcp.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.info.JCPAPIsGroups;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping(JCPAPIsGroups.PATH_OBJS)
@Api(tags = {JCPAPIsGroups.API_OBJS_SG_PLACEHOLDER_NAME})
public class ObjectController {

    @GetMapping("/public")
    @ApiOperation("Public test method")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class)
    })
    public String getPublic() {
        return "Object is up and running";
    }

    @GetMapping(path = "/private")
    @ApiOperation(value = "Private test method",
            authorizations = @Authorization(value=SwaggerConfigurer.OAUTH_FLOW_DEF_TEST)
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    public String getPrivate() {
        return "Object is up, running and private";
    }

    @GetMapping(path = "/role")
    @ApiOperation(value = "Private and role protected test method",
            authorizations = @Authorization(
                    value=SwaggerConfigurer.OAUTH_FLOW_DEF_TEST,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "User not allowed to access")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public String getRoleObj() {
        return String.format("Object is up, running and protected (role: %s)", SwaggerConfigurer.ROLE_OBJ);
    }

}

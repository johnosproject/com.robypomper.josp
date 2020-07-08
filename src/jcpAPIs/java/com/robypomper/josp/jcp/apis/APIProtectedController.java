package com.robypomper.josp.jcp.apis;

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
@RequestMapping(JCPAPIsGroups.PATH_EXMPL)
@Api(tags = {JCPAPIsGroups.API_EXMPL_SG_AUTHORIZATION_NAME})
public class APIProtectedController {

    @GetMapping(path = "/public")
    @ApiOperation("Public accessible method")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class)
    })
    public String getPublic() {
        return "Public string";
    }


    @GetMapping(path = "/private")
    @ApiOperation(value = "Only authenticated user accessible method",
            authorizations = @Authorization(value=SwaggerConfigurer.OAUTH_FLOW_DEF_SRV)
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    public String getPrivate() {
        return "Private string";
    }


    // Auth flows (Swagger only)

    @GetMapping(path = "/private/auth_swagger/pass")
    @ApiOperation(value = "Only authenticated with Auth Code Flow user accessible method",
            authorizations = @Authorization(value=SwaggerConfigurer.OAUTH_PASS)
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    public String getPrivatePass() {
        return "Private pass auth string";
    }

    @GetMapping(path = "/private/auth_swagger/credentials")
    @ApiOperation(value = "Only authenticated with Client Cred Flow user accessible method",
            authorizations = @Authorization(value=SwaggerConfigurer.OAUTH_CRED)
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    public String getPrivateCredentials() {
        return "Private cred auth string";
    }

    @GetMapping(path = "/private/auth_swagger/implicit")
    @ApiOperation(value = "Only authenticated with Implicit Flow user accessible method",
            authorizations = @Authorization(value=SwaggerConfigurer.OAUTH_IMPL)
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    public String getPrivateImplicit() {
        return "Private impl auth string";
    }


    // Roles

    @GetMapping(path = "/private/roles/obj")
    @ApiOperation(value = "Only authenticated obj/user accessible method",
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
        return "Private role obj string";
    }

    @GetMapping(path = "/private/roles/srv")
    @ApiOperation(value = "Only authenticated srv/user accessible method",
            authorizations = @Authorization(
                    value=SwaggerConfigurer.OAUTH_FLOW_DEF_TEST,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "User not allowed to access")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public String getRoleSrv() {
        return "Private role srv string";
    }

    @GetMapping(path = "/private/roles/mng")
    @ApiOperation(value = "Only authenticated mng/user accessible method",
            authorizations = @Authorization(
                    value=SwaggerConfigurer.OAUTH_FLOW_DEF_TEST,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "User not allowed to access")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public String getRoleMng() {
        return "Private role mng string";
    }

}

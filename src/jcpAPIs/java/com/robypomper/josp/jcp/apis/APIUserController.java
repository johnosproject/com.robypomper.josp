package com.robypomper.josp.jcp.apis;

import com.robypomper.josp.jcp.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.info.JCPAPIsGroups;
import com.robypomper.josp.jcp.security.SecurityUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping(JCPAPIsGroups.PATH_EXMPL)
@Api(tags = {JCPAPIsGroups.API_EXMPL_SG_AUTHENTICATION_NAME})
public class APIUserController {

    @GetMapping(path = "/user")
    @ApiOperation(value = "Get current user id",
            authorizations = @Authorization(value=SwaggerConfigurer.OAUTH_FLOW_DEF_TEST)
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
    })
    public String getUser() {
        try {
            return SecurityUser.getUserID();
        } catch (SecurityUser.UserNotAuthenticated e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not authenticated",e);
        } catch (SecurityUser.AuthNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Authorization not setup",e);
        }
    }

    @GetMapping(path = "/user/username")
    @ApiOperation(value = "Get current username",
            authorizations = @Authorization(value=SwaggerConfigurer.OAUTH_FLOW_DEF_TEST)
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
    })
    public String getUsername() {
        try {
            return SecurityUser.getUserName();
        } catch (SecurityUser.UserNotAuthenticated e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not authenticated",e);
        } catch (SecurityUser.AuthNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Authorization not setup",e);
        }
    }

    @GetMapping(path = "/user/scopes")
    @ApiOperation(value = "Get current user scopes",
            authorizations = @Authorization(value=SwaggerConfigurer.OAUTH_FLOW_DEF_TEST)
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class, responseContainer = "Collection"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
    })
    public Collection<String> getScopes() {
        try {
            return SecurityUser.getUserScopes();
        } catch (SecurityUser.AuthNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Authorization not setup",e);
        }
    }

    @GetMapping(path = "/user/roles")
    @ApiOperation(value = "Get current user roles",
            authorizations = @Authorization(value=SwaggerConfigurer.OAUTH_FLOW_DEF_TEST)
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class, responseContainer = "Collection"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
    })
    public Collection<String> getRoles() {
        try {
            return SecurityUser.getUserRoles();
        } catch (SecurityUser.UserNotAuthenticated e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not authenticated",e);
        } catch (SecurityUser.AuthNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Authorization not setup",e);
        }
    }

    @GetMapping(path = "/user/clientid")
    @ApiOperation(value = "Get current user client's id",
            authorizations = @Authorization(value=SwaggerConfigurer.OAUTH_FLOW_DEF_TEST)
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
    })
    public String getClientId() {
        try {
            return SecurityUser.getUserClientId();
        } catch (SecurityUser.UserNotAuthenticated e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not authenticated",e);
        } catch (SecurityUser.AuthNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Authorization not setup",e);
        }
    }

}

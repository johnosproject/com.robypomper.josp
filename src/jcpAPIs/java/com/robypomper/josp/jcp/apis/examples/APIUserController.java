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

package com.robypomper.josp.jcp.apis.examples;

import com.robypomper.josp.jcp.apis.paths.ExampleAPIs;
import com.robypomper.josp.jcp.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.security.SecurityUser;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

//@RestController
//@RequestMapping(JCPAPIsGroups.PATH_EXMPL)
//@Api(tags = {JCPAPIsGroups.API_EXMPL_SG_AUTHENTICATION_NAME})
@RestController
@RequestMapping(ExampleAPIs.FULL_AUTHENTICATION_GENERIC)
@Api(tags = {ExampleAPIs.SubGroupAuthentication.NAME})
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

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

package com.robypomper.josp.jcp.apis.controllers;

import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.params.usrs.UsrName;
import com.robypomper.josp.paths.APIUsrs;
import com.robypomper.josp.jcp.db.apis.UserDBService;
import com.robypomper.josp.jcp.db.apis.entities.User;
import com.robypomper.josp.jcp.external.resources.auth.AuthDefault;
import com.robypomper.josp.jcp.service.spring.SecurityUser;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Optional;


/**
 * Base JCP API Usrs controller, dedicated to provide current user info.
 */
@RestController
@Api(tags = {APIUsrs.SubGroupInfo.NAME})
public class APIUsrsController {

    // Internal vars

    @Autowired
    private AuthDefault authDefault;

    @Autowired
    private UserDBService userService;

    @Autowired
    private HttpSession httpSession;


    // Methods

    /**
     * Return current user id and username.
     * <p>
     * This method afterwards check if the user is registered in the JCP db. If
     * not, the it query the auth's server and register current user in the JCP
     * db.
     *
     * @return current user representation.
     */
    @GetMapping(path = APIUsrs.FULL_PATH_USERNAME)
    @ApiOperation(value = "Return user id and username",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User's id and username", response = UsrName.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 404, message = "User with specified id not found"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
            @ApiResponse(code = 503, message = "Authorization server not available"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<UsrName> get() {
        try {
            String usrId = SecurityUser.getUserID();
            User user = getOrRegisterUser(usrId);
            Collection<String> roles = SecurityUser.getUserRoles();
            boolean isAuthenticated = SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
            boolean isAdmin = roles.contains("mng");
            boolean isMaker = roles.contains("maker");
            boolean isDeveloper = roles.contains("devs");
            return ResponseEntity.ok(new UsrName(user.getUsrId(), user.getUsername(), isAuthenticated, isAdmin, isMaker, isDeveloper));

        } catch (SecurityUser.UserNotAuthenticated e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated.", e);
        } catch (SecurityUser.AuthNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authorization not setup.", e);
        } catch (JCPClient2.AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't connect to remote service because authentication problems.", e);
        } catch (JCPClient2.ConnectionException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't connect to remote service.", e);
        } catch (JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error on executing remote request.", e);
        }
    }


    // User register

    /**
     * Looks for <code>userId</code> on JCP db, if can't find it then create a
     * new user querying the auth server, store it on JCP db and finally return
     * current user representation.
     *
     * @param usrId the <code>usrId</code> to search in the JCP db or to register.
     * @return the {@link User} object stored on the JCP db.
     */
    private User getOrRegisterUser(String usrId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.RequestException, JCPClient2.ResponseException {
        Optional<User> optUser = userService.get(usrId);

        if (optUser.isPresent())
            return optUser.get();

        User newUser = authDefault.queryUser(usrId);
        return userService.add(newUser);
    }

}

package com.robypomper.josp.jcp.apis.usr;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.usrs.UsrName;
import com.robypomper.josp.jcp.apis.paths.APIUsrs;
import com.robypomper.josp.jcp.db.UserDBService;
import com.robypomper.josp.jcp.db.entities.User;
import com.robypomper.josp.jcp.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.external.resources.auth.AuthDefault;
import com.robypomper.josp.jcp.info.JCPAPIsGroups;
import com.robypomper.josp.jcp.security.SecurityUser;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;
import java.util.Optional;


/**
 * Base JCP API Usrs controller, dedicated to provide current user info.
 */
@RestController
@Api(tags = {JCPAPIsGroups.API_USRS_SG_BASE_NAME})
public class UserController {

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
            return ResponseEntity.ok(new UsrName(user.getUsrId(), user.getUsername()));
        } catch (SecurityUser.UserNotAuthenticated e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated.", e);
        } catch (SecurityUser.AuthNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authorization not setup.", e);
        } catch (JCPClient.ConnectionException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't connect to remote service.", e);
        } catch (JCPClient.RequestException e) {
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
    private User getOrRegisterUser(String usrId) throws JCPClient.ConnectionException, JCPClient.RequestException {
        Optional<User> optUser = userService.get(usrId);

        if (optUser.isPresent())
            return optUser.get();

        User newUser = authDefault.queryUser(usrId);
        return userService.add(newUser);
    }

}

package com.robypomper.josp.jcp.apis.srvs;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.srvs.SrvName;
import com.robypomper.josp.jcp.apis.paths.APISrvs;
import com.robypomper.josp.jcp.db.ServiceDBService;
import com.robypomper.josp.jcp.db.entities.Service;
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
@SuppressWarnings("unused")
@RestController
@Api(tags = {JCPAPIsGroups.API_SRVS_SG_BASE_NAME})
public class ServiceController {

    // Internal vars

    @Autowired
    private AuthDefault authDefault;

    @Autowired
    private ServiceDBService serviceService;

    @Autowired
    private HttpSession httpSession;


    // Methods

    /**
     * Return current {@link Service} instance.
     * <p>
     * This method afterwards check if the service is registered in the JCP db. If
     * not, then it query the auth's server and register current service in the JCP
     * db.
     *
     * @return current service representation.
     */
    @GetMapping(path = APISrvs.FULL_PATH_USERNAME)
    @ApiOperation(value = "Return service id and name",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service's id and name", response = SrvName.class),
            @ApiResponse(code = 401, message = "Client not authenticated"),
            @ApiResponse(code = 404, message = "Service with specified id not found"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
            @ApiResponse(code = 503, message = "Authorization server not available"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<SrvName> get() {
        try {
            String srvId = SecurityUser.getUserClientId();
            Service service = getOrRegisterService(srvId);
            return ResponseEntity.ok(new SrvName(service.getSrvId(), service.getSrvName()));
        } catch (SecurityUser.UserNotAuthenticated e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Client not authenticated.", e);
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
     * @param srvId the <code>usrId</code> to search in the JCP db or to register.
     * @return the {@link Service} object stored on the JCP db.
     */
    private Service getOrRegisterService(String srvId) throws JCPClient.ConnectionException, JCPClient.RequestException {
        Optional<Service> optService = serviceService.find(srvId);

        if (optService.isPresent())
            return optService.get();

        Service newService = authDefault.queryService(srvId);
        serviceService.save(newService);
        return newService;
    }

}

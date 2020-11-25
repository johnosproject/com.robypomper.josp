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
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.entities.Service;
import com.robypomper.josp.jcp.external.resources.auth.AuthDefault;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.service.spring.SecurityUser;
import com.robypomper.josp.params.srvs.SrvName;
import com.robypomper.josp.paths.APISrvs;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Base JCP API Usrs controller, dedicated to provide current user info.
 */
@SuppressWarnings("unused")
@RestController
@Api(tags = {APISrvs.SubGroupInfo.NAME})
public class APISrvsController {

    // Internal vars

    @Autowired
    private AuthDefault authDefault;
    @Autowired
    private ServiceDBService serviceService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APISrvs() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APISrvs.SubGroupInfo.NAME, APISrvs.SubGroupInfo.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APISrvs.API_NAME, APISrvs.API_VER, sg), swagger.getUrlBaseAuth());
    }


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
    @GetMapping(path = APISrvs.FULL_PATH_REGISTER)
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
        } catch (JCPClient2.ConnectionException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't connect to remote service.", e);
        } catch (JCPClient2.RequestException e) {
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
    private Service getOrRegisterService(String srvId) throws JCPClient2.ConnectionException, JCPClient2.RequestException {
        Optional<Service> optService = serviceService.find(srvId);

        if (optService.isPresent())
            return optService.get();

        Service newService = authDefault.queryService(srvId);
        newService = serviceService.save(newService);
        return newService;
    }

}

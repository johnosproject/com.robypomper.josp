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

package com.robypomper.josp.jcp.apis.controllers.jcp;

import com.robypomper.josp.info.JCPAPIsVersions;
import com.robypomper.josp.jcp.db.apis.GWDBService;
import com.robypomper.josp.jcp.db.apis.entities.GW;
import com.robypomper.josp.jcp.db.apis.entities.GWStatus;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStartup;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.jcp.paths.jcp.APIJCPGWs;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.paths.APIEvents;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;


/**
 * Base JCP APIs JCP GWs controller, dedicated to JCP GWs registration and
 * status updates.
 */
@RestController
@Api(tags = {APIJCPGWs.SubGroupRegistration.NAME})
public class APIJCPGWsController {

    // Internal vars

    private final GWDBService gwService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIEvents() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIJCPGWs.SubGroupRegistration.NAME, APIJCPGWs.SubGroupRegistration.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIJCPGWs.API_NAME, APIJCPGWs.API_VER, JCPAPIsVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Constructor

    /**
     * This default constructor check, for any GW registered in the DB, if it's
     * online or not. Then, this method, updates the GW status on the DB.
     *
     * @param gwService Spring DB Service's for GWs.
     */
    @Autowired
    public APIJCPGWsController(GWDBService gwService) {
        this.gwService = gwService;
        gwService.checkAllGWsAvailability();
    }


    // Methods

    /**
     * Allow JCP GW to register their startup and become available for objects
     * and services connections.
     *
     * @return true on GW registration success.
     */
    @PostMapping(path = APIJCPGWs.FULL_PATH_STARTUP)
    @ApiOperation(value = "Allow JCP GW to register their startup",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "True on GW registration success.", response = Boolean.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 404, message = "User with specified id not found"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
            @ApiResponse(code = 503, message = "Authorization server not available"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<Boolean> registerStartup(@RequestHeader(APIJCPGWs.HEADER_JCPGWID) String jcpGWId,
                                                   @RequestBody JCPGWsStartup gwStartup) {
        Optional<GW> optGW = gwService.findById(jcpGWId);
        GW gw;
        if (!optGW.isPresent()) {
            gw = new GW();
            gw.setGwId(jcpGWId);
            GWStatus gwStatus = new GWStatus();
            gwStatus.setGwId(jcpGWId);
            gw.setStatus(gwStatus);

        } else
            gw = optGW.get();

        gw.setType(gwStartup.type);
        gw.setGwAddr(gwStartup.gwAddr);
        gw.setGwPort(gwStartup.gwPort);
        gw.setGwAPIsAddr(gwStartup.gwAPIsAddr);
        gw.setGwAPIsPort(gwStartup.gwAPIsPort);
        gw.setClientsMax(gwStartup.clientsMax);
        gw.setVersion(gwStartup.version);
        gwService.save(gw);

        return ResponseEntity.ok(true);
    }

    /**
     * Allow JCP GW to update their status when clients connects or disconnects.
     *
     * @return true on GW status successfully updated.
     */
    @PostMapping(path = APIJCPGWs.FULL_PATH_STATUS)
    @ApiOperation(value = "Allow JCP GW to update their status info",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "True on GW status successfully updated.", response = Boolean.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 404, message = "User with specified id not found"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
            @ApiResponse(code = 503, message = "Authorization server not available"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<Boolean> postStatus(@RequestHeader(APIJCPGWs.HEADER_JCPGWID) String jcpGWId,
                                              @RequestBody JCPGWsStatus gwStatus) {
        Optional<GW> optGW = gwService.findById(jcpGWId);
        GW gw;
        if (!optGW.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Given JCP GWs id '%s' not found.", jcpGWId));

        gw = optGW.get();
        gw.getStatus().setClients(gwStatus.clients);
        gw.getStatus().setLastClientConnectedAt(gwStatus.lastClientConnectedAt);
        gw.getStatus().setLastClientDisconnectedAt(gwStatus.lastClientDisconnectedAt);
        gwService.save(gw);

        return ResponseEntity.ok(true);
    }

    /**
     * Allow JCP GW to register their startup and become available for objects
     * and services connections.
     *
     * @return true on GW registration success.
     */
    @PostMapping(path = APIJCPGWs.FULL_PATH_SHUTDOWN)
    @ApiOperation(value = "Allow JCP GW to register their startup",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "True on GW registration success.", response = Boolean.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 404, message = "User with specified id not found"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
            @ApiResponse(code = 503, message = "Authorization server not available"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<Boolean> postShutdown(@RequestHeader(APIJCPGWs.HEADER_JCPGWID) String jcpGWId) {
        Optional<GW> optGW = gwService.findById(jcpGWId);
        GW gw;
        if (!optGW.isPresent()) {
            gw = new GW();
            gw.setGwId(jcpGWId);
            GWStatus gwStatus = new GWStatus();
            gwStatus.setGwId(jcpGWId);
            gw.setStatus(gwStatus);

        } else
            gw = optGW.get();

        gwService.delete(gw);

        return ResponseEntity.ok(true);
    }

}

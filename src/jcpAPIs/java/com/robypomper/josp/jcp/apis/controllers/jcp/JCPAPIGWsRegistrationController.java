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
import com.robypomper.josp.jcp.apis.mngs.GWsManager;
import com.robypomper.josp.jcp.db.apis.entities.GW;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStartup;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.jcp.paths.apis.JCPAPIsGWRegistration;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.utils.ParamChecks;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.security.RolesAllowed;


/**
 * Base JCP APIs JCP GWs controller, dedicated to JCP GWs registration and
 * status updates.
 */
@RestController
@Api(tags = {JCPAPIsGWRegistration.SubGroupRegistration.NAME})
public class JCPAPIGWsRegistrationController {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JCPAPIGWsRegistrationController.class);
    @Autowired
    private GWsManager gwManager;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_JCPAPIsGWsRegistration() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(JCPAPIsGWRegistration.SubGroupRegistration.NAME, JCPAPIsGWRegistration.SubGroupRegistration.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(JCPAPIsGWRegistration.API_NAME, JCPAPIsGWRegistration.API_VER, JCPAPIsVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


//    // Constructor
//
//    /**
//     * This default constructor check, for any GW registered in the DB, if it's
//     * online or not. Then, this method, updates the GW status on the DB.
//     */
//    @Autowired
//    public JCPAPIsGWsController(GWsManager gwManager) {
//        this.gwManager = gwManager;
//    }


    // Methods

    /**
     * Allow JCP GW to register their startup and become available for objects
     * and services connections.
     *
     * @return true on GW registration success.
     */
    @PostMapping(path = JCPAPIsGWRegistration.FULL_PATH_STARTUP, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = JCPAPIsGWRegistration.DESCR_PATH_STARTUP,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
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
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Boolean> registerStartup(@RequestHeader(JCPAPIsGWRegistration.HEADER_JCPGWID) String gwId,
                                                   @RequestBody JCPGWsStartup gwStartup) {
        ParamChecks.checkGwId(log, gwId);

        GW gw = gwManager.getById(gwId);

        if (gw == null) {
            gwManager.add(gwId, gwStartup);
            log.info(String.format("Registered JCP GWs '%s' startup of type %s with '%s:%d' address", gwId, gwStartup.type, gwStartup.gwAddr, gwStartup.gwPort));

        } else {
            gwManager.addExisting(gw, gwStartup);
            log.info(String.format("Registered (existing) JCP GWs '%s' startup of type %s with '%s:%d' address", gwId, gwStartup.type, gwStartup.gwAddr, gwStartup.gwPort));
        }

        return ResponseEntity.ok(true);
    }

    /**
     * Allow JCP GW to update their status when clients connects or disconnects.
     *
     * @return true on GW status successfully updated.
     */
    @PostMapping(path = JCPAPIsGWRegistration.FULL_PATH_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = JCPAPIsGWRegistration.DESCR_PATH_STATUS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
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
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Boolean> postStatus(@RequestHeader(JCPAPIsGWRegistration.HEADER_JCPGWID) String gwId,
                                              @RequestBody JCPGWsStatus gwStatus) {
        ParamChecks.checkGwId(log, gwId);

        GW gw = gwManager.getById(gwId);

        if (gw == null) {
            log.warn(String.format("Error updating JCP GWs '%s' because not registered.", gwId));
            return ResponseEntity.ok(false);
        }

        gwManager.update(gw, gwStatus);
        log.trace(String.format("Updated JCP GWs '%s' status of type %s with '%s:%d' address", gwId, gw.getType(), gw.getGwAddr(), gw.getGwPort()));

        return ResponseEntity.ok(true);
    }

    /**
     * Allow JCP GW to register their startup and become available for objects
     * and services connections.
     *
     * @return true on GW registration success.
     */
    @PostMapping(path = JCPAPIsGWRegistration.FULL_PATH_SHUTDOWN, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = JCPAPIsGWRegistration.DESCR_PATH_SHUTDOWN,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
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
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Boolean> registerShutdown(@RequestHeader(JCPAPIsGWRegistration.HEADER_JCPGWID) String gwId) {
        ParamChecks.checkGwId(log, gwId);

        GW gw = gwManager.getById(gwId);

        if (gw == null) {
            log.warn(String.format("Error registering JCP GWs '%s' shutdown because not registered. Skipp error!", gwId));
            return ResponseEntity.ok(true);
        }

        gwManager.remove(gw);
        log.info(String.format("Registered JCP GWs '%s' shutdown of type %s with '%s:%d' address", gwId, gw.getType(), gw.getGwAddr(), gw.getGwPort()));

        return ResponseEntity.ok(true);
    }

}

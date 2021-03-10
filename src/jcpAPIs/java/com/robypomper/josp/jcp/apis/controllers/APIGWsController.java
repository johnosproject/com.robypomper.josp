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

import com.robypomper.java.JavaThreads;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.info.JCPAPIsVersions;
import com.robypomper.josp.jcp.apis.mngs.GWsManager;
import com.robypomper.josp.jcp.clients.jcp.jcp.GWsClient;
import com.robypomper.josp.jcp.db.apis.entities.GW;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jospgws.O2SAccessInfo;
import com.robypomper.josp.params.jospgws.O2SAccessRequest;
import com.robypomper.josp.params.jospgws.S2OAccessInfo;
import com.robypomper.josp.params.jospgws.S2OAccessRequest;
import com.robypomper.josp.paths.APIGWs;
import com.robypomper.josp.paths.APIObjs;
import com.robypomper.josp.paths.APISrvs;
import com.robypomper.josp.types.josp.gw.GWType;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.security.RolesAllowed;

/**
 * API Permission Objs's controller.
 * <p>
 * This controller expose methods used by the JOD Objects and JSL Services to
 * manage their JOSP GWs connections.
 */
@SuppressWarnings("unused")
@RestController
@Api(tags = {APIGWs.SubGroupGWs.NAME})
public class APIGWsController {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(APIGWsController.class);
    @Autowired
    private GWsManager gwManager;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIGWs() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIGWs.SubGroupGWs.NAME, APIGWs.SubGroupGWs.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIGWs.API_NAME, APIGWs.API_VER, JCPAPIsVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods

    @PostMapping(path = APIGWs.FULL_PATH_O2S_ACCESS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIGWs.DESCR_PATH_O2S_ACCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = O2SAccessInfo.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
            @ApiResponse(code = 500, message = "Error adding client certificate"),
            @ApiResponse(code = 503, message = "Internal error, no gateways available certificate")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<O2SAccessInfo> postO2SAccess(
            @RequestHeader(APIObjs.HEADER_OBJID) String objId,
            @RequestBody O2SAccessRequest accessRequest) {

        GW gw = gwManager.getAvailableObj2Srv();
        if (gw == null)
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, String.format("No %s JCP GWs available.", GWType.Obj2Srv));

        GWsClient apiGWsGWs = gwManager.getGWsClient(gw);
        O2SAccessInfo accessInfo;
        try {
            accessInfo = apiGWsGWs.getO2SAccessInfo(objId, accessRequest);
            JavaThreads.softSleep(200);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(String.format("Error retrieve O2S JCP GWs info for '%s' object", objId));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't forward request to '%s' O2S_ACCESS.", gw.getGwId()));
        }

        log.trace(String.format("Provided O2S JCP GWs info '%s:%d' to '%s' object", accessInfo.gwAddress, accessInfo.gwPort, objId));
        return ResponseEntity.ok(accessInfo);
    }


    @PostMapping(path = APIGWs.FULL_PATH_S2O_ACCESS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIGWs.DESCR_PATH_S2O_ACCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = S2OAccessInfo.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APISrvs.HEADER_SRVID),
            @ApiResponse(code = 500, message = "Error adding client certificate"),
            @ApiResponse(code = 503, message = "Internal error, no gateways available certificate")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<S2OAccessInfo> postS2OAccess(
            @RequestHeader(APISrvs.HEADER_SRVID) String srvId,
            @RequestBody S2OAccessRequest accessRequest) {

        GW gw = gwManager.getAvailableSrv2Obj();
        if (gw == null)
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, String.format("No %s JCP GWs available.", GWType.Srv2Obj));

        GWsClient apiGWsGWs = gwManager.getGWsClient(gw);
        S2OAccessInfo accessInfo;
        try {
            accessInfo = apiGWsGWs.getS2OAccessInfo(srvId, accessRequest);
            JavaThreads.softSleep(200);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(String.format("Error retrieve S2O JCP GWs info for '%s' service", srvId));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't forward request to '%s' S2O_ACCESS.", gw.getGwId()));
        }

        log.trace(String.format("Provided S2O JCP GWs info '%s:%d' to '%s' service", accessInfo.gwAddress, accessInfo.gwPort, srvId));
        return ResponseEntity.ok(accessInfo);
    }

}

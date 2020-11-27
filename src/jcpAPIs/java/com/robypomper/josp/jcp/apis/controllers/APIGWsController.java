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

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.info.JCPAPIsVersions;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.clients.JCPGWsClient;
import com.robypomper.josp.jcp.clients.gws.apis.APIGWsGWsClient;
import com.robypomper.josp.jcp.db.apis.GWDBService;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    private static final Logger log = LogManager.getLogger();
    @Autowired
    private GWDBService gwService;
    @Autowired
    private SwaggerConfigurer swagger;
    @Autowired
    private ClientParams gwsClientsParams;
    private Map<GW, APIGWsGWsClient> apiGWsGWsClients = new HashMap<>();


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIGWs() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIGWs.SubGroupGWs.NAME, APIGWs.SubGroupGWs.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIGWs.API_NAME, APIGWs.API_VER, JCPAPIsVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods

    @PostMapping(path = APIGWs.FULL_PATH_O2S_ACCESS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Set object's certificate and request JOSPGw O2S access info",
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
            @RequestHeader(APIObjs.HEADER_OBJID)
                    String objId,
            @RequestBody
                    O2SAccessRequest accessRequest) {

        // Get free GW url

        Optional<GW> gwOpt = gwService.findAvailableGW(GWType.Obj2Srv);
        if (!gwOpt.isPresent())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, String.format("No %s GWs available.", GWType.Obj2Srv));

        // Forward request to free GW
        try {
            APIGWsGWsClient apiGWsGWs = getAPIGWsGWsClient(gwOpt.get());
            return ResponseEntity.ok(apiGWsGWs.getO2SAccessInfo(objId, accessRequest));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't forward request to '%s' O2S_ACCESS.", gwOpt.get().getGwId()));
        }
    }


    @PostMapping(path = APIGWs.FULL_PATH_S2O_ACCESS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Set service's certificate and request JOSPGw S2O access info",
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
            @RequestHeader(APISrvs.HEADER_SRVID)
                    String srvId,
            @RequestBody
                    S2OAccessRequest accessRequest) {

        // Get free GW url
        Optional<GW> gwOpt = gwService.findAvailableGW(GWType.Srv2Obj);
        if (!gwOpt.isPresent())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, String.format("No %s GWs available.", GWType.Srv2Obj));

        // Forward request to free GW
        try {
            APIGWsGWsClient apiGWsGWs = getAPIGWsGWsClient(gwOpt.get());
            return ResponseEntity.ok(apiGWsGWs.getS2OAccessInfo(srvId, accessRequest));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't forward request to '%s' S2O_ACCESS.", gwOpt.get().getGwId()));
        }
    }


    protected APIGWsGWsClient getAPIGWsGWsClient(GW gw) {
        if (apiGWsGWsClients.get(gw) == null) {
            String url = String.format("%s:%d", gw.getGwAPIsAddr(), gw.getGwAPIsPort());
            apiGWsGWsClients.put(gw, new APIGWsGWsClient(new JCPGWsClient(gwsClientsParams, url)));
        }
        return apiGWsGWsClients.get(gw);
    }

}

package com.robypomper.josp.jcp.apis.controllers.josp.admin.gateways.buildinfo;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.callers.base.status.buildinfo.Caller20;
import com.robypomper.josp.defs.admin.gateways.buildinfo.Params20;
import com.robypomper.josp.defs.admin.gateways.buildinfo.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerLink;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.clients.JCPGWsClient;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.types.RESTItemList;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;


/**
 * JOSP Admin - Gateways / Build Info 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLink {

    // Internal vars

    private JCPClientsMngr clientsMngr;


    // List methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_BUILDINFO_LIST)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_BUILDINFO_LIST)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Gateways's server list", response = Params20.GatewaysServers.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Params20.GatewaysServers> getList() {
        Params20.GatewaysServers gwServers = new Params20.GatewaysServers();
        gwServers.serverList = new ArrayList<>();
        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            RESTItemList gwServer = new RESTItemList();
            gwServer.id = gwClient.getClientId();
            gwServer.name = gwClient.getClientId();
            gwServer.url = Paths20.FULL_PATH_JCP_GWS_BUILDINFO(gwClient.getClientId());
        }
        return ResponseEntity.ok(gwServers);
    }


    // Methods JCP GWs

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_BUILDINFO)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_BUILDINFO,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GW's instance status", response = Params20.BuildInfo.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.BuildInfo> getBuildInfoReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getBuildInfoReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

}
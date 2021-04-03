package com.robypomper.josp.jcp.apis.controllers.josp.admin.gateways.status;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.callers.gateways.status.Caller20;
import com.robypomper.josp.defs.admin.gateways.status.Params20;
import com.robypomper.josp.defs.admin.gateways.status.Paths20;
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
 * JOSP Admin - Gateways / Status 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLink {

    // Internal vars

    private JCPClientsMngr clientsMngr;


    // List methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_LIST)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_LIST)
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
            gwServer.url = Paths20.FULL_PATH_JCP_GWS_STATUS(gwClient.getClientId());
        }
        return ResponseEntity.ok(gwServers);
    }


    // Index methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS)
    @ApiOperation(value = Paths20.FULL_PATH_JCP_GWS_STATUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Gateways's status index", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Params20.Index> getIndex(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId) {
        return ResponseEntity.ok(new Params20.Index(gwServerId));
    }


    // GWs status methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_GWS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_GWS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GWs list", response = Params20.GWs.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.GWs> getGWsReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getGWsReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_GW)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_GW,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GWs list", response = Params20.GW.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.GW> getGWReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
                                                @PathVariable(Paths20.PARAM_GW) String gwId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getGWReq(gwId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_GW_CLIENT)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_GW_CLIENT,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GWs list", response = Params20.GWClient.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.GWClient> getGWClientReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
                                                            @PathVariable(Paths20.PARAM_GW) String gwId,
                                                            @PathVariable(Paths20.PARAM_GW_CLIENT) String gwClientId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getGWsClientReq(gwId, gwClientId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }


    // Broker status methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_BROKER,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP Broker's objects and services list", response = Params20.Broker.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Broker> getBrokerReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getBrokerReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_BROKER_OBJ,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP Broker's object info", response = Params20.BrokerObject.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.BrokerObject> getBrokerObjectReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
                                                                    @PathVariable(Paths20.PARAM_OBJ) String objId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getBrokerObjectReq(objId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_SRV)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_BROKER_SRV,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP Broker's object info", response = Params20.BrokerService.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.BrokerService> getBrokerServiceReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
                                                                      @PathVariable(Paths20.PARAM_SRV) String srvId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getBrokerServiceReq(srvId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ_DB)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_BROKER_OBJ_DB,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP Broker's object (DB) info", response = Params20.BrokerObjectDB.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.BrokerObjectDB> getBrokerObjectDBReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
                                                                        @PathVariable(Paths20.PARAM_OBJ) String objId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getBrokerObjectDBReq(objId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

}

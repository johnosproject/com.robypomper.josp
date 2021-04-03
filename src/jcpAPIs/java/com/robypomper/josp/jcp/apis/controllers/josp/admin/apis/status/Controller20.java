package com.robypomper.josp.jcp.apis.controllers.josp.admin.apis.status;

import com.robypomper.josp.defs.admin.apis.status.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerLink;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.defs.apis.internal.status.Params20;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;


/**
 * JOSP Admin - APIs / Status 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLink {

    // Internal vars

    private JCPClientsMngr clientsMngr;
    @Autowired
    private com.robypomper.josp.jcp.apis.controllers.internal.status.Controller20 apiClient;


    // Index methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_STATUS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_STATUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's status index", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Params20.Index> getIndex() {
        return ResponseEntity.ok(new Params20.Index());
    }


    // Objects methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_STATUS_OBJS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_STATUS_OBJS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's objects list", response = Params20.Objects.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Objects> getObjectsReq() {
        return apiClient.getObjectsReq();
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_STATUS_OBJ)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_STATUS_OBJ,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's object info", response = Params20.Object.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Object> getObjectReq(@PathVariable(Paths20.PARAM_OBJ) String objId) {
        return apiClient.getObjectReq(objId);
    }


    // Services methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_STATUS_SRVS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_STATUS_SRVS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's services list", response = Params20.Services.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Services> getServicesReq() {
        return apiClient.getServicesReq();
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_STATUS_SRV)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_STATUS_SRV,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's service info", response = Params20.Service.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Service> getServiceReq(@PathVariable(Paths20.PARAM_SRV) String srvId) {
        return apiClient.getServiceReq(srvId);
    }


    // Users methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_STATUS_USRS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_STATUS_USRS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's users list", response = Params20.Users.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Users> getUsersReq() {
        return apiClient.getUsersReq();
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_STATUS_USR)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_STATUS_USR,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's user info", response = Params20.User.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.User> getUserReq(@PathVariable(Paths20.PARAM_USR) String usrId) {
        return apiClient.getUserReq(usrId);
    }


    // Gateways methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_STATUS_GWS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_STATUS_GWS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Gateways list", response = Params20.Gateways.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Gateways> getGatewaysReq() {
        return apiClient.getGatewaysReq();
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_STATUS_GW)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_STATUS_GW,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's gateway info", response = Params20.Gateway.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Gateway> getGatewayReq(@PathVariable(Paths20.PARAM_GW) String gwId) {
        return apiClient.getGatewayReq(gwId);
    }

}

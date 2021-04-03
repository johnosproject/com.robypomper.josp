package com.robypomper.josp.jcp.apis.controllers.josp.admin.frontend.status;

import com.robypomper.josp.defs.admin.frontend.status.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerLink;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.defs.apis.internal.status.Params20;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * JOSP Admin - Front End / Status 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLink {

    // Internal vars

    @Autowired
    private JCPClientsMngr clientsMngr;


    // Index methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_FE_STATUS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_FE_STATUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's status index", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Params20.Index> getIndex() {
        return ResponseEntity.ok(new Params20.Index());
    }


    //// Sessions methods

    //@GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_STATUS_...)
    //@ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_STATUS_...,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_MNG_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "JCP JSL WebBridge's web sessions list", response = Params20.....class),
    //        @ApiResponse(code = 401, message = "User not authenticated"),
    //        @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    //public ResponseEntity<Params20....> getJCPJSLWBSessionsReq() {
    //    JCPFEClient client = clientsMngr.getJCPFEClient();
    //    Caller20 caller = new Caller20(client);
    //    try {
    //        return ResponseEntity.ok(caller.get...Req());
    //
    //    } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
    //        throw jcpServiceNotAvailable(client, e);
    //    }
    //}

}

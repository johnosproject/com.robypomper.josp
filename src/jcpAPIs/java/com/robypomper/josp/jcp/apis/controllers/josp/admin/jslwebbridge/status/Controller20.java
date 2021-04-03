package com.robypomper.josp.jcp.apis.controllers.josp.admin.jslwebbridge.status;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.callers.jslwebbridge.status.Caller20;
import com.robypomper.josp.defs.admin.jslwebbridge.status.Params20;
import com.robypomper.josp.defs.admin.jslwebbridge.status.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerLink;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.clients.JCPJSLWebBridgeClient;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;


/**
 * JOSP Admin - JSL Web Bridge / Status 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLink {

    // Internal vars

    @Autowired
    private JCPClientsMngr clientsMngr;


    // Index methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_STATUS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_STATUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Web Bridge's status index", response = com.robypomper.josp.jcp.defs.apis.internal.status.Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<com.robypomper.josp.jcp.defs.apis.internal.status.Params20.Index> getIndex() {
        return ResponseEntity.ok(new com.robypomper.josp.jcp.defs.apis.internal.status.Params20.Index());
    }


    // Sessions methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_STATUS_SESSIONS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_STATUS_SESSIONS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL WebBridge's web sessions list", response = Params20.Sessions.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Sessions> getJCPJSLWBSessionsReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getSessionsReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_STATUS_SESSION)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_STATUS_SESSION,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP required JSL WebBridge's web session", response = Params20.Session.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Session> getJCPJSLWBSessionsReq(@PathVariable(Paths20.PARAM_SESSIONS) String sessionId) {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getSessionReq(sessionId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

}

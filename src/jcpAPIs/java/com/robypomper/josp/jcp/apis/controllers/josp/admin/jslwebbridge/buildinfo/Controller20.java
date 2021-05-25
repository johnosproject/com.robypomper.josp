package com.robypomper.josp.jcp.apis.controllers.josp.admin.jslwebbridge.buildinfo;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.callers.base.status.buildinfo.Caller20;
import com.robypomper.josp.defs.admin.jslwebbridge.buildinfo.Params20;
import com.robypomper.josp.defs.admin.jslwebbridge.buildinfo.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerLink;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.clients.JCPFEClient;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.clients.JCPJSLWebBridgeClient;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;


/**
 * JOSP Admin - JSL Web Bridge / Build Info 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLink {

    // Internal vars

    @Autowired
    private JCPClientsMngr clientsMngr;


    // Build info methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_BUILDINFO, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_BUILDINFO,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's build info", response = Params20.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.BuildInfo> getBuildInfoReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getBuildInfoReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

}

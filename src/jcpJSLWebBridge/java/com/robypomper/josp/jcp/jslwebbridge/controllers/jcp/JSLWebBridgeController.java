package com.robypomper.josp.jcp.jslwebbridge.controllers.jcp;

import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.JCPJSLWebBridgeStatus;
import com.robypomper.josp.paths.jcp.APIJCP;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJCP.SubGroupJSLWebBridgeStatus.NAME})
public class JSLWebBridgeController {

    @GetMapping(path = APIJCP.FULL_PATH_JSLWB_STATUS)
    @ApiOperation(value = "Return JCP JSL Web Bridge info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's info and stats", response = JCPJSLWebBridgeStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPJSLWebBridgeStatus> getJCPJSLWebBridgeReq() {
        return ResponseEntity.ok(new JCPJSLWebBridgeStatus());
    }

}

package com.robypomper.josp.jcp.gws.controllers.jcp;

import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.paths.APIJCP;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJCP.SubGroupGWsStatus.NAME})
public class APIJCPGWsController {

    @GetMapping(path = APIJCP.FULL_PATH_GWS_STATUS)
    @ApiOperation(value = "Return JCP GWs info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's info and stats", response = JCPGWsStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPGWsStatus> getJCPGWsReq() {
        return ResponseEntity.ok(new JCPGWsStatus());
    }

}

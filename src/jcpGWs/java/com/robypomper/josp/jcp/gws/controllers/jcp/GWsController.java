package com.robypomper.josp.jcp.gws.controllers.jcp;

import com.robypomper.josp.jcp.gws.controllers.APIGWsGWsController;
import com.robypomper.josp.jcp.gws.services.GWServiceO2S;
import com.robypomper.josp.jcp.gws.services.GWServiceS2O;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.JCPAPIsStatus;
import com.robypomper.josp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.paths.jcp.APIJCP;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJCP.SubGroupGWsStatus.NAME})
public class GWsController {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(APIGWsGWsController.class);
    @Autowired
    private GWServiceO2S gwO2SService;
    @Autowired
    private GWServiceS2O gwS2OService;


    // Methods

    @GetMapping(path = APIJCP.FULL_PATH_GWS_STATUS)
    @ApiOperation(value = "Return JCP GWs info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's info and stats", response = JCPGWsStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<JCPGWsStatus> getJCPGWsReq() {
        return ResponseEntity.ok(new JCPGWsStatus());
    }

    @GetMapping(path = APIJCP.FULL_PATH_GWS_STATUS_CLI)
    @ApiOperation(value = "Return JOSP GWs info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GW's info and stats", response = JCPAPIsStatus.GWs.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<List<JCPAPIsStatus.GWs>> getJCPAPIsStatusGWsCliReq() {
        List<JCPAPIsStatus.GWs> gws = new ArrayList<>();

        gws.add(gwO2SService.getJCPAPIsStatus());
        gws.add(gwS2OService.getJCPAPIsStatus());

        return ResponseEntity.ok(gws);
    }

}

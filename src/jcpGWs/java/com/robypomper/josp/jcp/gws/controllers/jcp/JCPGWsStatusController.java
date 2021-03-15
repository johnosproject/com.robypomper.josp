package com.robypomper.josp.jcp.gws.controllers.jcp;

import com.robypomper.jcpGWs.BuildInfoJcpGWs;
import com.robypomper.josp.jcp.gws.services.GWServiceO2S;
import com.robypomper.josp.jcp.gws.services.GWServiceS2O;
import com.robypomper.josp.jcp.info.JCPGWsVersions;
import com.robypomper.josp.jcp.paths.gws.JCPGWsStatus;
import com.robypomper.josp.jcp.service.controllers.jcp.JCPStatusControllerAbs;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.GWsStatus;
import com.robypomper.josp.params.jcp.ServiceStatus;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
@RestController
@Api(tags = {JCPGWsStatus.SubGroupGWsStatus.NAME})
public class JCPGWsStatusController extends JCPStatusControllerAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JCPGWsStatusController.class);
    @Autowired
    private GWServiceO2S gwO2SService;
    @Autowired
    private GWServiceS2O gwS2OService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_JCPAPIsGWsStatus() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(JCPGWsStatus.SubGroupGWsStatus.NAME, JCPGWsStatus.SubGroupGWsStatus.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(JCPGWsStatus.API_NAME, JCPGWsStatus.API_VER, JCPGWsVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods

    @Override
    protected ServiceStatus getInstanceReqSubClass() {
        return new GWsStatus(BuildInfoJcpGWs.Current);
    }

    @GetMapping(path = JCPGWsStatus.FULL_PATH_GWS_STATUS_CLI)
    @ApiOperation(value = JCPGWsStatus.DESCR_PATH_GWS_STATUS_CLI,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GW's info and stats", response = GWsStatus.Server.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<List<GWsStatus.Server>> getJCPAPIsStatusGWsCliReq() {
        List<GWsStatus.Server> gws = new ArrayList<>();

        gws.add(gwO2SService.get().getJCPAPIsStatus());
        gws.add(gwS2OService.get().getJCPAPIsStatus());

        return ResponseEntity.ok(gws);
    }

}

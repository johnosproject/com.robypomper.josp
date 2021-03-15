package com.robypomper.josp.jcp.apis.controllers.jcp;

import com.robypomper.jcpAPIs.BuildInfoJcpAPIs;
import com.robypomper.josp.info.JCPAPIsVersions;
import com.robypomper.josp.jcp.apis.mngs.GWsManager;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.UserDBService;
import com.robypomper.josp.jcp.paths.apis.JCPAPIsStatus;
import com.robypomper.josp.jcp.service.controllers.jcp.JCPStatusControllerAbs;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.APIsStatus;
import com.robypomper.josp.params.jcp.ServiceStatus;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;


@SuppressWarnings("unused")
@RestController
@Api(tags = {JCPAPIsStatus.SubGroupAPIsStatus.NAME})
public class JCPAPIsStatusController extends JCPStatusControllerAbs {

    // Internal vars

    @Autowired
    private ObjectDBService objDB;
    @Autowired
    private ServiceDBService srvDB;
    @Autowired
    private UserDBService usrDB;
    @Autowired
    private GWsManager gwManager;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private ClientParams gwsClientsParams;

    private final String OAUTH_FLOW = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG;
    private final String OAUTH_SCOPE = SwaggerConfigurer.ROLE_MNG_SWAGGER;
    private final String OAUTH_DESCR = SwaggerConfigurer.ROLE_MNG_DESC;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_JCPAPIsStatus() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(JCPAPIsStatus.SubGroupAPIsStatus.NAME, JCPAPIsStatus.SubGroupAPIsStatus.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(JCPAPIsStatus.API_NAME, JCPAPIsStatus.API_VER, JCPAPIsVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods

    @Override
    protected ServiceStatus getInstanceReqSubClass() {
        return new APIsStatus(BuildInfoJcpAPIs.Current);
    }


    @GetMapping(path = JCPAPIsStatus.FULL_PATH_APIS_STATUS_OBJS)
    @ApiOperation(value = JCPAPIsStatus.DESCR_PATH_APIS_STATUS_OBJS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Objects info and stats", response = APIsStatus.Objects.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<APIsStatus.Objects> getJCPAPIsStatusObjsReq() {
        APIsStatus.Objects jcpStatus = new APIsStatus.Objects();

        jcpStatus.count = objDB.count();
        jcpStatus.onlineCount = objDB.countOnline();
        jcpStatus.offlineCount = objDB.countOffline();
        jcpStatus.activeCount = objDB.countActive();
        jcpStatus.inactiveCount = objDB.countInactive();
        jcpStatus.ownersCount = objDB.countOwners();

        return ResponseEntity.ok(jcpStatus);
    }

    @GetMapping(path = JCPAPIsStatus.FULL_PATH_APIS_STATUS_SRVS)
    @ApiOperation(value = JCPAPIsStatus.DESCR_PATH_APIS_STATUS_SRVS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Services info and stats", response = APIsStatus.Services.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<APIsStatus.Services> getJCPAPIsStatusSrvsReq() {
        APIsStatus.Services jcpStatus = new APIsStatus.Services();

        jcpStatus.count = srvDB.count();
        jcpStatus.onlineCount = srvDB.countOnline();
        jcpStatus.offlineCount = srvDB.countOffline();
        jcpStatus.instancesCount = srvDB.countInstances();
        jcpStatus.instancesOnlineCount = srvDB.countInstancesOnline();
        jcpStatus.instancesOfflineCount = srvDB.countInstancesOffline();

        return ResponseEntity.ok(jcpStatus);
    }

    @GetMapping(path = JCPAPIsStatus.FULL_PATH_APIS_STATUS_USRS)
    @ApiOperation(value = JCPAPIsStatus.DESCR_PATH_APIS_STATUS_USRS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Users info and stats", response = APIsStatus.Users.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<APIsStatus.Users> getJCPAPIsStatusUsrsReq() {
        APIsStatus.Users jcpStatus = new APIsStatus.Users();

        jcpStatus.count = usrDB.count();

        return ResponseEntity.ok(jcpStatus);
    }

}

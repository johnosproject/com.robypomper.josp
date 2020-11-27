package com.robypomper.josp.jcp.apis.controllers.jcp;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.clients.JCPGWsClientMngr;
import com.robypomper.josp.jcp.clients.jcp.jcp.GWsClient;
import com.robypomper.josp.jcp.db.apis.GWDBService;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.UserDBService;
import com.robypomper.josp.jcp.db.apis.entities.GW;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.JCPAPIsStatus;
import com.robypomper.josp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.paths.jcp.APIJCP;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJCP.SubGroupAPIsStatus.NAME})
public class APIsController {

    // Internal vars

    @Autowired
    private ObjectDBService objDB;
    @Autowired
    private ServiceDBService srvDB;
    @Autowired
    private UserDBService usrDB;
    @Autowired
    private GWDBService gwDB;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private ClientParams gwsClientsParams;
    @Autowired
    private JCPGWsClientMngr<GWsClient> apiGWsGWsClients;

    private final String OAUTH_FLOW = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG;
    private final String OAUTH_SCOPE = SwaggerConfigurer.ROLE_MNG_SWAGGER;
    private final String OAUTH_DESCR = SwaggerConfigurer.ROLE_MNG_DESC;


    // Methods

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS)
    @ApiOperation(value = "Return JCP APIs info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's info and stats", response = JCPAPIsStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPAPIsStatus> getJCPAPIsStatusReq() {
        return ResponseEntity.ok(new JCPAPIsStatus());
    }

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS_GWS)
    @ApiOperation(value = "Return JOSP GWs clients",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GW's clients", response = JCPAPIsStatus.GWs.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPGWsStatus>> getJCPAPIsStatusGWsReq() {
        List<JCPGWsStatus> gws = new ArrayList<>();

        for (GW gw : gwDB.getAll()) {
            GWsClient apiGWs = apiGWsGWsClients.getAPIGWsGWsClient(gw.getGwId(), gw.getGwAPIsAddr(), gw.getGwAPIsPort(), GWsClient.class);
            String url = String.format("%s:%d", gw.getGwAPIsAddr(), gw.getGwAPIsPort());
            try {
                gws.add(apiGWs.getJCPGWsReq());
            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok(gws);
    }

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS_GWS_CLI)
    @ApiOperation(value = "Return JOSP GWs info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GW's info and stats", response = JCPAPIsStatus.GWs.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPAPIsStatus.GWs>> getJCPAPIsStatusGWsGWsReq() {
        List<JCPAPIsStatus.GWs> gws = new ArrayList<>();

        for (GW gw : gwDB.getAll()) {
            GWsClient apiGWs = apiGWsGWsClients.getAPIGWsGWsClient(gw.getGwId(), gw.getGwAPIsAddr(), gw.getGwAPIsPort(), GWsClient.class);
            String url = String.format("%s:%d", gw.getGwAPIsAddr(), gw.getGwAPIsPort());
            try {
                List<JCPAPIsStatus.GWs> gwsTmp = apiGWs.getJCPAPIsStatusGWsCliReq();
                gws.addAll(gwsTmp);
            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
                e.printStackTrace();
            }

        }

        return ResponseEntity.ok(gws);
    }

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS_OBJS)
    @ApiOperation(value = "Return JCP APIs's Objects info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Objects info and stats", response = JCPAPIsStatus.Objects.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPAPIsStatus.Objects> getJCPAPIsStatusObjsReq() {
        JCPAPIsStatus.Objects jcpStatus = new JCPAPIsStatus.Objects();

        jcpStatus.count = objDB.count();
        jcpStatus.onlineCount = objDB.countOnline();
        jcpStatus.offlineCount = objDB.countOffline();
        jcpStatus.activeCount = objDB.countActive();
        jcpStatus.inactiveCount = objDB.countInactive();
        jcpStatus.ownersCount = objDB.countOwners();

        return ResponseEntity.ok(jcpStatus);
    }

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS_SRVS)
    @ApiOperation(value = "Return JCP APIs Services info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Services info and stats", response = JCPAPIsStatus.Services.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPAPIsStatus.Services> getJCPAPIsStatusSrvsReq() {
        JCPAPIsStatus.Services jcpStatus = new JCPAPIsStatus.Services();

        jcpStatus.count = srvDB.count();
        jcpStatus.onlineCount = srvDB.countOnline();
        jcpStatus.offlineCount = srvDB.countOffline();
        jcpStatus.instancesCount = srvDB.countInstances();
        jcpStatus.instancesOnlineCount = srvDB.countInstancesOnline();
        jcpStatus.instancesOfflineCount = srvDB.countInstancesOffline();

        return ResponseEntity.ok(jcpStatus);
    }

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS_USRS)
    @ApiOperation(value = "Return JCP APIs Users info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Users info and stats", response = JCPAPIsStatus.Users.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPAPIsStatus.Users> getJCPAPIsStatusUsrsReq() {
        JCPAPIsStatus.Users jcpStatus = new JCPAPIsStatus.Users();

        jcpStatus.count = usrDB.count();

        return ResponseEntity.ok(jcpStatus);
    }

}

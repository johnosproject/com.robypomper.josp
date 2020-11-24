package com.robypomper.josp.jcp.apis.controllers.jcp;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.UserDBService;
import com.robypomper.josp.jcp.gw.JOSPGWsO2SService;
import com.robypomper.josp.jcp.gw.JOSPGWsS2OService;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.JCPAPIsStatus;
import com.robypomper.josp.paths.APIJCP;
import com.robypomper.josp.types.josp.gw.GWType;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = {APIJCP.SubGroupAPIsStatus.NAME})
public class APIsController {

    @Autowired
    private ObjectDBService objDB;
    @Autowired
    private ServiceDBService srvDB;
    @Autowired
    private UserDBService usrDB;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private JOSPGWsO2SService gwO2SService;
    @Autowired
    private JOSPGWsS2OService gwS2OService;

    private final String OAUTH_FLOW = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG;
    private final String OAUTH_SCOPE = SwaggerConfigurer.ROLE_MNG_SWAGGER;
    private final String OAUTH_DESCR = SwaggerConfigurer.ROLE_MNG_DESC;


    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS)
    @ApiOperation(value = "Return JCP APIs info and stats"/*,
            authorizations = @Authorization(value = OAUTH_FLOW,scopes = @AuthorizationScope(scope = OAUTH_DESCR,description = OAUTH_DESCR))*/
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's info and stats", response = JCPAPIsStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    /*@RolesAllowed(SwaggerConfigurer.ROLE_MNG)*/
    public ResponseEntity<JCPAPIsStatus> getJCPAPIsStatusReq() {
        return ResponseEntity.ok(new JCPAPIsStatus());
    }

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS_OBJS)
    @ApiOperation(value = "Return JCP APIs's Objects info and stats"/*,
            authorizations = @Authorization(value = OAUTH_FLOW,scopes = @AuthorizationScope(scope = OAUTH_DESCR,description = OAUTH_DESCR))*/
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Objects info and stats", response = JCPAPIsStatus.Objects.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    /*@RolesAllowed(SwaggerConfigurer.ROLE_MNG)*/
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
    @ApiOperation(value = "Return JCP APIs Services info and stats"/*,
            authorizations = @Authorization(value = OAUTH_FLOW,scopes = @AuthorizationScope(scope = OAUTH_DESCR,description = OAUTH_DESCR))*/
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Services info and stats", response = JCPAPIsStatus.Services.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    /*@RolesAllowed(SwaggerConfigurer.ROLE_MNG)*/
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

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS_GWS)
    @ApiOperation(value = "Return JOSP GWs info and stats"/*,
            authorizations = @Authorization(value = OAUTH_FLOW,scopes = @AuthorizationScope(scope = OAUTH_DESCR,description = OAUTH_DESCR))*/
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GW's info and stats", response = JCPAPIsStatus.GWs.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    /*@RolesAllowed(SwaggerConfigurer.ROLE_MNG)*/
    public ResponseEntity<List<JCPAPIsStatus.GWs>> getJCPAPIsStatusGWs_GWReq() {
        List<JCPAPIsStatus.GWs> gws = new ArrayList<>();

        for (Server s : gwO2SService.getJOSPServers().values()) {
            JCPAPIsStatus.GWs gw = new JCPAPIsStatus.GWs();
            gw.id = s.getServerId();
            gw.type = GWType.Obj2Srv;
            gw.isRunning = s.isRunning();
            gw.address = s.getAddress().getHostAddress();
            gw.hostName = s.getAddress().getHostName();
            gw.hostNameCanonical = s.getAddress().getCanonicalHostName();
            gw.port = s.getPort();
            gw.clientsCount = s.getClients().size();
            gw.clientsList = new ArrayList<>();
            for (ClientInfo c : s.getClients())
                gw.clientsList.add(new JCPAPIsStatus.GWs.Client(c.getClientId(), c.isConnected()));

            gws.add(gw);
        }

        for (Server s : gwS2OService.getJOSPServers().values()) {
            JCPAPIsStatus.GWs gw = new JCPAPIsStatus.GWs();
            gw.id = s.getServerId();
            gw.type = GWType.Srv2Obj;
            gw.isRunning = s.isRunning();
            gw.address = s.getAddress().getHostAddress();
            gw.hostName = s.getAddress().getHostName();
            gw.hostNameCanonical = s.getAddress().getCanonicalHostName();
            gw.port = s.getPort();
            gw.clientsCount = s.getClients().size();
            gw.clientsList = new ArrayList<>();
            for (ClientInfo c : s.getClients())
                gw.clientsList.add(new JCPAPIsStatus.GWs.Client(c.getClientId(), c.isConnected()));

            gws.add(gw);
        }

        return ResponseEntity.ok(gws);
    }

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS_USRS)
    @ApiOperation(value = "Return JCP APIs Users info and stats"/*,
            authorizations = @Authorization(value = OAUTH_FLOW,scopes = @AuthorizationScope(scope = OAUTH_DESCR,description = OAUTH_DESCR))*/
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Users info and stats", response = JCPAPIsStatus.Users.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    /*@RolesAllowed(SwaggerConfigurer.ROLE_MNG)*/
    public ResponseEntity<JCPAPIsStatus.Users> getJCPAPIsStatusUsrsReq() {
        JCPAPIsStatus.Users jcpStatus = new JCPAPIsStatus.Users();

        jcpStatus.count = usrDB.count();

        return ResponseEntity.ok(jcpStatus);
    }

}

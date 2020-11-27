package com.robypomper.josp.jcp.gws.controllers.jcp;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.josp.jcp.gws.services.GWsO2SService;
import com.robypomper.josp.jcp.gws.services.GWsS2OService;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.JCPAPIsStatus;
import com.robypomper.josp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.paths.jcp.APIJCP;
import com.robypomper.josp.types.josp.gw.GWType;
import io.swagger.annotations.*;
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

    @Autowired
    private GWsO2SService gwO2SService;
    @Autowired
    private GWsS2OService gwS2OService;


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

        Server s = gwO2SService.getServer();
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

        s = gwS2OService.getServer();
        gw = new JCPAPIsStatus.GWs();
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

        return ResponseEntity.ok(gws);
    }

}

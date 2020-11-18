package com.robypomper.josp.jcp.apis.jcp;

import com.robypomper.cloud.apis.CloudStatusControllerBase;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.josp.params.admin.JCPCloudStatus;
import com.robypomper.josp.paths.APIMngr;
import com.robypomper.josp.jcp.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.gw.JOSPGWsO2SService;
import com.robypomper.josp.jcp.gw.JOSPGWsS2OService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = {APIMngr.SubGroupGWs.NAME})
public class MngmGWsController extends CloudStatusControllerBase {

    // Internal vars

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private JOSPGWsO2SService gwO2SService;
    @Autowired
    private JOSPGWsS2OService gwS2OService;

    @GetMapping(path = APIMngr.FULL_PATH_MNGM_GW)
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
            @ApiResponse(code = 200, message = "JOSP GW's info and stats", response = JCPCloudStatus.JCPGWs.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPCloudStatus.JCPGWs>> getJCPAPIs_GWReq() {
        List<JCPCloudStatus.JCPGWs> gws = new ArrayList<>();

        for (Server s : gwO2SService.getJOSPServers().values()) {
            JCPCloudStatus.JCPGWs gw = new JCPCloudStatus.JCPGWs();
            gw.id = s.getServerId();
            gw.type = JCPCloudStatus.JCPGWs.Type.O2S;
            gw.isRunning = s.isRunning();
            gw.address = s.getAddress().getHostAddress();
            gw.hostName = s.getAddress().getHostName();
            gw.hostNameCanonical = s.getAddress().getCanonicalHostName();
            gw.port = s.getPort();
            gw.clientsCount = s.getClients().size();
            gw.clientsList = new ArrayList<>();
            for (ClientInfo c : s.getClients())
                gw.clientsList.add(new JCPCloudStatus.JCPGWs.Client(c.getClientId(), c.isConnected()));

            gws.add(gw);
        }

        for (Server s : gwS2OService.getJOSPServers().values()) {
            JCPCloudStatus.JCPGWs gw = new JCPCloudStatus.JCPGWs();
            gw.id = s.getServerId();
            gw.type = JCPCloudStatus.JCPGWs.Type.S2O;
            gw.isRunning = s.isRunning();
            gw.address = s.getAddress().getHostAddress();
            gw.hostName = s.getAddress().getHostName();
            gw.hostNameCanonical = s.getAddress().getCanonicalHostName();
            gw.port = s.getPort();
            gw.clientsCount = s.getClients().size();
            gw.clientsList = new ArrayList<>();
            for (ClientInfo c : s.getClients())
                gw.clientsList.add(new JCPCloudStatus.JCPGWs.Client(c.getClientId(), c.isConnected()));

            gws.add(gw);
        }

        return ResponseEntity.ok(gws);
    }

}

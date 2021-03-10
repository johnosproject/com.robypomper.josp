package com.robypomper.josp.jcp.apis.controllers;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.info.JCPAPIsVersions;
import com.robypomper.josp.jcp.apis.controllers.jcp.JCPAPIsStatusController;
import com.robypomper.josp.jcp.apis.mngs.GWsManager;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.clients.JCPFEClient;
import com.robypomper.josp.jcp.clients.JCPJSLWebBridgeClient;
import com.robypomper.josp.jcp.clients.jcp.jcp.FEClient;
import com.robypomper.josp.jcp.clients.jcp.jcp.GWsClient;
import com.robypomper.josp.jcp.clients.jcp.jcp.JSLWebBridgeClient;
import com.robypomper.josp.jcp.db.apis.entities.GW;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.*;
import com.robypomper.josp.paths.APIAdmin;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIAdmin.SubGroupAdmin.NAME})
public class APIAdminController {

    // Class constants

    private final String OAUTH_FLOW = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG;
    private final String OAUTH_SCOPE = SwaggerConfigurer.ROLE_MNG_SWAGGER;
    private final String OAUTH_DESCR = SwaggerConfigurer.ROLE_MNG_DESC;


    // Internal vars

    @Autowired
    private JCPAPIsStatusController apiClient;
    @Autowired
    private GWsManager gwManager;
    private final JSLWebBridgeClient wbClient;
    private final FEClient feClient;
    @Autowired
    private SwaggerConfigurer swagger;

    @Autowired
    public APIAdminController(ClientParams params, @Value("${jcp.urlJSLWebBridge}") String urlJSLWB, @Value("${jcp.urlFE}") String urlFE) {
        wbClient = new JSLWebBridgeClient(new JCPJSLWebBridgeClient(params, urlJSLWB, true));
        feClient = new FEClient(new JCPFEClient(params, urlFE, true));
    }


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIAdmin() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIAdmin.SubGroupAdmin.NAME, APIAdmin.SubGroupAdmin.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIAdmin.API_NAME, APIAdmin.API_VER, JCPAPIsVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods JCP APIs

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_INSTANCE)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_INSTANCE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's info and stats", response = APIsStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<APIsStatus> getJCPAPIsInstanceReq() {
        return apiClient.getInstanceReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_OBJS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_OBJS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's info and stats", response = APIsStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<APIsStatus.Objects> getJCPAPIsObjsReq() {
        return apiClient.getJCPAPIsStatusObjsReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_SRVS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_SRVS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's info and stats", response = APIsStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<APIsStatus.Services> getJCPAPIsSrvseReq() {
        return apiClient.getJCPAPIsStatusSrvsReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_USRS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_USRS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's info and stats", response = APIsStatus.Users.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<APIsStatus.Users> getJCPAPIsUsrReq() {
        return apiClient.getJCPAPIsStatusUsrsReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_ONLINE)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_ONLINE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable ONLINE", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<String> getJCPAPIsExecONLINE() {
        return apiClient.getOnlineReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_CPU)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_CPU,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable CPU", response = JCPStatus.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.CPU> getJCPAPIsExecCPU() {
        return apiClient.getCPUReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_DISKS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_DISKS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable DISKS", response = JCPStatus.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Disks> getJCPAPIsExecDISKS() {
        return apiClient.getDisksReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_JAVA)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_JAVA,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable JAVA", response = JCPStatus.Java.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Java> getJCPAPIsExecJAVA() {
        return apiClient.getJavaReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_JAVA_THS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_JAVA_THS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable JAVA_THS", response = JCPStatus.JavaThread.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPStatus.JavaThread>> getJCPAPIsExecJAVA_THS() {
        return apiClient.getJavaThreadReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_MEMORY)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_MEMORY,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable MEMORY", response = JCPStatus.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Memory> getJCPAPIsExecMEMORY() {
        return apiClient.getMemoryReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_NETWORK)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_NETWORK,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable NETWORK", response = JCPStatus.Network.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Network> getJCPAPIsExecNETWORK() {
        return apiClient.getNetworkReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_NETWORK_INTFS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_NETWORK_INTFS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable NETWORK_INTFS", response = JCPStatus.NetworkIntf.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPStatus.NetworkIntf>> getJCPAPIsExecNETWORK_INTFS() {
        return apiClient.getNetworkIntfsReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_OS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_OS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable OS", response = JCPStatus.Os.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Os> getJCPAPIsExecOS() {
        return apiClient.getOsReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_PROCESS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_PROCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable PROCESS", response = JCPStatus.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Process> getJCPAPIsExecPROCESS() {
        return apiClient.getProcessReq();
    }


    // Methods JCP GWs

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_GWS_STATUS_INSTANCE)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_INSTANCE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GW's clients", response = GWsStatus.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<GWsStatus>> getJCPGWsInstanceReq() {
        List<GWsStatus> gws = new ArrayList<>();

        List<String> gwAPIsUrlsQueried = new ArrayList<>();
        for (GW gw : gwManager.getAll()) {
            String url = String.format("%s:%d", gw.getGwAPIsAddr(), gw.getGwAPIsPort());
            if (gwAPIsUrlsQueried.contains(url))
                continue;

            GWsClient apiGWs = gwManager.getGWsClient(gw);
            try {
                gws.add(apiGWs.getJCPGWsReq());
            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
                e.printStackTrace();
                continue;
            }

            gwAPIsUrlsQueried.add(url);
        }

        return ResponseEntity.ok(gws);
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_GWS_STATUS_SERVERS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_SERVERS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GW's info and stats", response = GWsStatus.Server.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<GWsStatus.Server>> getJCPGWsClientsReq() {
        List<GWsStatus.Server> gws = new ArrayList<>();

        List<String> gwAPIsUrlsQueried = new ArrayList<>();
        for (GW gw : gwManager.getAll()) {
            String url = String.format("%s:%d", gw.getGwAPIsAddr(), gw.getGwAPIsPort());
            if (gwAPIsUrlsQueried.contains(url))
                continue;

            GWsClient apiGWs = gwManager.getGWsClient(gw);
            try {
                List<GWsStatus.Server> gwsTmp = apiGWs.getJCPAPIsStatusGWsCliReq();
                gws.addAll(gwsTmp);
            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
                e.printStackTrace();
                continue;
            }

            gwAPIsUrlsQueried.add(url);
        }

        return ResponseEntity.ok(gws);
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_ONLINE)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_ONLINE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable ONLINE", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<String> getJCPGWsExecONLINE() {
        return apiClient.getOnlineReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_CPU)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_CPU,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable CPU", response = JCPStatus.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.CPU> getJCPGWsExecCPU() {
        return apiClient.getCPUReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_DISKS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_DISKS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable DISKS", response = JCPStatus.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Disks> getJCPGWsExecDISKS() {
        return apiClient.getDisksReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_JAVA)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_JAVA,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable JAVA", response = JCPStatus.Java.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Java> getJCPGWsExecJAVA() {
        return apiClient.getJavaReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_JAVA_THS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_JAVA_THS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable JAVA_THS", response = JCPStatus.JavaThread.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPStatus.JavaThread>> getJCPGWsExecJAVA_THS() {
        return apiClient.getJavaThreadReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_MEMORY)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_MEMORY,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable MEMORY", response = JCPStatus.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Memory> getJCPGWsExecMEMORY() {
        return apiClient.getMemoryReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_NETWORK)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_NETWORK,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable NETWORK", response = JCPStatus.Network.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Network> getJCPGWsExecNETWORK() {
        return apiClient.getNetworkReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_NETWORK_INTFS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_NETWORK_INTFS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable NETWORK_INTFS", response = JCPStatus.NetworkIntf.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPStatus.NetworkIntf>> getJCPGWsExecNETWORK_INTFS() {
        return apiClient.getNetworkIntfsReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_OS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_OS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable OS", response = JCPStatus.Os.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Os> getJCPGWsExecOS() {
        return apiClient.getOsReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_PROCESS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_PROCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable PROCESS", response = JCPStatus.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Process> getJCPGWsExecPROCESS() {
        return apiClient.getProcessReq();
    }


    // Methods JCP JSL WB

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_INSTANCE)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_INSTANCE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL WebBridge's status", response = JSLWebBridgeStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JSLWebBridgeStatus> getJCPJSLWBInstanceReq() {
        try {
            return ResponseEntity.ok(wbClient.getJCPJSLWebBridgeReq());
        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error occurred with query 'JCP JSL WebBridge's status' resource because %s", e.getMessage()), e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_ONLINE)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_ONLINE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable ONLINE", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<String> getJCPJSLWBExecONLINE() {
        return apiClient.getOnlineReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_CPU)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_CPU,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable CPU", response = JCPStatus.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.CPU> getJCPJSLWBExecCPU() {
        return apiClient.getCPUReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_DISKS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_DISKS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable DISKS", response = JCPStatus.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Disks> getJCPJSLWBExecDISKS() {
        return apiClient.getDisksReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_JAVA)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_JAVA,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable JAVA", response = JCPStatus.Java.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Java> getJCPJSLWBExecJAVA() {
        return apiClient.getJavaReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_JAVA_THS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_JAVA_THS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable JAVA_THS", response = JCPStatus.JavaThread.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPStatus.JavaThread>> getJCPJSLWBExecJAVA_THS() {
        return apiClient.getJavaThreadReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_MEMORY)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_MEMORY,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable MEMORY", response = JCPStatus.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Memory> getJCPJSLWBExecMEMORY() {
        return apiClient.getMemoryReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_NETWORK)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_NETWORK,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable NETWORK", response = JCPStatus.Network.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Network> getJCPJSLWBExecNETWORK() {
        return apiClient.getNetworkReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_NETWORK_INTFS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_NETWORK_INTFS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable NETWORK_INTFS", response = JCPStatus.NetworkIntf.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPStatus.NetworkIntf>> getJCPJSLWBExecNETWORK_INTFS() {
        return apiClient.getNetworkIntfsReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_OS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_OS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable OS", response = JCPStatus.Os.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Os> getJCPJSLWBExecOS() {
        return apiClient.getOsReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_PROCESS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_PROCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable PROCESS", response = JCPStatus.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Process> getJCPJSLWBExecPROCESS() {
        return apiClient.getProcessReq();
    }


    // Methods JCP FE

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_FE_STATUS_INSTANCE)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_FE_STATUS_INSTANCE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP FrontEnd's status", response = FEStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<FEStatus> getJCPFEInstanceReq() {
        try {
            return ResponseEntity.ok(feClient.getJCPFEStatusReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error occurred with query 'JCP FE's status' resource because %s", e.getMessage()), e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_ONLINE)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_ONLINE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable ONLINE", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<String> getJCPFEExecONLINE() {
        return apiClient.getOnlineReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_CPU)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_CPU,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable CPU", response = JCPStatus.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.CPU> getJCPFEExecCPU() {
        return apiClient.getCPUReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_DISKS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_DISKS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable DISKS", response = JCPStatus.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Disks> getJCPFEExecDISKS() {
        return apiClient.getDisksReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_JAVA)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_JAVA,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable JAVA", response = JCPStatus.Java.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Java> getJCPFEExecJAVA() {
        return apiClient.getJavaReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_JAVA_THS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_JAVA_THS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable JAVA_THS", response = JCPStatus.JavaThread.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPStatus.JavaThread>> getJCPFEExecJAVA_THS() {
        return apiClient.getJavaThreadReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_MEMORY)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_MEMORY,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable MEMORY", response = JCPStatus.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Memory> getJCPFEExecMEMORY() {
        return apiClient.getMemoryReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_NETWORK)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_NETWORK,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable NETWORK", response = JCPStatus.Network.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Network> getJCPFEExecNETWORK() {
        return apiClient.getNetworkReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_NETWORK_INTFS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_NETWORK_INTFS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable NETWORK_INTFS", response = JCPStatus.NetworkIntf.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPStatus.NetworkIntf>> getJCPFEExecNETWORK_INTFS() {
        return apiClient.getNetworkIntfsReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_OS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_OS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable OS", response = JCPStatus.Os.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Os> getJCPFEExecOS() {
        return apiClient.getOsReq();
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_PROCESS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_PROCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable PROCESS", response = JCPStatus.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Process> getJCPFEExecPROCESS() {
        return apiClient.getProcessReq();
    }

}

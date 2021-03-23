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

    private JCPClientsMngr clientsMngr;
    @Autowired
    private JCPAPIsStatusController apiClient;
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
    public ResponseEntity<ServiceStatus> getJCPAPIsInstanceReq() {
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
    public ResponseEntity<List<ServiceStatus>> getJCPGWsInstanceReq() {
        List<ServiceStatus> gws = new ArrayList<>();

        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            try {
                gws.add(new GWsClient(gwClient).getStatusReq());

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException ignore) {
            }
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

        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            try {
                gws.addAll(new GWsClient(gwClient).getJCPAPIsStatusGWsCliReq());

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException ignore) {
            }
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
    public ResponseEntity<List<String>> getJCPGWsExecONLINE() {
        List<String> gws = new ArrayList<>();

        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            try {
                gws.add(new GWsClient(gwClient).getStatusOnlineReq());

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException ignore) {
            }
        }

        return ResponseEntity.ok(gws);
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
    public ResponseEntity<List<JCPStatus.CPU>> getJCPGWsExecCPU() {
        List<JCPStatus.CPU> gws = new ArrayList<>();

        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            try {
                gws.add(new GWsClient(gwClient).getStatusCpuReq());

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException ignore) {
            }
        }

        return ResponseEntity.ok(gws);
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
    public ResponseEntity<List<JCPStatus.Disks>> getJCPGWsExecDISKS() {
        List<JCPStatus.Disks> gws = new ArrayList<>();

        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            try {
                gws.add(new GWsClient(gwClient).getStatusDisksReq());

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException ignore) {
            }
        }

        return ResponseEntity.ok(gws);
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
    public ResponseEntity<List<JCPStatus.Java>> getJCPGWsExecJAVA() {
        List<JCPStatus.Java> gws = new ArrayList<>();

        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            try {
                gws.add(new GWsClient(gwClient).getStatusJavaReq());

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException ignore) {
            }
        }

        return ResponseEntity.ok(gws);
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
    public ResponseEntity<List<List<JCPStatus.JavaThread>>> getJCPGWsExecJAVA_THS() {
        List<List<JCPStatus.JavaThread>> gws = new ArrayList<>();

        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            try {
                gws.add(new GWsClient(gwClient).getStatusJavaThsReq());

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException ignore) {
            }
        }

        return ResponseEntity.ok(gws);
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
    public ResponseEntity<List<JCPStatus.Memory>> getJCPGWsExecMEMORY() {
        List<JCPStatus.Memory> gws = new ArrayList<>();

        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            try {
                gws.add(new GWsClient(gwClient).getStatusMemoryReq());

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException ignore) {
            }
        }

        return ResponseEntity.ok(gws);
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
    public ResponseEntity<List<JCPStatus.Network>> getJCPGWsExecNETWORK() {
        List<JCPStatus.Network> gws = new ArrayList<>();

        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            try {
                gws.add(new GWsClient(gwClient).getStatusNetworkReq());

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException ignore) {
            }
        }

        return ResponseEntity.ok(gws);
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
    public ResponseEntity<List<List<JCPStatus.NetworkIntf>>> getJCPGWsExecNETWORK_INTFS() {
        List<List<JCPStatus.NetworkIntf>> gws = new ArrayList<>();

        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            try {
                gws.add(new GWsClient(gwClient).getStatusNetworkIntfsReq());

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException ignore) {
            }
        }

        return ResponseEntity.ok(gws);
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
    public ResponseEntity<List<JCPStatus.Os>> getJCPGWsExecOS() {
        List<JCPStatus.Os> gws = new ArrayList<>();

        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            try {
                gws.add(new GWsClient(gwClient).getStatusOSReq());

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException ignore) {
            }
        }

        return ResponseEntity.ok(gws);
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
    public ResponseEntity<List<JCPStatus.Process>> getJCPGWsExecPROCESS() {
        List<JCPStatus.Process> gws = new ArrayList<>();

        for (JCPGWsClient gwClient : clientsMngr.getGWsClientsAll().values()) {
            try {
                gws.add(new GWsClient(gwClient).getStatusProcessReq());

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException ignore) {
            }
        }

        return ResponseEntity.ok(gws);
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
    public ResponseEntity<ServiceStatus> getJCPJSLWBInstanceReq() {
        try {
            return ResponseEntity.ok(wbClient.getStatusReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("JSL WebBridge", "Instance", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_ONLINE)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_ONLINE,
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
        try {
            return ResponseEntity.ok(wbClient.getStatusOnlineReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("JSL WebBridge", "Online", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_CPU)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_CPU,
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
        try {
            return ResponseEntity.ok(wbClient.getStatusCpuReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("JSL WebBridge", "CPU", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_DISKS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_DISKS,
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
        try {
            return ResponseEntity.ok(wbClient.getStatusDisksReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("JSL WebBridge", "Disks", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_JAVA)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_JAVA,
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
        try {
            return ResponseEntity.ok(wbClient.getStatusJavaReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("JSL WebBridge", "Java", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_JAVA_THS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_JAVA_THS,
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
        try {
            return ResponseEntity.ok(wbClient.getStatusJavaThsReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("JSL WebBridge", "Java Ths", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_MEMORY)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_MEMORY,
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
        try {
            return ResponseEntity.ok(wbClient.getStatusMemoryReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("JSL WebBridge", "Memory", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_NETWORK)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_NETWORK,
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
        try {
            return ResponseEntity.ok(wbClient.getStatusNetworkReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("JSL WebBridge", "Network", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_NETWORK_INTFS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_NETWORK_INTFS,
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
        try {
            return ResponseEntity.ok(wbClient.getStatusNetworkIntfsReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("JSL WebBridge", "Network Intfs", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_OS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_OS,
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
        try {
            return ResponseEntity.ok(wbClient.getStatusOSReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("JSL WebBridge", "OS", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_PROCESS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_PROCESS,
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
        try {
            return ResponseEntity.ok(wbClient.getStatusProcessReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("JSL WebBridge", "Process", e);
        }
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
    public ResponseEntity<ServiceStatus> getJCPFEInstanceReq() {
        try {
            return ResponseEntity.ok(feClient.getStatusReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("Front End", "Instance", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_ONLINE)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_ONLINE,
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
        try {
            return ResponseEntity.ok(feClient.getStatusOnlineReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("Front End", "Online", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_CPU)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_CPU,
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
        try {
            return ResponseEntity.ok(feClient.getStatusCpuReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("Front End", "Online", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_DISKS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_DISKS,
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
        try {
            return ResponseEntity.ok(feClient.getStatusDisksReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("Front End", "Disks", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_JAVA)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_JAVA,
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
        try {
            return ResponseEntity.ok(feClient.getStatusJavaReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("Front End", "Java", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_JAVA_THS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_JAVA_THS,
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
        try {
            return ResponseEntity.ok(feClient.getStatusJavaThsReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("Front End", "Java Ths", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_MEMORY)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_MEMORY,
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
        try {
            return ResponseEntity.ok(feClient.getStatusMemoryReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("Front End", "Memory", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_NETWORK)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_NETWORK,
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
        try {
            return ResponseEntity.ok(feClient.getStatusNetworkReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("Front End", "Network", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_NETWORK_INTFS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_NETWORK_INTFS,
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
        try {
            return ResponseEntity.ok(feClient.getStatusNetworkIntfsReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("Front End", "Network Intfs", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_OS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_OS,
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
        try {
            return ResponseEntity.ok(feClient.getStatusOSReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("Front End", "OS", e);
        }
    }

    @GetMapping(path = APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_PROCESS)
    @ApiOperation(value = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_PROCESS,
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
        try {
            return ResponseEntity.ok(feClient.getStatusProcessReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpNotConnectedException("Front End", "Process", e);
        }
    }


    // Exception utils

    protected ResponseStatusException jcpNotConnectedException(String service, String resource, Throwable e) {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, String.format("Error occurred with query 'JCP %s's %s' resource because %s", service, resource, e.getMessage()), e);
    }

}

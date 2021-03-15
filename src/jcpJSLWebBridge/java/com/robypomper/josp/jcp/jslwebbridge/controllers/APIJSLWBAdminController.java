package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBAdmin;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.admin.JSLAdmin;
import com.robypomper.josp.params.jcp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpSession;
import java.util.List;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBAdmin.SubGroupAdmin.NAME})
public class APIJSLWBAdminController extends APIJSLWBControllerAbs {


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(APIJSLWBAdminController.class);
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private JSLWebBridgeService webBridgeService;
    @Autowired
    private ClientParams params;
    @Value("${jcp.urlAPIs}")
    private String urlAPIs;


    // Constructors

    public APIJSLWBAdminController() {
        super(APIJSLWBAdmin.API_NAME, APIJSLWBAdmin.API_VER, JCPJSLWBVersions.API_NAME, APIJSLWBAdmin.SubGroupAdmin.NAME, APIJSLWBAdmin.SubGroupAdmin.DESCR);
    }


    // Swagger configs

    @Bean
    public Docket swaggerConfig_APIJSLWBAdmin() {
        return swaggerConfig();
    }


    // APIs forwards for JCP APIs

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's info and stats", response = APIsStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<APIsStatus> getJCPAPIsStatusForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsStatus());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs Status", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs Status", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_OBJS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_OBJS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered JOD Objects", response = APIsStatus.Objects.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<APIsStatus.Objects> getJCPAPIsStatusObjsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsObjects());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs Objects", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs Objects", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_SRVS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_SRVS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered JSL Services", response = APIsStatus.Services.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<APIsStatus.Services> getJCPAPIsStatusSrvsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsServices());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs Services", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs Services", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_USRS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_USRS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered Users", response = APIsStatus.Users.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<APIsStatus.Users> getJCPAPIsStatusUsrsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsUsers());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs Users", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs Users", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_EXEC_ONLINE)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_EXEC_ONLINE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable ONLINE", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<String> getJCPAPIsStatusExecONLINEForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecOnline());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable ONLINE", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable ONLINE", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_EXEC_CPU)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_EXEC_CPU)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable CPU", response = JCPStatus.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.CPU> getJCPAPIsStatusExecCPUForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecCPU());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable CPU", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable CPU", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_EXEC_DISKS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_EXEC_DISKS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable DISKS", response = JCPStatus.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Disks> getJCPAPIsStatusExecDisksForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecDisks());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable DISKS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable DISKS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_EXEC_JAVA)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_EXEC_JAVA)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable JAVA", response = JCPStatus.Java.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Java> getJCPAPIsStatusExecJavaForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecJava());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable JAVA", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable JAVA", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_EXEC_JAVA_THS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_EXEC_JAVA_THS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable JAVA_THS", response = JCPStatus.JavaThread.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.JavaThread>> getJCPAPIsStatusExecJavaThsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecJavaThs());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable JAVA_THS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable JAVA_THS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_EXEC_MEMORY)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_EXEC_MEMORY)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable MEMORY", response = JCPStatus.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Memory> getJCPAPIsStatusExecMemoryForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecMemory());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable MEMORY", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable MEMORY", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_EXEC_NETWORK)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_EXEC_NETWORK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable NETWORK", response = JCPStatus.Network.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Network> getJCPAPIsStatusExecNetworkForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecNetwork());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable NETWORK", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable NETWORK", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_EXEC_NETWORK_INTFS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_EXEC_NETWORK_INTFS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable NETWORK_INTFS", response = JCPStatus.NetworkIntf.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.NetworkIntf>> getJCPAPIsStatusExecNetworkIntfsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecNetworkIntfs());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable NETWORK_INTFS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable NETWORK_INTFS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_EXEC_OS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_EXEC_OS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable OS", response = JCPStatus.Os.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Os> getJCPAPIsStatusExecOsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecOs());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable OS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable OS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_APIS_EXEC_PROCESS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_APIS_EXEC_PROCESS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's executable PROCESS", response = JCPStatus.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Process> getJCPAPIsStatusExecProcessForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecProcess());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable PROCESS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable PROCESS", e);
        }
    }


    // APIs forwards for JCP GWs

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_GWS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_GWS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered JCP GWs", response = GWsStatus.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<GWsStatus>> getJCPGWsStatusForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGWsStatus());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP GWs Status", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP GWs Status", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_GWS_CLI)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_GWS_CLI)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered JCP GWs clients", response = GWsStatus.Server.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<GWsStatus.Server>> getJCPGWsStatusCliForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGWsClients());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP GWs Clients", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP GWs Clients", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_GWS_EXEC_ONLINE)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_GWS_EXEC_ONLINE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable ONLINE", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<String>> getJCPGWsStatusExecONLINEForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGWsExecOnline());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable ONLINE", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable ONLINE", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_GWS_EXEC_CPU)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_GWS_EXEC_CPU)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable CPU", response = JCPStatus.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.CPU>> getJCPGWsStatusExecCPUForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGWsExecCPU());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable CPU", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable CPU", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_GWS_EXEC_DISKS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_GWS_EXEC_DISKS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable DISKS", response = JCPStatus.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.Disks>> getJCPGWsStatusExecDisksForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGWsExecDisks());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable DISKS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable DISKS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_GWS_EXEC_JAVA)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_GWS_EXEC_JAVA)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable JAVA", response = JCPStatus.Java.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.Java>> getJCPGWsStatusExecJavaForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGWsExecJava());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable JAVA", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable JAVA", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_GWS_EXEC_JAVA_THS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_GWS_EXEC_JAVA_THS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable JAVA_THS", response = JCPStatus.JavaThread.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<List<JCPStatus.JavaThread>>> getJCPGWsStatusExecJavaThsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGWsExecJavaThs());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable JAVA_THS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable JAVA_THS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_GWS_EXEC_MEMORY)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_GWS_EXEC_MEMORY)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable MEMORY", response = JCPStatus.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.Memory>> getJCPGWsStatusExecMemoryForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGWsExecMemory());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable MEMORY", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable MEMORY", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_GWS_EXEC_NETWORK)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_GWS_EXEC_NETWORK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable NETWORK", response = JCPStatus.Network.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.Network>> getJCPGWsStatusExecNetworkForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGWsExecNetwork());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable NETWORK", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable NETWORK", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_GWS_EXEC_NETWORK_INTFS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_GWS_EXEC_NETWORK_INTFS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable NETWORK_INTFS", response = JCPStatus.NetworkIntf.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<List<JCPStatus.NetworkIntf>>> getJCPGWsStatusExecNetworkIntfsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGWsExecNetworkIntfs());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable NETWORK_INTFS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable NETWORK_INTFS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_GWS_EXEC_OS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_GWS_EXEC_OS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable OS", response = JCPStatus.Os.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.Os>> getJCPGWsStatusExecOsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGWsExecOs());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable OS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable OS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_GWS_EXEC_PROCESS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_GWS_EXEC_PROCESS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP GWs's executable PROCESS", response = JCPStatus.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.Process>> getJCPGWsStatusExecProcessForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGWsExecProcess());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable PROCESS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable PROCESS", e);
        }
    }


    // APIs forwards for JCP JSL Web Brdige

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_JSL_WB)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_JSL_WB)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge status", response = JSLWebBridgeStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JSLWebBridgeStatus> getJCPJSLWBStatusForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPJSLWebBridgeStatus());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP JSL WebBridge Status", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP JSL WebBridge Status", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_ONLINE)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_ONLINE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable ONLINE", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<String> getJCPJSLWBStatusExecONLINEForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPJSLWBExecOnline());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable ONLINE", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable ONLINE", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_CPU)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_CPU)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable CPU", response = JCPStatus.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.CPU> getJCPJSLWBStatusExecCPUForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPJSLWBExecCPU());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable CPU", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable CPU", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_DISKS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_DISKS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable DISKS", response = JCPStatus.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Disks> getJCPJSLWBStatusExecDisksForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPJSLWBExecDisks());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable DISKS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable DISKS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_JAVA)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_JAVA)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable JAVA", response = JCPStatus.Java.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Java> getJCPJSLWBStatusExecJavaForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPJSLWBExecJava());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable JAVA", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable JAVA", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_JAVA_THS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_JAVA_THS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable JAVA_THS", response = JCPStatus.JavaThread.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.JavaThread>> getJCPJSLWBStatusExecJavaThsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPJSLWBExecJavaThs());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable JAVA_THS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable JAVA_THS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_MEMORY)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_MEMORY)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable MEMORY", response = JCPStatus.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Memory> getJCPJSLWBStatusExecMemoryForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPJSLWBExecMemory());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable MEMORY", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable MEMORY", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_NETWORK)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_NETWORK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable NETWORK", response = JCPStatus.Network.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Network> getJCPJSLWBStatusExecNetworkForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPJSLWBExecNetwork());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable NETWORK", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable NETWORK", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_NETWORK_INTFS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_NETWORK_INTFS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable NETWORK_INTFS", response = JCPStatus.NetworkIntf.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.NetworkIntf>> getJCPJSLWBStatusExecNetworkIntfsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPJSLWBExecNetworkIntfs());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable NETWORK_INTFS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable NETWORK_INTFS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_OS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_OS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable OS", response = JCPStatus.Os.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Os> getJCPJSLWBStatusExecOsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPJSLWBExecOs());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable OS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable OS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_PROCESS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_PROCESS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's executable PROCESS", response = JCPStatus.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Process> getJCPJSLWBStatusExecProcessForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPJSLWBExecProcess());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable PROCESS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable PROCESS", e);
        }
    }


    // APIs forwards for JCP Front End

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_FE)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_FE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP FE status", response = FEStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<FEStatus> getJCPFEStatusForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPFEStatus());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP FE Status", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP FE Status", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_FE_EXEC_ONLINE)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_FE_EXEC_ONLINE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable ONLINE", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<String> getJCPFEStatusExecONLINEForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPFEExecOnline());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable ONLINE", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable ONLINE", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_FE_EXEC_CPU)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_FE_EXEC_CPU)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable CPU", response = JCPStatus.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.CPU> getJCPFEStatusExecCPUForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPFEExecCPU());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable CPU", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable CPU", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_FE_EXEC_DISKS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_FE_EXEC_DISKS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable DISKS", response = JCPStatus.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Disks> getJCPFEStatusExecDisksForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPFEExecDisks());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable DISKS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable DISKS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_FE_EXEC_JAVA)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_FE_EXEC_JAVA)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable JAVA", response = JCPStatus.Java.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Java> getJCPFEStatusExecJavaForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPFEExecJava());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable JAVA", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable JAVA", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_FE_EXEC_JAVA_THS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_FE_EXEC_JAVA_THS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable JAVA_THS", response = JCPStatus.JavaThread.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.JavaThread>> getJCPFEStatusExecJavaThsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPFEExecJavaThs());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable JAVA_THS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable JAVA_THS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_FE_EXEC_MEMORY)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_FE_EXEC_MEMORY)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable MEMORY", response = JCPStatus.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Memory> getJCPFEStatusExecMemoryForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPFEExecMemory());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable MEMORY", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable MEMORY", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_FE_EXEC_NETWORK)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_FE_EXEC_NETWORK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable NETWORK", response = JCPStatus.Network.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Network> getJCPFEStatusExecNetworkForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPFEExecNetwork());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable NETWORK", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable NETWORK", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_FE_EXEC_NETWORK_INTFS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_FE_EXEC_NETWORK_INTFS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable NETWORK_INTFS", response = JCPStatus.NetworkIntf.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPStatus.NetworkIntf>> getJCPFEStatusExecNetworkIntfsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPFEExecNetworkIntfs());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable NETWORK_INTFS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable NETWORK_INTFS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_FE_EXEC_OS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_FE_EXEC_OS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable OS", response = JCPStatus.Os.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Os> getJCPFEStatusExecOsForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPFEExecOs());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable OS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable OS", e);
        }
    }

    @GetMapping(path = APIJSLWBAdmin.FULL_PATH_JSLWB_STATUS_FE_EXEC_PROCESS)
    @ApiOperation(value = APIJSLWBAdmin.DESCR_PATH_JSLWB_STATUS_FE_EXEC_PROCESS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Front End's executable PROCESS", response = JCPStatus.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPStatus.Process> getJCPFEStatusExecProcessForward(@ApiIgnore HttpSession session) {
        JSL jsl = webBridgeService.getJSL(session.getId());

        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPFEExecProcess());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpClientException("JCP APIs executable PROCESS", e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException("JCP APIs executable PROCESS", e);
        }
    }


    // Utils

    private void checkAdmin(JSL jsl) {
        if (!jsl.getUserMngr().isUserAuthenticated())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED /* 401 */, "User not authenticated");

        if (!jsl.getUserMngr().isAdmin())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN /* 403 */, "Only Admin user can access to this request");
    }


    // Exception utils

    private ResponseStatusException jcpClientException(String request, Throwable e) {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE /* 503 */, String.format("Error occurred with '%s' resource because %s", request, e.getMessage()), e);
    }

    private ResponseStatusException userNotAuthorizedException(String request, Throwable e) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN /* 503 */, String.format("Forbidden access for current user to '%s' resource because %s", request, e.getMessage()), e);
    }

}

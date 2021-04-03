package com.robypomper.josp.jcp.apis.controllers.josp.admin.jslwebbridge.executable;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.callers.base.status.executable.Caller20;
import com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20;
import com.robypomper.josp.defs.admin.jslwebbridge.executable.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerLink;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.clients.JCPJSLWebBridgeClient;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.Date;


/**
 * JOSP Admin - JSL Web Bridge / Executable 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLink {

    // Internal vars

    @Autowired
    private JCPClientsMngr clientsMngr;


    // Index methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's executable index", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Params20.Index> getIndex() {
        return ResponseEntity.ok(new Params20.Index());
    }


    // Online methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_ONLINE)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_ONLINE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's local date", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Date> getOnlineReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getOnlineReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }


    // Process methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_PROCESS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_PROCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's process info and stats", response = Params20.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Process> getProcessReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getProcessReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }


    // Java methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_JAVA)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_JAVA,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's executable java index", response = Params20.JavaIndex.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaIndex> getJavaIndex() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getJavaIndex());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_JAVA_VM)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_JAVA_VM,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaVM.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaVM> getJavaVMReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getJavaVMReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_JAVA_RUNTIME)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_JAVA_RUNTIME,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaRuntime.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaRuntime> getJavaRuntimeReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getJavaRuntimeReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_JAVA_TIMES)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_JAVA_TIMES,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaTimes.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaTimes> getJavaTimesReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getJavaTimesReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_JAVA_CLASSES)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_JAVA_CLASSES,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaClasses.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaClasses> getJavaClassesReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getJavaClassesReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_JAVA_MEMORY)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_JAVA_MEMORY,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaMemory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaMemory> getJavaMemoryReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getJavaMemoryReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_JAVA_THREADS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_JAVA_THREADS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaThreads.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaThreads> getJavaThreadsReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getJavaThreadsReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_JAVA_THREAD)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_JAVA_THREAD,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaThread.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaThread> getJavaThreadReq(@PathVariable(Paths20.PARAM_THREAD) long threadId) {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getJavaThreadReq(threadId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }


    // OS methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_OS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_OS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's operating system info and stats", response = Params20.OS.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.OS> getOSReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getOSReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }


    // CPU methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_CPU)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_CPU,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's CPU info and stats", response = Params20.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.CPU> getCPUReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getCPUReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }


    // Memory methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_MEMORY)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_MEMORY,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's memory info and stats", response = Params20.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Memory> getMemoryReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getMemoryReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }


    // Disks methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_DISKS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_DISKS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's disks info and stats", response = Params20.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Disks> getDisksReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getDisksReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_DISK)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_DISK,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's disks info and stats", response = Params20.Disk.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Disk> getDiskReq(@PathVariable(Paths20.PARAM_DISK) String diskId) {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getDiskReq(diskId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }


    // Networks methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_NETWORKS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_NETWORKS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's network info and stats", response = Params20.Networks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Networks> getNetworksReq() {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getNetworksReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_JSLWB_EXEC_NETWORK)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_JSLWB_EXEC_NETWORK,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's network's interfaces info and stats", response = Params20.Network.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Network> getNetworkReq(@PathVariable(Paths20.PARAM_NTWK) int networkId) {
        JCPJSLWebBridgeClient client = clientsMngr.getJCPJSLWebBridgeClient();
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getNetworkReq(networkId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

}
package com.robypomper.josp.jcp.apis.controllers.josp.admin.apis.executable;

import com.robypomper.josp.defs.admin.apis.executable.Params20;
import com.robypomper.josp.defs.admin.apis.executable.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerLink;
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
 * JOSP Admin - APIs / Executable 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLink {


    // Internal vars

    @Autowired
    private com.robypomper.josp.jcp.base.controllers.internal.status.executable.Controller20 apiClient;


    // Index methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's executable index", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Params20.Index> getIndex() {
        return ResponseEntity.ok(new Params20.Index());
    }


    // Online methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_ONLINE)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_ONLINE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's local date", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Date> getOnlineReq() {
        return apiClient.getOnlineReq();
    }


    // Process methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_PROCESS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_PROCESS,
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
        return apiClient.getProcessReq();
    }


    // Java methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_JAVA)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_JAVA,
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
        return ResponseEntity.ok(new Params20.JavaIndex());
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_JAVA_VM)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_JAVA_VM,
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
        return apiClient.getJavaVMReq();
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_JAVA_RUNTIME)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_JAVA_RUNTIME,
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
        return apiClient.getJavaRuntimeReq();
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_JAVA_TIMES)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_JAVA_TIMES,
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
        return apiClient.getJavaTimesReq();
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_JAVA_CLASSES)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_JAVA_CLASSES,
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
        return apiClient.getJavaClassesReq();
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_JAVA_MEMORY)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_JAVA_MEMORY,
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
        return apiClient.getJavaMemoryReq();
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_JAVA_THREADS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_JAVA_THREADS,
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
        return apiClient.getJavaThreadsReq();
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_JAVA_THREAD)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_JAVA_THREAD,
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
        return apiClient.getJavaThreadReq(threadId);
    }


    // OS methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_OS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_OS,
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
        return apiClient.getOsReq();
    }


    // CPU methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_CPU)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_CPU,
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
        return apiClient.getCPUReq();
    }


    // Memory methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_MEMORY)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_MEMORY,
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
        return apiClient.getMemoryReq();
    }


    // Disks methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_DISKS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_DISKS,
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
        return apiClient.getDisksReq();
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_DISK)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_DISK,
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
        return apiClient.getDiskReq(diskId);
    }


    // Networks methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_NETWORKS)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_NETWORKS,
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
        return apiClient.getNetworksReq();
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_APIS_EXEC_NETWORK)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_APIS_EXEC_NETWORK,
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
        return apiClient.getNetworkIntfsReq(networkId);
    }

}

package com.robypomper.josp.jcp.service.controllers.jcp;

import com.robypomper.java.JavaDate;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.JCPStatus;
import com.robypomper.josp.params.jcp.ServiceStatus;
import com.robypomper.josp.paths.jcp.JCPStatusAbs;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class JCPStatusControllerAbs<S extends ServiceStatus> {

    // Methods

    @GetMapping(path = JCPStatusAbs.FULL_PATH_STATUS_INSTANCE)
    @ApiOperation(value = JCPStatusAbs.DESCR_PATH_STATUS_INSTANCE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's instance status", response = ServiceStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<S> getInstanceReq() {
        return ResponseEntity.ok(getInstanceReqSubClass());
    }

    protected abstract S getInstanceReqSubClass();

    @GetMapping(path = JCPStatusAbs.FULL_PATH_STATUS_ONLINE)
    @ApiOperation(value = JCPStatusAbs.DESCR_PATH_STATUS_ONLINE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's local date", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<String> getOnlineReq() {
        return ResponseEntity.ok(JavaDate.getNow());
    }

    @GetMapping(path = JCPStatusAbs.FULL_PATH_STATUS_PROCESS)
    @ApiOperation(value = JCPStatusAbs.DESCR_PATH_STATUS_PROCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's process info and stats", response = JCPStatus.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Process> getProcessReq() {
        return ResponseEntity.ok(new JCPStatus.Process());
    }

    @GetMapping(path = JCPStatusAbs.FULL_PATH_STATUS_JAVA)
    @ApiOperation(value = JCPStatusAbs.DESCR_PATH_STATUS_JAVA,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = JCPStatus.Java.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Java> getJavaReq() {
        return ResponseEntity.ok(new JCPStatus.Java());
    }

    @GetMapping(path = JCPStatusAbs.FULL_PATH_STATUS_JAVA_THS)
    @ApiOperation(value = JCPStatusAbs.DESCR_PATH_STATUS_JAVA_THS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java threads info and stats", response = JCPStatus.JavaThread.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPStatus.JavaThread>> getJavaThreadReq() {
        List<JCPStatus.JavaThread> threads = new ArrayList<>();
        for (long thId : ManagementFactory.getThreadMXBean().getAllThreadIds())
            threads.add(new JCPStatus.JavaThread(thId));

        return ResponseEntity.ok(threads);
    }

    @GetMapping(path = JCPStatusAbs.FULL_PATH_STATUS_OS)
    @ApiOperation(value = JCPStatusAbs.DESCR_PATH_STATUS_OS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's operating system info and stats", response = JCPStatus.Os.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Os> getOsReq() {
        return ResponseEntity.ok(new JCPStatus.Os());
    }

    @GetMapping(path = JCPStatusAbs.FULL_PATH_STATUS_CPU)
    @ApiOperation(value = JCPStatusAbs.DESCR_PATH_STATUS_CPU,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's CPU info and stats", response = JCPStatus.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.CPU> getCPUReq() {
        return ResponseEntity.ok(new JCPStatus.CPU());
    }

    @GetMapping(path = JCPStatusAbs.FULL_PATH_STATUS_MEMORY)
    @ApiOperation(value = JCPStatusAbs.DESCR_PATH_STATUS_MEMORY,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's memory info and stats", response = JCPStatus.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Memory> getMemoryReq() {
        return ResponseEntity.ok(new JCPStatus.Memory());
    }

    @GetMapping(path = JCPStatusAbs.FULL_PATH_STATUS_DISKS)
    @ApiOperation(value = JCPStatusAbs.DESCR_PATH_STATUS_DISKS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's disks info and stats", response = JCPStatus.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Disks> getDisksReq() {
        return ResponseEntity.ok(new JCPStatus.Disks());
    }

    @GetMapping(path = JCPStatusAbs.FULL_PATH_STATUS_NETWORK)
    @ApiOperation(value = JCPStatusAbs.DESCR_PATH_STATUS_NETWORK,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's network info and stats", response = JCPStatus.Network.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPStatus.Network> getNetworkReq() {
        return ResponseEntity.ok(new JCPStatus.Network());
    }

    @GetMapping(path = JCPStatusAbs.FULL_PATH_STATUS_NETWORK_INTFS)
    @ApiOperation(value = JCPStatusAbs.DESCR_PATH_STATUS_NETWORK_INTFS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's network's interfaces info and stats", response = JCPStatus.Network.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<JCPStatus.NetworkIntf>> getNetworkIntfsReq() {
        List<JCPStatus.NetworkIntf> listInterfaces = new ArrayList<>();
        List<NetworkInterface> itfs;
        try {
            itfs = Collections.list(NetworkInterface.getNetworkInterfaces());

        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            for (NetworkInterface itf : itfs)
                listInterfaces.add(new JCPStatus.NetworkIntf(itf));

        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(listInterfaces);
    }

}

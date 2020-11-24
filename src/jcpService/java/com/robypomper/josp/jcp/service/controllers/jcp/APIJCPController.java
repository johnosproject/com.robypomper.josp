package com.robypomper.josp.jcp.service.controllers.jcp;

import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.JCPStatus;
import com.robypomper.josp.paths.APIJCP;
import com.robypomper.josp.protocol.JOSPProtocol;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = {APIJCP.SubGroupStatus.NAME})
public class APIJCPController {

    // Internal vars

    @Autowired
    private HttpSession httpSession;


    // Controller's Methods

    @GetMapping(path = APIJCP.FULL_PATH_STATUS)
    @ApiOperation(value = "Return ONLINE if the service is up")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's local date", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<String> getStateReq() {
        return ResponseEntity.ok(JOSPProtocol.getNow());
    }

    @GetMapping(path = APIJCP.FULL_PATH_STATUS_PROCESS)
    @ApiOperation(value = "Return process info and stats",
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

    @GetMapping(path = APIJCP.FULL_PATH_STATUS_JAVA)
    @ApiOperation(value = "Return java vm + process info and stats",
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

    @GetMapping(path = APIJCP.FULL_PATH_STATUS_JAVA_THREADS)
    @ApiOperation(value = "Return java threads info",
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
        ;

        return ResponseEntity.ok(threads);
    }

    @GetMapping(path = APIJCP.FULL_PATH_STATUS_OS)
    @ApiOperation(value = "Return operating system info and stats",
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

    @GetMapping(path = APIJCP.FULL_PATH_STATUS_CPU)
    @ApiOperation(value = "Return CPU info and stats",
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

    @GetMapping(path = APIJCP.FULL_PATH_STATUS_MEMORY)
    @ApiOperation(value = "Return memory info and stats",
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

    @GetMapping(path = APIJCP.FULL_PATH_STATUS_DISKS)
    @ApiOperation(value = "Return disks info and stats",
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

    @GetMapping(path = APIJCP.FULL_PATH_STATUS_NETWORK)
    @ApiOperation(value = "Return networks's info and stats",
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

    @GetMapping(path = APIJCP.FULL_PATH_STATUS_NETWORK_INTFS)
    @ApiOperation(value = "Return network's interfaces info and stats",
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

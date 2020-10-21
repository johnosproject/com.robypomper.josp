package com.robypomper.josp.jcp.apis.jcp;

import com.robypomper.cloud.apis.CloudStatusControllerBase;
import com.robypomper.cloud.params.CloudStatus;
import com.robypomper.josp.jcp.apis.params.admin.JCPCloudStatus;
import com.robypomper.josp.jcp.apis.paths.APICloudStatus;
import com.robypomper.josp.jcp.docs.SwaggerConfigurer;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Api(tags = {APICloudStatus.SubGroupStatus.NAME})
public class CloudStatusController extends CloudStatusControllerBase {

    // Internal vars

    @Autowired
    private HttpSession httpSession;

    @GetMapping(path = APICloudStatus.FULL_PATH_STATE_PROCESS)
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
            @ApiResponse(code = 200, message = "JCP API's process info and stats", response = CloudStatus.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<CloudStatus.Process> getProcessReq() {
        return ResponseEntity.ok(getProcess());
    }

    @GetMapping(path = APICloudStatus.FULL_PATH_STATE_JAVA)
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
            @ApiResponse(code = 200, message = "JCP API's java vm and process info and stats", response = CloudStatus.Java.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<CloudStatus.Java> getJavaReq() {
        return ResponseEntity.ok(getJava());
    }

    @GetMapping(path = APICloudStatus.FULL_PATH_STATE_JAVA_THREADS)
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
            @ApiResponse(code = 200, message = "JCP API's java threads info and stats", response = CloudStatus.JavaThread.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<CloudStatus.JavaThread>> getJavaThreadReq() {
        return ResponseEntity.ok(getJavaThreads());
    }

    @GetMapping(path = APICloudStatus.FULL_PATH_STATE_OS)
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
            @ApiResponse(code = 200, message = "JCP API's operating system info and stats", response = CloudStatus.Os.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<CloudStatus.Os> getOsReq() {
        return ResponseEntity.ok(getOs());
    }

    @GetMapping(path = APICloudStatus.FULL_PATH_STATE_CPU)
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
            @ApiResponse(code = 200, message = "JCP API's CPU info and stats", response = CloudStatus.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<CloudStatus.CPU> getCPUReq() {
        return ResponseEntity.ok(getCPU());
    }

    @GetMapping(path = APICloudStatus.FULL_PATH_STATE_MEMORY)
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
            @ApiResponse(code = 200, message = "JCP API's memory info and stats", response = CloudStatus.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<CloudStatus.Memory> getMemoryReq() {
        return ResponseEntity.ok(getMemory());
    }

    @GetMapping(path = APICloudStatus.FULL_PATH_STATE_DISKS)
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
            @ApiResponse(code = 200, message = "JCP API's disks info and stats", response = CloudStatus.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<CloudStatus.Disks> getDisksReq() {
        return ResponseEntity.ok(getDisks());
    }

    @GetMapping(path = APICloudStatus.FULL_PATH_STATE_NETWORK)
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
            @ApiResponse(code = 200, message = "JCP API's network info and stats", response = CloudStatus.Network.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<CloudStatus.Network> getNetworkReq() {
        return ResponseEntity.ok(getNetwork());
    }


    @GetMapping(path = APICloudStatus.FULL_PATH_STATE_NETWORK_INTFS)
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
            @ApiResponse(code = 200, message = "JCP API's network's interfaces info and stats", response = CloudStatus.Network.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<List<CloudStatus.NetworkIntf>> getNetworkIntfsReq() {
        return ResponseEntity.ok(getNetworkIntfs());
    }

    @GetMapping(path = APICloudStatus.FULL_PATH_STATE_JCPAPIS)
    @ApiOperation(value = "Return JCP APIs info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's info and stats", response = JCPCloudStatus.JCPAPI.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPCloudStatus.JCPAPI> getJCPAPIsReq() {
        JCPCloudStatus.JCPAPI jcpStatus = new JCPCloudStatus.JCPAPI();

        // ...

        return ResponseEntity.ok(jcpStatus);
    }

}

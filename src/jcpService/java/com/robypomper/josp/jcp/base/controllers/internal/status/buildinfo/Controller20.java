package com.robypomper.josp.jcp.base.controllers.internal.status.buildinfo;

import com.robypomper.josp.jcp.base.controllers.ControllerImplAbs;
import com.robypomper.josp.jcp.defs.base.internal.status.buildinfo.Params20;
import com.robypomper.josp.jcp.defs.base.internal.status.buildinfo.Paths20;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;


/**
 * JCP All - Status / Build Info 2.0
 * <p>
 * This controller is not annotated as @RestController and @APi because it must
 * be inherited by a class that can access to the right {@link com.robypomper.BuildInfo}
 * instances.
 * <p>
 * Example:
 * <code>
 * static Params20.BuildInfo current = (Params20.BuildInfo)BuildInfoJcpAPIs.current
 * </code>
 */
//@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
//@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public abstract class Controller20 extends ControllerImplAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);


    // Methods

    @GetMapping(path = Paths20.FULL_PATH_BUILDINFO)
    @ApiOperation(value = Paths20.DESCR_PATH_BUILDINFO,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's instance status", response = Params20.BuildInfo.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.BuildInfo> getBuildInfoReq() {
        return ResponseEntity.ok(getInstanceReqSubClass());
    }

    protected abstract Params20.BuildInfo getInstanceReqSubClass();

}
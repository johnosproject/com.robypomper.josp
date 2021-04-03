package com.robypomper.josp.jcp.jslwebbridge.controllers.internal.status;

import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.defs.jslwebbridge.internal.status.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.internal.status.Paths20;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.types.RESTItemList;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;


/**
 * JCP JSL Web Bridge - Status 2.0
 */
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImpl {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private JSLWebBridgeService jslWB;


    // Index methods

    @GetMapping(path = Paths20.FULL_PATH_STATUS)
    @ApiOperation(value = Paths20.FULL_PATH_STATUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's status index", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Params20.Index> getIndex() {
        return ResponseEntity.ok(new Params20.Index());
    }


    // Sessions methods

    @GetMapping(path = Paths20.FULL_PATH_STATUS_SESSIONS)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_SESSIONS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web bridge's web sessions list", response = Params20.Sessions.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Sessions> getSessionsReq() {
        Params20.Sessions sessions = new Params20.Sessions();

        sessions.sessionsList = new ArrayList<>();
        for (HttpSession session : jslWB.getAllSessions()) {
            RESTItemList sessionItem = new RESTItemList();
            sessionItem.id = session.getId();
            sessionItem.name = session.getId().substring(0, 5) + "...";
            sessionItem.url = Paths20.FULL_PATH_STATUS_SESSION(session.getId());
            sessions.sessionsList.add(sessionItem);
        }

        return ResponseEntity.ok(sessions);
    }

    @GetMapping(path = Paths20.FULL_PATH_STATUS_SESSION)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_SESSION,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web bridge's web session info", response = Params20.Session.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Session> getSessionReq(@PathVariable(Paths20.PARAM_SESSIONS) String sessionId) {
        HttpSession session = jslWB.getSession(sessionId);
        if (session == null)
            throw resourceNotFound("Web Session", sessionId);

        Params20.Session sessionRes = new Params20.Session();
        sessionRes.id = session.getId();
        sessionRes.name = session.getId().substring(0, 5) + "...";
        sessionRes.createdAt = new Date(session.getCreationTime());
        sessionRes.lastAccessedAt = new Date(session.getLastAccessedTime());
        sessionRes.maxInactiveInterval = session.getMaxInactiveInterval();

        return ResponseEntity.ok(sessionRes);
    }

}

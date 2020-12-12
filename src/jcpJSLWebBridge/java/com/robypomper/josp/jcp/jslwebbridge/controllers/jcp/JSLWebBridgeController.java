package com.robypomper.josp.jcp.jslwebbridge.controllers.jcp;

import com.robypomper.josp.clients.DefaultJCPClient2;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.clients.JCPAPIsClient;
import com.robypomper.josp.jcp.clients.jcp.jcp.APIsClient;
import com.robypomper.josp.jcp.jslwebbridge.jsl.JSLSpringService;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.JCPAPIsStatus;
import com.robypomper.josp.params.jcp.JCPFEStatus;
import com.robypomper.josp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.params.jcp.JCPJSLWebBridgeStatus;
import com.robypomper.josp.paths.jcp.APIJCP;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;
import java.util.List;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJCP.SubGroupJSLWebBridgeStatus.NAME})
public class JSLWebBridgeController {


    // Internal vars

    @Autowired
    private HttpSession httpSession;
    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private ClientParams params;
    @Value("${jcp.urlAPIs}")
    private String urlAPIs;


    @GetMapping(path = APIJCP.FULL_PATH_JSLWB_STATUS)
    @ApiOperation(value = "Return JCP JSL Web Bridge info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's info and stats", response = JCPJSLWebBridgeStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPJSLWebBridgeStatus> getJCPJSLWebBridgeReq() {
        return ResponseEntity.ok(new JCPJSLWebBridgeStatus());
    }

    // APIs forwards

    @GetMapping(path = APIJCP.FULL_PATH_JSLWB_STATUS_API)
    @ApiOperation(value = "Return JCP APIs info and stats")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's info and stats", response = JCPFEStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPAPIsStatus> getJCPAPIsStatusForward(@ApiIgnore HttpSession session) {
        checkAdmin();

        try {
            // Forward request to JCP APIs
            return ResponseEntity.ok(getAPIsClient(session).getJCPAPIsStatusReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE /* 503 */, String.format("Error occurred with resource because %s", e.getMessage()), e);
        }
    }

    @GetMapping(path = APIJCP.FULL_PATH_JSLWB_STATUS_API_OBJS)
    @ApiOperation(value = "Return JCP APIs registered JOD Objects")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered JOD Objects", response = JCPAPIsStatus.Objects.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPAPIsStatus.Objects> getJCPAPIsStatusObjsForward(@ApiIgnore HttpSession session) {
        checkAdmin();

        try {
            // Forward request to JCP APIs
            return ResponseEntity.ok(getAPIsClient(session).getJCPAPIsStatusObjsReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE /* 503 */, String.format("Error occurred with resource because %s", e.getMessage()), e);
        }
    }

    @GetMapping(path = APIJCP.FULL_PATH_JSLWB_STATUS_API_SRVS)
    @ApiOperation(value = "Return JCP APIs registered JSL Services")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered JSL Services", response = JCPAPIsStatus.Services.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPAPIsStatus.Services> getJCPAPIsStatusSrvsForward(@ApiIgnore HttpSession session) {
        checkAdmin();

        try {
            // Forward request to JCP APIs
            return ResponseEntity.ok(getAPIsClient(session).getJCPAPIsStatusSrvsReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE /* 503 */, String.format("Error occurred with resource because %s", e.getMessage()), e);
        }
    }

    @GetMapping(path = APIJCP.FULL_PATH_JSLWB_STATUS_API_USRS)
    @ApiOperation(value = "Return JCP APIs registered Users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered Users", response = JCPAPIsStatus.Users.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPAPIsStatus.Users> getJCPAPIsStatusUsrsForward(@ApiIgnore HttpSession session) {
        checkAdmin();

        try {
            // Forward request to JCP APIs
            return ResponseEntity.ok(getAPIsClient(session).getJCPAPIsStatusUsrsReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE /* 503 */, String.format("Error occurred with resource because %s", e.getMessage()), e);
        }
    }

    @GetMapping(path = APIJCP.FULL_PATH_JSLWB_STATUS_GWS)
    @ApiOperation(value = "Return JCP APIs registered JCP GWs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered JCP GWs", response = List.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPGWsStatus>> getJCPAPIsStatusGWsForward(@ApiIgnore HttpSession session) {
        checkAdmin();

        try {
            // Forward request to JCP APIs
            return ResponseEntity.ok(getAPIsClient(session).getJCPAPIsStatusGWsReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE /* 503 */, String.format("Error occurred with resource because %s", e.getMessage()), e);
        }
    }

    @GetMapping(path = APIJCP.FULL_PATH_JSLWB_STATUS_GWS_CLI)
    @ApiOperation(value = "Return JCP APIs registered JCP GWs clients")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered JCP GWs clients", response = List.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<List<JCPAPIsStatus.GWs>> getJCPAPIsStatusGWsCliForward(@ApiIgnore HttpSession session) {
        checkAdmin();

        try {
            // Forward request to JCP APIs
            return ResponseEntity.ok(getAPIsClient(session).getJCPAPIsStatusGWsCliReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE /* 503 */, String.format("Error occurred with resource because %s", e.getMessage()), e);
        }
    }

    @GetMapping(path = APIJCP.FULL_PATH_JSLWB_STATUS_JSLWB)
    @ApiOperation(value = "Return JCP JSL Web Bridge status")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge status", response = JCPJSLWebBridgeStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPJSLWebBridgeStatus> getJCPAPIsStatusJSLWBForward(@ApiIgnore HttpSession session) {
        checkAdmin();

        try {
            // Forward request to JCP APIs
            return ResponseEntity.ok(getAPIsClient(session).getJCPAPIsStatusJSLWBReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE /* 503 */, String.format("Error occurred with resource because %s", e.getMessage()), e);
        }
    }

    @GetMapping(path = APIJCP.FULL_PATH_JSLWB_STATUS_FE)
    @ApiOperation(value = "Return JCP FE status")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP FE status", response = JCPFEStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<JCPFEStatus> getJCPAPIsStatusFEForward(@ApiIgnore HttpSession session) {
        checkAdmin();

        try {
            // Forward request to JCP APIs
            return ResponseEntity.ok(getAPIsClient(session).getJCPAPIsStatusFEReq());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE /* 503 */, String.format("Error occurred with resource because %s", e.getMessage()), e);
        }
    }


    // Utils

    private void checkAdmin() {
        try {
            if (!jslService.get(httpSession).getUserMngr().isUserAuthenticated())
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED /* 401 */, "User not authenticated");

            if (!jslService.get(httpSession).getUserMngr().isAdmin())
                throw new ResponseStatusException(HttpStatus.FORBIDDEN /* 403 */, "Only Admin user can access to this request");

        } catch (JSLSpringService.JSLSpringException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't get JSL instance associated to this session", e);
        }
    }

    private APIsClient getAPIsClient(HttpSession session) {
        JCPAPIsClientSrv cl = jslService.getHttp(session).getJCPClient();
        JCPAPIsClient apiCl = new JCPAPIsClient(params, urlAPIs, false);

        DefaultJCPClient2.copyCredentials(cl, apiCl);

        return new APIsClient(apiCl);
    }

}

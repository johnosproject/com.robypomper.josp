package com.robypomper.josp.jcp.fe.controllers.jcp;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.params.jcp.JCPAPIsStatus;
import com.robypomper.josp.params.jcp.JCPFEStatus;
import com.robypomper.josp.paths.APIJCP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.List;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJCP.SubGroupFEStatus.NAME})
public class FEController {

    // Internal vars

    @Autowired
    private HttpSession httpSession;
    @Autowired
    private JSLSpringService jslService;


    // Methods


    @GetMapping(path = APIJCP.FULL_PATH_FE_STATUS)
    @ApiOperation(value = "Return JCP FE info and stats")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP FE's info and stats", response = JCPFEStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<JCPFEStatus> getJCPFEStatusReq() {
        checkAdmin();

        return ResponseEntity.ok(new JCPFEStatus());
    }

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS)
    @ApiOperation(value = "Return JCP APIs info and stats")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's info and stats", response = JCPFEStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<JCPAPIsStatus> getJCPAPIsStatusForward() {
        checkAdmin();

        // Forward request to JCP APIs

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            JCPAPIsStatus jcpAPIsResponse = jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_APIS_STATUS, JCPAPIsStatus.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS_GWS)
    @ApiOperation(value = "Return JCP APIs registered JCP GWs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered JCP GWs", response = List.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<List<JCPAPIsStatus.GWs>> getJCPAPIsStatusGWsForward() {
        checkAdmin();

        // Forward request to JCP APIs

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            List<JCPAPIsStatus.GWs> jcpAPIsResponse = jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_APIS_STATUS_GWS, List.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS_OBJS)
    @ApiOperation(value = "Return JCP APIs registered JOD Objects")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered JOD Objects", response = JCPAPIsStatus.Objects.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<JCPAPIsStatus.Objects> getJCPAPIsStatusObjsForward() {
        checkAdmin();

        // Forward request to JCP APIs

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            JCPAPIsStatus.Objects jcpAPIsResponse = jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_APIS_STATUS_OBJS, JCPAPIsStatus.Objects.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS_SRVS)
    @ApiOperation(value = "Return JCP APIs registered JSL Services")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered JSL Services", response = JCPAPIsStatus.Services.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<JCPAPIsStatus.Services> getJCPAPIsStatusSrvsForward() {
        checkAdmin();

        // Forward request to JCP APIs

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            JCPAPIsStatus.Services jcpAPIsResponse = jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_APIS_STATUS_SRVS, JCPAPIsStatus.Services.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = APIJCP.FULL_PATH_APIS_STATUS_USRS)
    @ApiOperation(value = "Return JCP APIs registered Users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs registered Users", response = JCPAPIsStatus.Users.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<JCPAPIsStatus.Users> getJCPAPIsStatusUsrsForward() {
        checkAdmin();

        // Forward request to JCP APIs

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            JCPAPIsStatus.Users jcpAPIsResponse = jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_APIS_STATUS_USRS, JCPAPIsStatus.Users.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private void checkAdmin() {
        try {
            if (!jslService.get(httpSession).getUserMngr().isUserAuthenticated())
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED /* 401 */, "User not authenticated");

            if (!jslService.get(httpSession).getUserMngr().isAdmin())
                throw new ResponseStatusException(HttpStatus.FORBIDDEN /* 403 */, "Only Admin user can access to this request");

        } catch (JSLSpringService.JSLSpringException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't get JSL instance associated to this session");
        }
    }

}

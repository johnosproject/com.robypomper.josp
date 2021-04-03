package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.admin.apis.status;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.apis.status.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.apis.status.Paths20;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.controllers.ControllerLinkJSL;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.admin.JSLAdmin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;


/**
 * JCP JSL Web Bridge - Admin / APIs / Status 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLinkJSL {


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private ClientParams params;


    // Constructors

    public Controller20() {
        super(Paths20.API_NAME, Paths20.API_VER, JCPJSLWBVersions.API_NAME, Paths20.DOCS_NAME, Paths20.DOCS_DESCR);
    }


    // JCP APIs Status methods

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_STATUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Index> getJCPAPIsStatusReq(@ApiIgnore HttpSession session) {
        return ResponseEntity.ok(new Params20.Index());
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS_OBJS)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_OBJS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Objects.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Objects> getJCPAPIsStatusObjectsReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsObjects());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS_OBJ)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_OBJ)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Object.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Object> getJCPAPIsStatusObjectReq(
            @ApiIgnore HttpSession session,
            @PathVariable(com.robypomper.josp.defs.admin.apis.status.Paths20.PARAM_OBJ) String objId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsObject(objId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS_SRVS)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_SRVS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Services.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Services> getJCPAPIsStatusServicesReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsServices());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS_SRV)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_SRV)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Service.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Service> getJCPAPIsStatusServiceReq(
            @ApiIgnore HttpSession session,
            @PathVariable(com.robypomper.josp.defs.admin.apis.status.Paths20.PARAM_SRV) String srvId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsService(srvId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS_USRS)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_USRS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Users.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Users> getJCPAPIsStatusUsersReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsUsers());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS_USR)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_USR)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.User.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.User> getJCPAPIsStatusUserReq(
            @ApiIgnore HttpSession session,
            @PathVariable(com.robypomper.josp.defs.admin.apis.status.Paths20.PARAM_USR) String usrId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsUser(usrId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS_GWS)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_GWS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Gateways.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Gateways> getJCPAPIsStatusGatewaysReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsGateways());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS_GW)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_GW)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Gateway.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Gateway> getJCPAPIsStatusGatewayReq(
            @ApiIgnore HttpSession session,
            @PathVariable(com.robypomper.josp.defs.admin.apis.status.Paths20.PARAM_GW) String gwId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsGateway(gwId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

}

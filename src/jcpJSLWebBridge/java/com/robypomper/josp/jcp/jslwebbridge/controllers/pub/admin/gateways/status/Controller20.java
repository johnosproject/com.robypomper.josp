package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.admin.gateways.status;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.gateways.status.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.gateways.status.Paths20;
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
 * JCP JSL Web Bridge - Admin / Gateways / Status 2.0
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


    // JCP Gateways Status methods

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_LIST)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_LIST)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.GatewaysServers.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.GatewaysServers> getJCPGatewaysStatusGatewaysServersReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGatewaysServers());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Index> getJCPAPIsStatusReq(
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_GW_SERVER) String gwServerId) {
        return ResponseEntity.ok(new Params20.Index(gwServerId));
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GWS)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GWS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.GWs.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.GWs> getJCPGatewaysStatusGatewaysReq(
            @ApiIgnore HttpSession session,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_GW_SERVER) String gwServerId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGatewaysGWs(gwServerId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.GW.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.GW> getJCPGatewaysStatusGatewayReq(
            @ApiIgnore HttpSession session,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_GW_SERVER) String gwServerId,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_GW) String gwId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGatewaysGW(gwServerId, gwServerId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW_CLIENT)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW_CLIENT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.GWClient.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.GWClient> getJCPGatewaysStatusGatewayClientReq(
            @ApiIgnore HttpSession session,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_GW_SERVER) String gwServerId,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_GW) String gwId,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_GW_CLIENT) String gwClientId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGatewaysGWsClient(gwServerId, gwServerId, gwClientId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Broker.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Broker> getJCPGatewaysStatusBrokerReq(
            @ApiIgnore HttpSession session,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_GW_SERVER) String gwServerId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGatewaysBroker(gwServerId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.BrokerObject.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.BrokerObject> getJCPGatewaysStatusBrokerObjectReq(
            @ApiIgnore HttpSession session,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_GW_SERVER) String gwServerId,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_OBJ) String objId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGatewaysBrokerObject(gwServerId, objId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_SRV)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_SRV)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.BrokerService.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.BrokerService> getJCPGatewaysStatusBrokerServiceReq(
            @ApiIgnore HttpSession session,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_GW_SERVER) String gwServerId,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_SRV) String srvId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGatewaysBrokerService(gwServerId, srvId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ_DB)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ_DB)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.BrokerObjectDB.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.BrokerObjectDB> getJCPGatewaysStatusBrokerObjectDBReq(
            @ApiIgnore HttpSession session,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_GW_SERVER) String gwServerId,
            @PathVariable(com.robypomper.josp.defs.admin.gateways.status.Paths20.PARAM_OBJ) String objId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGatewaysBrokerObjectDB(gwServerId, objId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

}

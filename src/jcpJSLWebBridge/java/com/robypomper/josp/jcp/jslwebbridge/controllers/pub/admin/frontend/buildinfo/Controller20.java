package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.admin.frontend.buildinfo;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.frontend.buildinfo.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.frontend.buildinfo.Paths20;
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
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;


/**
 * JCP JSL Web Bridge - Admin / Front End / Build Info 2.0
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


    // JCP Front End Build Info methods

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_FRONTEND_BUILDINFO)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_FRONTEND_BUILDINFO)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.BuildInfo.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.BuildInfo> getJCPFrontEndBuildInfoReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPFrontEndBuildInfo());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

}

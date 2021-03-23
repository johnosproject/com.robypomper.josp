package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jcp.params.jslwb.JOSPObjHtml;
import com.robypomper.josp.jcp.params.jslwb.JOSPUserHtml;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBUsr;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.states.StateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBUsr.SubGroupUser.NAME})
public class APIJSLWBUsrController extends APIJSLWBControllerAbs {

    public static final String SESS_ATTR_LOGIN_REDIRECT = "redirect_url_login";
    public static final String SESS_ATTR_LOGOUT_REDIRECT = "redirect_url_logout";

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(APIJSLWBUsrController.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;
    private final String URL_REDIRECT_HOME = "/";


    // Constructors

    public APIJSLWBUsrController() {
        super(APIJSLWBUsr.API_NAME, APIJSLWBUsr.API_VER, JCPJSLWBVersions.API_NAME, APIJSLWBUsr.SubGroupUser.NAME, APIJSLWBUsr.SubGroupUser.DESCR);
    }


    // Swagger configs

    @Bean
    public Docket swaggerConfig_APIJSLWBUsr() {
        return swaggerConfig();
    }


    // Methods User Info

    @GetMapping(path = APIJSLWBUsr.FULL_PATH_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBUsr.DESCR_PATH_DETAILS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<JOSPUserHtml> jsonUserDetails(@ApiIgnore HttpSession session) {

        JSL jsl = getJSL(session.getId(),"get user");
        JSLUserMngr jslUserMngr = jsl.getUserMngr();
        return ResponseEntity.ok(new JOSPUserHtml(jslUserMngr));
    }


    // Methods - Login

    @GetMapping(path = APIJSLWBUsr.FULL_PATH_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBUsr.DESCR_PATH_LOGIN)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<String> htmlLoginUser(@ApiIgnore HttpSession session,
                                                @ApiIgnore HttpServletResponse response,
                                                @RequestParam(name = "redirect_uri", required = false) String redirectUrl,
                                                @RequestParam(name = "auto_redirect", required = false) boolean autoRedirect) {
        JSL jsl = getJSL(session.getId(),"get login url");

        if (redirectUrl != null)
            session.setAttribute(SESS_ATTR_LOGIN_REDIRECT, redirectUrl);

        String redirect = jsl.getJCPClient().getAuthLoginUrl();

        if (autoRedirect) {
            try {
                response.sendRedirect(redirect);

            } catch (IOException ignore) {
            }
            return ResponseEntity.ok(String.format("Redirect failed, please go to <a href=\"%s\">%s</a>", redirect, redirect));
        }

        return ResponseEntity.ok(redirect);
    }

    @GetMapping(path = APIJSLWBUsr.FULL_PATH_LOGIN_CALLBACK, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBUsr.DESCR_PATH_LOGIN_CALLBACK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<String> htmlLoginUserCallback(@ApiIgnore HttpSession session,
                                                        @ApiIgnore HttpServletResponse response,
                                                        @RequestParam(name = "session_state") String sessionState,
                                                        @RequestParam(name = "code") String code) {
        //https://localhost:8080/login/code/?session_state=087edff3-848c-4b59-9592-e44c7410e6b0&code=8ab0ceb4-e3cf-48e2-99df-b59fe7be129d.087edff3-848c-4b59-9592-e44c7410e6b0.79e472b0-e562-4535-a516-db7d7696a447
        JSL jsl = getJSL(session.getId(),"exec user login callback");

        String redirectURL = (String) session.getAttribute(SESS_ATTR_LOGIN_REDIRECT);
        session.removeAttribute(SESS_ATTR_LOGIN_REDIRECT);

        try {
            jsl.getJCPClient().setLoginCodeAndReconnect(code);

        } catch (StateException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't connect JCP APIs service because %s", e.getMessage()), e);

        } catch (JCPClient2.AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't access to JCP APIs service because authentication error %s", e.getMessage()), e);
        }

        try {
            if (redirectURL != null)
                response.sendRedirect(redirectURL);
            else
                response.sendRedirect(URL_REDIRECT_HOME);

        } catch (IOException ignore) {
        }

        return ResponseEntity.ok(String.format("User login successfully but redirect failed, please go to <a href=\"%s\">%s</a>", URL_REDIRECT_HOME, URL_REDIRECT_HOME));
    }


    // Methods - Logout

    @GetMapping(path = APIJSLWBUsr.FULL_PATH_LOGOUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBUsr.DESCR_PATH_LOGOUT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<String> htmlLogoutUser(@ApiIgnore HttpSession session,
                                                 @ApiIgnore HttpServletResponse response,
                                                 @RequestParam(name = "redirect_uri", required = false) String redirectUrl,
                                                 @RequestParam(name = "auto_redirect", required = false) boolean autoRedirect) {

        JSL jsl = getJSL(session.getId(),"get logout url");

        String redirect = jsl.getJCPClient().getAuthLogoutUrl(redirectUrl);
        jsl.getJCPClient().userLogout();

        if (autoRedirect) {
            try {
                response.sendRedirect(redirect);

            } catch (IOException ignore) {
            }
            return ResponseEntity.ok(String.format("User logout successfully but redirect failed, please go to <a href=\"%s\">%s</a>", redirect, redirect));
        }

        return ResponseEntity.ok(redirect);
    }


    // Methods - Registration

    @GetMapping(path = APIJSLWBUsr.FULL_PATH_REGISTRATION, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBUsr.DESCR_PATH_REGISTRATION)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<String> htmlRegistrationUser(@ApiIgnore HttpSession session,
                                                       @ApiIgnore HttpServletResponse response,
                                                       @RequestParam(name = "redirect_uri", required = false) String redirectUrl,
                                                       @RequestParam(name = "auto_redirect", required = false) boolean autoRedirect) {

        JSL jsl = getJSL(session.getId(),"get registration url");

        if (redirectUrl != null)
            session.setAttribute(SESS_ATTR_LOGIN_REDIRECT, redirectUrl);

        String redirect = jsl.getJCPClient().getAuthRegistrationUrl();
        jsl.getJCPClient().userLogout();

        if (autoRedirect) {
            try {
                response.sendRedirect(redirect);

            } catch (IOException ignore) {
            }
            return ResponseEntity.ok(String.format("Redirect failed, please go to <a href=\"%s\">%s</a>", redirect, redirect));
        }

        return ResponseEntity.ok(redirect);
    }


    // Utils

    private String getCurrentBaseUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder();
        String scheme = request.getScheme();
        int port = request.getServerPort();
        if (port < 0)
            port = 80;          // Work around java.net.URL bug

        if (request.getHeader("X-Forwarded-For") != null) {
            scheme = "https";
            port = 443;
        }

        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());
        if ((scheme.equals("http") && (port != 80))
                || (scheme.equals("https") && (port != 443))) {
            url.append(':');
            url.append(port);
        }

        return url.toString();
    }

}

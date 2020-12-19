package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.info.JCPFEVersions;
import com.robypomper.josp.jcp.jslwebbridge.jsl.JSLSpringService;
import com.robypomper.josp.jcp.params.jslwb.JOSPObjHtml;
import com.robypomper.josp.jcp.params.jslwb.JOSPUserHtml;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBUsr;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.states.StateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
public class APIJSLWBUsrController {

    public static final String SESS_ATTR_LOGIN_REDIRECT = "redirect_url_login";
    public static final String SESS_ATTR_LOGOUT_REDIRECT = "redirect_url_logout";

    // Internal vars

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;
    private final String URL_REDIRECT_HOME = "/";


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIJSLWBUsr() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIJSLWBUsr.SubGroupUser.NAME, APIJSLWBUsr.SubGroupUser.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIJSLWBUsr.API_NAME, APIJSLWBUsr.API_VER, JCPFEVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods User Info

    public static JOSPUserHtml userDetails(HttpSession session,
                                           JSLSpringService jslStaticService) {
        // Convert to HTML shared structure
        JSLUserMngr jslUserMngr = jslStaticService.getUserMngr(jslStaticService.getHttp(session));
        return new JOSPUserHtml(jslUserMngr);
    }

    @GetMapping(path = APIJSLWBUsr.FULL_PATH_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<JOSPUserHtml> jsonUserDetails(@ApiIgnore HttpSession session) {
        return ResponseEntity.ok(userDetails(session, jslService));
    }


    // Methods - Login

    @GetMapping(path = APIJSLWBUsr.FULL_PATH_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlLoginUser(@ApiIgnore HttpSession session,
                                @ApiIgnore HttpServletResponse response,
                                @RequestParam(name = "redirect_uri", required = false) String redirectUrl) {
        if (redirectUrl != null)
            session.setAttribute(SESS_ATTR_LOGIN_REDIRECT, redirectUrl);

        String redirect = jslService.getLoginUrl(jslService.getHttp(session));

        try {
            response.sendRedirect(redirect);

        } catch (IOException ignore) {
        }

        return String.format("Redirect failed, please go to <a href=\"%s\">%s</a>", redirect, redirect);
    }

    @GetMapping(path = APIJSLWBUsr.FULL_PATH_LOGIN_CALLBACK, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String authRedirect(@ApiIgnore HttpSession session,
                               @ApiIgnore HttpServletResponse response,
                               @RequestParam(name = "session_state") String sessionState,
                               @RequestParam(name = "code") String code) {
        //https://localhost:8080/login/code/?session_state=087edff3-848c-4b59-9592-e44c7410e6b0&code=8ab0ceb4-e3cf-48e2-99df-b59fe7be129d.087edff3-848c-4b59-9592-e44c7410e6b0.79e472b0-e562-4535-a516-db7d7696a447

        String redirectURL = (String) session.getAttribute(SESS_ATTR_LOGIN_REDIRECT);
        session.removeAttribute(SESS_ATTR_LOGIN_REDIRECT);

        try {
            jslService.login(jslService.getHttp(session), code);

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

        return String.format("Login successfully, go to <a href=\"%s\">%s</a>", URL_REDIRECT_HOME, URL_REDIRECT_HOME);
    }


    // Methods - Logout

    @GetMapping(path = APIJSLWBUsr.FULL_PATH_LOGOUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlLogoutUser(@ApiIgnore HttpSession session,
                                 @ApiIgnore HttpServletResponse response,
                                 @RequestParam(name = "redirect_uri", required = false) String redirectUrl) {

        String redirect = jslService.getLogoutUrl(jslService.getHttp(session), redirectUrl);
        jslService.logout(jslService.getHttp(session));

        try {
            response.sendRedirect(redirect);

        } catch (IOException ignore) {
        }

        return String.format("Redirect failed, please go to <a href=\"%s\">%s</a>", redirect, redirect);
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

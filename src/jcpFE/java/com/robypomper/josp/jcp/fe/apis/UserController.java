package com.robypomper.josp.jcp.fe.apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.fe.HTMLUtils;
import com.robypomper.josp.jcp.params.fe.JOSPUserHtml;
import com.robypomper.josp.jcp.paths.fe.APIJCPFEUser;
import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@RestController
//@Api(tags = {APIJCPFEUser.SubGroupUser.NAME})
public class UserController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;
    private final String URL_REDIRECT_HOME = "/";


    // User Info

    public static JOSPUserHtml userDetails(HttpSession session,
                                           JSLSpringService jslStaticService) {
        // Convert to HTML shared structure
        JSLUserMngr jslUserMngr = jslStaticService.getUserMngr(jslStaticService.getHttp(session));
        return new JOSPUserHtml(jslUserMngr);
    }

    @GetMapping(path = APIJCPFEUser.FULL_PATH_DETAILS)
    public ResponseEntity<JOSPUserHtml> jsonUserDetails(HttpSession session) {
        return ResponseEntity.ok(userDetails(session, jslService));
    }

    @GetMapping(path = APIJCPFEUser.FULL_PATH_DETAILS, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlUserDetails(HttpSession session) {
        JOSPUserHtml usrHtml = jsonUserDetails(session).getBody();
        if (usrHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error on get service info.");

        try {
            return HTMLUtils.toHTMLFormattedJSON(usrHtml, String.format("User %s", usrHtml.name));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get service's info on formatting response (%s).", e.getMessage()), e);
        }
    }


    // Login

    @GetMapping(path = APIJCPFEUser.FULL_PATH_LOGIN)
    public String htmlLoginUser(HttpSession session,
                                HttpServletResponse response) {
        String redirect = jslService.getLoginUrl(jslService.getHttp(session));

        try {
            response.sendRedirect(redirect);

        } catch (IOException ignore) {
        }

        return String.format("Redirect failed, please go to <a href=\"%s\">%s</a>", redirect, redirect);
    }

    @GetMapping(path = APIJCPFEUser.FULL_PATH_LOGIN_CALLBACK)
    public String authRedirect(HttpSession session,
                               HttpServletResponse response,
                               @RequestParam(name = "session_state") String sessionState,
                               @RequestParam(name = "code") String code) {
        //https://localhost:8080/login/code/?session_state=087edff3-848c-4b59-9592-e44c7410e6b0&code=8ab0ceb4-e3cf-48e2-99df-b59fe7be129d.087edff3-848c-4b59-9592-e44c7410e6b0.79e472b0-e562-4535-a516-db7d7696a447
        try {
            jslService.login(jslService.getHttp(session), code);

        } catch (JCPClient2.ConnectionException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't connect JCP APIs service because %s", e.getMessage()), e);

        } catch (JCPClient2.JCPNotReachableException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, String.format("Can't connect JCP APIs service because unreachable (%s)", e.getMessage()), e);

        } catch (JCPClient2.AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't access to JCP APIs service because authentication error %s", e.getMessage()), e);
        }

        try {
            response.sendRedirect(URL_REDIRECT_HOME);

        } catch (IOException ignore) {
        }

        return String.format("Login successfully, go to <a href=\"%s\">%s</a>", URL_REDIRECT_HOME, URL_REDIRECT_HOME);
    }


    // Logout

    @GetMapping(path = APIJCPFEUser.FULL_PATH_LOGOUT, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlLogoutUser(HttpSession session,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        JSL jsl = jslService.getHttp(session);
        jslService.logout(jsl);
        String redirect = jslService.getLogoutUrl(jsl, getCurrentBaseUrl(request) + URL_REDIRECT_HOME);

        try {
            response.sendRedirect(redirect);

        } catch (IOException ignore) {
        }

        return String.format("Redirect failed, please go to <a href=\"%s\">%s</a>", redirect, redirect);
    }


    // Utils

    private String getCurrentBaseUrl(HttpServletRequest request) {
        StringBuffer url = new StringBuffer();
        String scheme = request.getScheme();
        int port = request.getServerPort();
        if (port < 0)
            port = 80;          // Work around java.net.URL bug

        if (request.getHeader("X-Forwarded-For")!=null) {
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

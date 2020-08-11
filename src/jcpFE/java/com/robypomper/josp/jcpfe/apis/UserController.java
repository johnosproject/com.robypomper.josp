package com.robypomper.josp.jcpfe.apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcpfe.HTMLUtils;
import com.robypomper.josp.jcpfe.apis.params.JOSPUserHtml;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEUser;
import com.robypomper.josp.jcpfe.jsl.JSLSpringService;
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
import java.io.IOException;


@RestController
//@Api(tags = {APIJCPFEUser.SubGroupUser.NAME})
public class UserController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;


    // User Info

    public static JOSPUserHtml userDetails(JSLSpringService jslStaticService) {
        // Convert to HTML shared structure
        JSLUserMngr jslUserMngr = jslStaticService.getUserMngr();
        return new JOSPUserHtml(jslUserMngr);
    }

    @GetMapping(path = APIJCPFEUser.FULL_PATH_DETAILS)
    public ResponseEntity<JOSPUserHtml> jsonUserDetails() {
        JSLUserMngr jslUserMngr = jslService.getUserMngr();
        return ResponseEntity.ok(userDetails(jslService));
    }

    @GetMapping(path = APIJCPFEUser.FULL_PATH_DETAILS, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlUserDetails() {
        JOSPUserHtml usrHtml = jsonUserDetails().getBody();
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
    public String htmlLoginUser(HttpServletResponse response) {
        String redirect = jslService.getLoginUrl(); //http://localhost:8080/test/user/login/code/

        try {
            response.sendRedirect(redirect);

        } catch (IOException ignore) {
        }

        return String.format("Redirect failed, please go to <a href=\"%s\">%s</a>", redirect, redirect);
    }

    @GetMapping(path = APIJCPFEUser.FULL_PATH_LOGIN_CALLBACK)
    public String authRedirect(HttpServletResponse response,
                               @RequestParam(name = "session_state") String sessionState,
                               @RequestParam(name = "code") String code) {
        //https://localhost:8080/login/code/?session_state=087edff3-848c-4b59-9592-e44c7410e6b0&code=8ab0ceb4-e3cf-48e2-99df-b59fe7be129d.087edff3-848c-4b59-9592-e44c7410e6b0.79e472b0-e562-4535-a516-db7d7696a447
        try {
            jslService.login(code);

        } catch (JCPClient2.ConnectionException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't connect JCP APIs service because %s", e.getMessage()), e);

        } catch (JCPClient2.JCPNotReachableException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, String.format("Can't connect JCP APIs service because unreachable (%s)", e.getMessage()), e);

        } catch (JCPClient2.AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't access to JCP APIs service because authentication error %s", e.getMessage()), e);
        }

        String redirect = "/test/";
        try {
            response.sendRedirect(redirect);

        } catch (IOException ignore) {
        }

        return String.format("Login successfully, go to <a href=\"%s\">%s</a>", redirect, redirect);
    }


    // Logout

    @GetMapping(path = APIJCPFEUser.FULL_PATH_LOGOUT)
    public ResponseEntity<Boolean> jsonLogoutUser() {
        jslService.logout();
        return ResponseEntity.ok(true);
    }

    @GetMapping(path = APIJCPFEUser.FULL_PATH_LOGOUT, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlLogoutUser(HttpServletRequest request) {
        @SuppressWarnings("ConstantConditions") boolean success = jsonLogoutUser().getBody();
        return HTMLUtils.redirectBackAndReturn(request, success);
    }

}

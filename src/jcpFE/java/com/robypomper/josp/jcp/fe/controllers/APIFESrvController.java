package com.robypomper.josp.jcp.fe.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.jcp.fe.HTMLUtils;
import com.robypomper.josp.jcp.params.fe.JOSPSrvHtml;
import com.robypomper.josp.jcp.paths.fe.APIJCPFEService;
import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jsl.JSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;


@RestController
//@Api(tags = {APIJCPFEService.SubGroupService.NAME})
public class APIFESrvController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;


    // Service Info

    public static JOSPSrvHtml serviceDetails(HttpSession session,
                                             JSLSpringService jslStaticService) {
        // Convert to HTML shared structure
        JSL jsl = jslStaticService.getJSL(jslStaticService.getHttp(session));
        return new JOSPSrvHtml(session, jsl);
    }


    @GetMapping(path = APIJCPFEService.FULL_PATH_DETAILS)
    public ResponseEntity<JOSPSrvHtml> jsonServiceDetails(HttpSession session) {
        return ResponseEntity.ok(serviceDetails(session, jslService));
    }

    @GetMapping(path = APIJCPFEService.FULL_PATH_DETAILS, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlServiceDetails(HttpSession session) {
        JOSPSrvHtml srvHtml = jsonServiceDetails(session).getBody();
        if (srvHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error on get service info.");

        try {
            return HTMLUtils.toHTMLFormattedJSON(srvHtml, String.format("Service %s", srvHtml.name));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get service's info on formatting response (%s).", e.getMessage()), e);
        }
    }


    // Shared structures

}

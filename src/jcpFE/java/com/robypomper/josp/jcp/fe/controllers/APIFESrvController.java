package com.robypomper.josp.jcp.fe.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.jcp.fe.HTMLUtils;
import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jcp.params.fe.JOSPObjHtml;
import com.robypomper.josp.jcp.params.fe.JOSPSrvHtml;
import com.robypomper.josp.jcp.paths.fe.APIFESrv;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jsl.JSL;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpSession;


@RestController
@Api(tags = {APIFESrv.SubGroupService.NAME})
public class APIFESrvController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIFESrv() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIFESrv.SubGroupService.NAME, APIFESrv.SubGroupService.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIFESrv.API_NAME, APIFESrv.API_VER, sg), swagger.getUrlBaseAuth());
    }


    // Methods

    public static JOSPSrvHtml serviceDetails(HttpSession session,
                                             JSLSpringService jslStaticService) {
        // Convert to HTML shared structure
        JSL jsl = jslStaticService.getJSL(jslStaticService.getHttp(session));
        return new JOSPSrvHtml(session, jsl);
    }


    @GetMapping(path = APIFESrv.FULL_PATH_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<JOSPSrvHtml> jsonServiceDetails(@ApiIgnore HttpSession session) {
        return ResponseEntity.ok(serviceDetails(session, jslService));
    }

    @GetMapping(path = APIFESrv.FULL_PATH_DETAILS, produces = MediaType.TEXT_HTML_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String htmlServiceDetails(@ApiIgnore HttpSession session) {
        JOSPSrvHtml srvHtml = jsonServiceDetails(session).getBody();
        if (srvHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error on get service info.");

        try {
            return HTMLUtils.toHTMLFormattedJSON(srvHtml, String.format("Service %s", srvHtml.name));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get service's info on formatting response (%s).", e.getMessage()), e);
        }
    }

}

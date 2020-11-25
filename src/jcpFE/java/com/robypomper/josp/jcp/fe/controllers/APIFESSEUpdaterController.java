package com.robypomper.josp.jcp.fe.controllers;

import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jcp.params.fe.JOSPObjHtml;
import com.robypomper.josp.jcp.paths.fe.APIFESSEUpdater;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@Api(tags = {APIFESSEUpdater.SubGroupState.NAME})
public class APIFESSEUpdaterController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIFESSEUpdater() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIFESSEUpdater.SubGroupState.NAME, APIFESSEUpdater.SubGroupState.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIFESSEUpdater.API_NAME, APIFESSEUpdater.API_VER, sg), swagger.getUrlBaseAuth());
    }


    // Methods - SSE Updater

    @GetMapping(path = APIFESSEUpdater.FULL_PATH_INIT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public SseEmitter streamSseMvc(@ApiIgnore HttpSession session,
                                   @ApiIgnore HttpServletResponse response,
                                   CsrfToken token) {
        SseEmitter emitter;
        try {
            emitter = jslService.newEmitter(session);

        } catch (JSLSpringService.JSLSpringException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }

        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data("csrf:" + token.getToken() + ";" +
                        "header:" + token.getHeaderName())
                .id(String.valueOf(0));
        try {
            emitter.send(event);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }

        response.addHeader("X-Accel-Buffering", "no");
        return emitter;
    }

}

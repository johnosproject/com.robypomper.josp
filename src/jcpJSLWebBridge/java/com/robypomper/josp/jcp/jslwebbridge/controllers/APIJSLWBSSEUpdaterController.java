package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.info.JCPFEVersions;
import com.robypomper.josp.jcp.jslwebbridge.jsl.JSLSpringService;
import com.robypomper.josp.jcp.params.jslwb.JOSPObjHtml;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBSSEUpdater;
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


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBSSEUpdater.SubGroupState.NAME})
public class APIJSLWBSSEUpdaterController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIJSLWBSSEUpdater() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIJSLWBSSEUpdater.SubGroupState.NAME, APIJSLWBSSEUpdater.SubGroupState.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIJSLWBSSEUpdater.API_NAME, APIJSLWBSSEUpdater.API_VER, JCPFEVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods - SSE Updater

    @GetMapping(path = APIJSLWBSSEUpdater.FULL_PATH_INIT, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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

        SseEmitter.SseEventBuilder eventCSRF = SseEmitter.event()
                .data("csrf:" + token.getToken() + ";" + "header:" + token.getHeaderName())
                .id(String.valueOf(0));
        //SseEmitter.SseEventBuilder eventSession = SseEmitter.event()
        //        .data("cookie:JSESSIONID=" + session.getId())
        //        .id(String.valueOf(0));
        try {
            emitter.send(eventCSRF);
            //emitter.send(eventSession);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }

        //response.addHeader("Access-Control-Allow-Headers", "Cookie, Set-Cookie");
        response.addHeader("X-Accel-Buffering", "no");
        return emitter;
    }

}

package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.exceptions.*;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jcp.params.jslwb.JSLStatus;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBInit;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jsl.JSL;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBInit.SubGroupInit.NAME})
public class APIJSLWBInitController extends APIJSLWBControllerAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(APIJSLWBInitController.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Constructors

    public APIJSLWBInitController() {
        super(APIJSLWBInit.API_NAME, APIJSLWBInit.API_VER, JCPJSLWBVersions.API_NAME, APIJSLWBInit.SubGroupInit.NAME, APIJSLWBInit.SubGroupInit.DESCR);
    }


    // Swagger configs

    @Bean
    public Docket swaggerConfig_APIJSLWBInit() {
        return swaggerConfig();
    }


    // Methods - JSL Instance Status

    @GetMapping(path = APIJSLWBInit.FULL_PATH_INIT_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBInit.DESCR_PATH_INIT_STATUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JSLStatus.class),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<JSLStatus> statusJSL(@ApiIgnore HttpSession session) {
        boolean isJSLInit = false;
        try {
            webBridgeService.getJSL(session.getId());
            isJSLInit = true;

        } catch (JSLNotInitForSessionException ignore) {
        }

        return ResponseEntity.ok(new JSLStatus(session.getId(), isJSLInit));
    }


    // Methods - Init JSL Instance

    @GetMapping(path = APIJSLWBInit.FULL_PATH_INIT_JSL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBInit.DESCR_PATH_INIT_JSL)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<String> initJSL(@ApiIgnore HttpSession session,
                                           @RequestParam("client_id") String clientId,
                                           @RequestParam("client_secret") String clientSecret) {
        JSL jsl = null;
        try {
            jsl = webBridgeService.initJSL(session.getId(), clientId, clientSecret);

        } catch (JSLAlreadyInitForSessionException ignore) {
            try {
                jsl = webBridgeService.getJSL(session.getId());

            } catch (JSLNotInitForSessionException ignore2) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't initialize/get JSL instance for '%s' session", session.getId()));
            }

        } catch (JSLErrorOnInitException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't initialize JSL instance for '%s' session (%s)", session.getId(), e));
        }

        return ResponseEntity.ok(jsl.getServiceInfo().getFullId());
    }


    // Methods - SSE Updater

    @GetMapping(path = APIJSLWBInit.FULL_PATH_INIT_SSE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation(value = APIJSLWBInit.DESCR_PATH_INIT_SSE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = SseEmitter.class),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public SseEmitter initSSE(@ApiIgnore HttpSession session,
                              @ApiIgnore HttpServletResponse response,
                              @RequestParam(name = "client_id", required = false) String clientId,
                              @RequestParam(name = "client_secret", required = false) String clientSecret) {
        if (clientId != null)
            initJSL(session, clientId, clientSecret);

        SseEmitter emitter;
        try {
            emitter = webBridgeService.getJSLEmitter(session.getId());

        } catch (JSLNotInitForSessionException e) {
            throw jslNotInitForSessionException(session.getId(),"initialize JSL Emitter");
        }

        response.addHeader("X-Accel-Buffering", "no");
        return emitter;
    }

}

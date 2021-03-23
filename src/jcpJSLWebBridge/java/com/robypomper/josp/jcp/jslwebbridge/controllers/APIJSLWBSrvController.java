package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jcp.params.jslwb.JOSPObjHtml;
import com.robypomper.josp.jcp.params.jslwb.JOSPSrvHtml;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBSrv;
import com.robypomper.josp.jsl.JSL;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpSession;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJSLWBSrv.SubGroupService.NAME})
public class APIJSLWBSrvController extends APIJSLWBControllerAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(APIJSLWBSrvController.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;


    // Constructors

    public APIJSLWBSrvController() {
        super(APIJSLWBSrv.API_NAME, APIJSLWBSrv.API_VER, JCPJSLWBVersions.API_NAME, APIJSLWBSrv.SubGroupService.NAME, APIJSLWBSrv.SubGroupService.DESCR);
    }


    // Swagger configs

    @Bean
    public Docket swaggerConfig_APIJSLWBSrv() {
        return swaggerConfig();
    }


    // Methods

    @GetMapping(path = APIJSLWBSrv.FULL_PATH_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = APIJSLWBSrv.DESCR_PATH_DETAILS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "JSL Instance not initialized")
    })
    public ResponseEntity<JOSPSrvHtml> jsonServiceDetails(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId(),"get service");

        JOSPSrvHtml srvHTML = new JOSPSrvHtml(session, jsl);
        return ResponseEntity.ok(srvHTML);
    }

}

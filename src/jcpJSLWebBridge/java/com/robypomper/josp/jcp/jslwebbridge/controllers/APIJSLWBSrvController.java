package com.robypomper.josp.jcp.jslwebbridge.controllers;

import com.robypomper.josp.jcp.info.JCPFEVersions;
import com.robypomper.josp.jcp.jslwebbridge.jsl.JSLSpringService;
import com.robypomper.josp.jcp.params.jslwb.JOSPObjHtml;
import com.robypomper.josp.jcp.params.jslwb.JOSPSrvHtml;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBSrv;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jsl.JSL;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
public class APIJSLWBSrvController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIJSLWBSrv() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIJSLWBSrv.SubGroupService.NAME, APIJSLWBSrv.SubGroupService.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIJSLWBSrv.API_NAME, APIJSLWBSrv.API_VER, JCPFEVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods

    public static JOSPSrvHtml serviceDetails(HttpSession session,
                                             JSLSpringService jslStaticService) {
        // Convert to HTML shared structure
        JSL jsl = jslStaticService.getJSL(jslStaticService.getHttp(session));
        return new JOSPSrvHtml(session, jsl);
    }


    @GetMapping(path = APIJSLWBSrv.FULL_PATH_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<JOSPSrvHtml> jsonServiceDetails(@ApiIgnore HttpSession session) {
        return ResponseEntity.ok(serviceDetails(session, jslService));
    }

}

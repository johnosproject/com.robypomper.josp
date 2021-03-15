package com.robypomper.josp.jcp.jslwebbridge.controllers.jcp;

import com.robypomper.jcpJSLWebBridge.BuildInfoJcpJSLWebBridge;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.paths.jslwb.JCPJSLWBStatus;
import com.robypomper.josp.jcp.service.controllers.jcp.JCPStatusControllerAbs;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.JSLWebBridgeStatus;
import com.robypomper.josp.params.jcp.ServiceStatus;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.plugins.Docket;


@SuppressWarnings("unused")
@RestController
@Api(tags = {com.robypomper.josp.jcp.paths.jslwb.JCPJSLWBStatus.SubGroupJSLWebBridgeStatus.NAME})
public class JCPJSLWBStatusController extends JCPStatusControllerAbs {

    // Internal vars

    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_JCPJSLWBStatus() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(JCPJSLWBStatus.SubGroupJSLWebBridgeStatus.NAME, JCPJSLWBStatus.SubGroupJSLWebBridgeStatus.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(com.robypomper.josp.jcp.paths.jslwb.JCPJSLWBStatus.API_NAME, com.robypomper.josp.jcp.paths.jslwb.JCPJSLWBStatus.API_VER, JCPJSLWBVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }

    // Methods

    @Override
    protected ServiceStatus getInstanceReqSubClass() {
        return new JSLWebBridgeStatus(BuildInfoJcpJSLWebBridge.Current);
    }

}

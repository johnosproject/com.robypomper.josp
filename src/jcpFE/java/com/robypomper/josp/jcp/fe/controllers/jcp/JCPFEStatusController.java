package com.robypomper.josp.jcp.fe.controllers.jcp;

import com.robypomper.jcpFE.BuildInfoJcpFE;
import com.robypomper.josp.jcp.info.JCPFEVersions;
import com.robypomper.josp.jcp.service.controllers.jcp.JCPStatusControllerAbs;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jcp.FEStatus;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.plugins.Docket;


@SuppressWarnings("unused")
@RestController
@Api(tags = {com.robypomper.josp.jcp.paths.fe.JCPFEStatus.SubGroupFEStatus.NAME})
public class JCPFEStatusController extends JCPStatusControllerAbs<FEStatus> {

    // Internal vars

    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_JCPFEStatus() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(com.robypomper.josp.jcp.paths.fe.JCPFEStatus.SubGroupFEStatus.NAME, com.robypomper.josp.jcp.paths.fe.JCPFEStatus.SubGroupFEStatus.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(com.robypomper.josp.jcp.paths.fe.JCPFEStatus.API_NAME, com.robypomper.josp.jcp.paths.fe.JCPFEStatus.API_VER, JCPFEVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods

    @Override
    protected FEStatus getInstanceReqSubClass() {
        return new FEStatus(BuildInfoJcpFE.Current);
    }

}

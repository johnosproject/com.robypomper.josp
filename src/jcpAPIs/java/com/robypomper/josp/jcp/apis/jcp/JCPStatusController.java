package com.robypomper.josp.jcp.apis.jcp;

import com.robypomper.java.JavaVersionUtils;
import com.robypomper.josp.jcp.apis.paths.APIJCPStatus;
import com.robypomper.josp.jcp.info.JCPAPIsVersions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Api(tags = {APIJCPStatus.SubGroupStatus.NAME})
public class JCPStatusController {

    public JCPStatusController() {
        System.out.println(JavaVersionUtils.buildJavaVersionStr("John Cloud Platform", JCPAPIsVersions.VER_JCP_APIs_2_0));
    }

    @GetMapping(path = APIJCPStatus.FULL_PATH_STATUS_PUBLIC)
    @ApiOperation("Public accessible method")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class)
    })
    public String getJCPStatusPublic() {
        return new SimpleDateFormat().format(new Date());
    }

    @GetMapping(path = APIJCPStatus.FULL_PATH_STATUS_FULL)
    @ResponseBody
    @ApiOperation("Public accessible method")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class)
    })
    public String getJCPStatusFull() {
        return JavaVersionUtils.buildJavaVersionStr("John Cloud Platform", JCPAPIsVersions.VER_JCP_APIs_2_0);
    }


}

package com.robypomper.josp.jcp.apis.jcp;

import com.robypomper.java.JavaVersionUtils;
import com.robypomper.josp.jcp.apis.paths.APIJCPStatus;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Api(tags = {APIJCPStatus.SubGroupStatus.NAME})
public class JCPStatusController {

    public JCPStatusController() {
        System.out.println(JavaVersionUtils.buildJavaVersionStr("John Cloud Platform", "VERSION"));
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
        return JavaVersionUtils.buildJavaVersionStr("John Cloud Platform", "VERSION");
    }


}

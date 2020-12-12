package com.robypomper.josp.jcp.fe.controllers.jcp;

import com.robypomper.josp.params.jcp.JCPFEStatus;
import com.robypomper.josp.paths.jcp.APIJCP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIJCP.SubGroupFEStatus.NAME})
public class FEController {

    // Methods

    @GetMapping(path = APIJCP.FULL_PATH_FE_STATUS)
    @ApiOperation(value = "Return JCP FE info and stats")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP FE's info and stats", response = JCPFEStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<JCPFEStatus> getJCPFEStatusReq() {
        return ResponseEntity.ok(new JCPFEStatus());
    }

}

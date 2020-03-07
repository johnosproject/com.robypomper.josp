package com.robypomper.josp.jcp.apis;

import com.robypomper.josp.jcp.info.JCPAPIsGroups;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API class for method examples.
 */
@RestController
@RequestMapping(JCPAPIsGroups.PATH_EXMPL)
@Api(tags = {JCPAPIsGroups.API_EXMPL_SG_METHODS_NAME})
public class APIMethodsController {

    @GetMapping("/str")
    @ApiOperation("Example method that return a string value")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class)
    })
    protected ResponseEntity<String> methodString() {
        return ResponseEntity.ok("String value");
    }

    @GetMapping("/int")
    @ApiOperation("Example method that return a int value")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Integer.class)
    })
    protected ResponseEntity<Integer> methodInt() {
        return ResponseEntity.ok(42);
    }

}

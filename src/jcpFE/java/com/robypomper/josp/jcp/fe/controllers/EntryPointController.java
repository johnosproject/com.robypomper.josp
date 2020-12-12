package com.robypomper.josp.jcp.fe.controllers;

import com.robypomper.josp.params.jcp.JCPAPIsStatus;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
/*@Api(tags = {APIJCP.SubGroupAPIsStatus.NAME})*/ /* Private API used only by FE's javascript code*/
public class EntryPointController {

    public static final String URL = "/entrypoint";


    // Internal vars

    private final boolean useSSL;
    private final String jslWBUrl;


    // Constructor

    @Autowired
    public EntryPointController(@Value("${jcp.client.ssl.public}") boolean useSSL,
                                @Value("${jcp.urlJSLWebBridge}") String jslWBUrl) {
        this.useSSL = useSSL;
        this.jslWBUrl = jslWBUrl;
    }


    // Methods

    @GetMapping(path = URL)
    @ApiOperation(value = "Return JCP FE Backend entry point")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP FE Backend entry point", response = JCPAPIsStatus.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<String> getEntryPointReq() {
        return ResponseEntity.ok((useSSL ? "https://" : "http:") + jslWBUrl);
    }

}

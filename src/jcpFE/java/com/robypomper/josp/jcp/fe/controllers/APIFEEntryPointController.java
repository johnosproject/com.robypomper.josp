package com.robypomper.josp.jcp.fe.controllers;

import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBInit;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("unused")
@RestController
/*@Api(tags = {APIJCP.SubGroupAPIsStatus.NAME})*/ /* Private API used only by FE's javascript code*/
public class APIFEEntryPointController {

    public static final String URL_ENTRYPOINT = "/entrypoint";
    public static final String URL_INIT_JSL_SESSION = "/jslwbsession";


    // Internal vars

    private final String jslWBUrl;
    private final String clientId;
    private final String clientSecret;


    // Constructor

    @Autowired
    public APIFEEntryPointController(@Value("${jcp.urlJSLWebBridge}") String jslWBUrl,
                                     ClientParams jcpAPIsParams) {
        this.jslWBUrl = (jcpAPIsParams.useSSLPublic ? "https://" : "http:") + jslWBUrl;
        this.clientId = jcpAPIsParams.client;
        this.clientSecret = jcpAPIsParams.secret;
    }


    // Methods

    @GetMapping(path = URL_ENTRYPOINT)
    @ApiOperation(value = "Return JCP FE Backend entry point")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP FE Backend entry point url", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<String> getEntrypoint() {
        return ResponseEntity.ok(jslWBUrl);
    }

    @GetMapping(path = URL_INIT_JSL_SESSION)
    @ApiOperation(value = "Send a JSL Init request to JCP JSL WebBridge")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP FE Backend entry point", response = Boolean.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Boolean> initJSLWebBridgeSession(@RequestParam("session_id") String sessionId) {
        try {
            URL url = new URL(jslWBUrl + APIJSLWBInit.FULL_PATH_INIT_JSL + "?client_id=" + clientId + "&client_secret=" + clientSecret);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
            conn.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((reader.readLine()) != null) ;
            reader.close();

            return ResponseEntity.ok(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(false);
    }

}
